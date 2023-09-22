package com.toan.spring.project.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toan.spring.project.models.Role;
import com.toan.spring.project.models.User;
import com.toan.spring.project.payload.request.LoginRequest;
import com.toan.spring.project.payload.request.SignupRequest;
import com.toan.spring.project.payload.response.StringResponse;
import com.toan.spring.project.repository.RoleRepository;
import com.toan.spring.project.repository.UserRepository;
import com.toan.spring.project.security.jwt.JwtUtils;
import com.toan.spring.project.security.services.UserDetailsImpl;

import io.micrometer.common.util.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      if (StringUtils.isBlank(loginRequest.getUsername())
          || StringUtils.isBlank(loginRequest.getPassword())) {
        return ResponseEntity.badRequest().body(new StringResponse(1, "Thiếu thông tin đăng nhập"));
      }
      if (containsUpperCase(loginRequest.getUsername()) || containsSpecialCharacters(loginRequest.getPassword())) {
        return ResponseEntity.badRequest().body(new StringResponse(2, "Username và password không hợp lệ"));
      }
      Authentication authentication = authenticationManager
          .authenticate(
              new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

      ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

      // gắn jwt vào header
      StringResponse response = new StringResponse(0, "Đăng nhập thành công");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
          .body(response);
    } catch (BadCredentialsException e) {
      // 401
      return ResponseEntity.ok(new StringResponse(3, "Sai username hoặc password"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new StringResponse(4, "Thực hiện thất bại: " + e.getMessage()));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    try {
      if (StringUtils.isBlank(signUpRequest.getUsername()) || StringUtils.isBlank(signUpRequest.getEmail())
          || StringUtils.isBlank(signUpRequest.getPassword()) || StringUtils.isBlank(signUpRequest.getName())) {
        return ResponseEntity.badRequest().body(new StringResponse(1, "Thiếu thông tin đăng kí"));
      }
      // validate
      if (containsUpperCase(signUpRequest.getUsername()) || containsSpecialCharacters(signUpRequest.getPassword())) {
        return ResponseEntity.badRequest().body(new StringResponse(2, "Username và password không hợp lệ"));
      }
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
        return ResponseEntity.badRequest().body(new StringResponse(3, "Username đã được sử dụng"));
      }
      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity.badRequest().body(new StringResponse(4, "Email đã được sử dụng"));
      }

      // Create new user's account
      User user = new User(signUpRequest.getUsername(),
          signUpRequest.getEmail(), signUpRequest.getName(),
          encoder.encode(signUpRequest.getPassword()));

      Set<String> strRoles = signUpRequest.getRole();
      Set<Role> roles = new HashSet<>();

      if (strRoles == null) {
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
      } else {
        strRoles.forEach(role -> {
          switch (role) {
            case "admin":
              Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(adminRole);
              break;
            default:
              Role userRole = roleRepository.findByName("ROLE_USER")
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(userRole);
          }
        });
      }

      user.setRoles(roles);
      userRepository.save(user);

      return ResponseEntity.ok(new StringResponse(0, "Đăng ký thành công"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new StringResponse(5, "Thực hiện thất bại: " + e.getMessage()));
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    try {
      ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
          .body(new StringResponse(0, "Bạn đã đăng xuất!"));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new StringResponse(1, "Thực hiện thất bại: " + e.getMessage()));
    }

  }

  // Hàm kiểm tra xem một chuỗi có chứa chữ hoa không
  private boolean containsUpperCase(String str) {
    for (char c : str.toCharArray()) {
      if (Character.isUpperCase(c)) {
        return true;
      }
    }
    return false;
  }

  // Hàm kiểm tra xem một chuỗi có chứa ký tự đặc biệt không
  private boolean containsSpecialCharacters(String str) {
    String specialCharacters = "!@#$%^&*()_+[]{}|;':,.<>?";
    for (char c : str.toCharArray()) {
      if (specialCharacters.contains(String.valueOf(c))) {
        return true;
      }
    }
    return false;
  }
}
