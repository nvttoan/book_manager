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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toan.spring.project.dto.LoginResponseDto;
import com.toan.spring.project.models.Role;
import com.toan.spring.project.models.User;
import com.toan.spring.project.payload.request.LoginRequest;
import com.toan.spring.project.payload.request.SignupRequest;
import com.toan.spring.project.payload.response.MessageResponse;
import com.toan.spring.project.payload.response.UserInfoResponse;
import com.toan.spring.project.repository.RoleRepository;
import com.toan.spring.project.repository.UserRepository;
import com.toan.spring.project.security.jwt.JwtUtils;
import com.toan.spring.project.security.services.UserDetailsImpl;

//for Angular Client (withCredentials)
//@CrossOrigin(origins = "http://localhost:8081", maxAge = 3600, allowCredentials="true")
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

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
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
      LoginResponseDto response = new LoginResponseDto("Đăng nhập thành công với thông tin sau:", new UserInfoResponse(
          userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getName(), roles));

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
          .body(response);
    } catch (BadCredentialsException e) {
      // 401
      return ResponseEntity.ok(new MessageResponse("Error: Sai username hoặc password"));
    } catch (Exception e) {
      return ResponseEntity.ok(new MessageResponse("Error: Đăng nhập thất bại"));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    try {// validate
      if (containsUpperCase(signUpRequest.getUsername()) || containsSpecialCharacters(signUpRequest.getPassword())) {
        return ResponseEntity.badRequest().body(new MessageResponse(
            "Error: Username và password không viết hoa hoặc chứa kí tự đặc biệt"));
      }
      if (userRepository.existsByUsername(signUpRequest.getUsername())) {
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Username đã được sử dụng"));
      }

      if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email đã được sử dụng"));
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

      return ResponseEntity.ok(new MessageResponse("Đăng kí thành công"));
    } catch (Exception e) {
      return ResponseEntity.ok(new MessageResponse("Đăng kí thất bại"));
    }

  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    try {
      ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
          .body(new MessageResponse("Bạn đã đăng xuất!"));
    } catch (Exception e) {
      return ResponseEntity.ok(new MessageResponse("Error: Đăng xuất thất bại"));
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
