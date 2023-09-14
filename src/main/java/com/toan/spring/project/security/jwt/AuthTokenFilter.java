package com.toan.spring.project.security.jwt;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.toan.spring.project.security.services.UserDetailsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  // xác thực và ủy quyền người dùng
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Sử dụng phương thức parseJwt (đã được triển khai trước đó) để lấy chuỗi JWT
      // từ đối tượng HttpServletRequest.
      String jwt = parseJwt(request);
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);
        // validate jwt và lấy username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // sử dụng đối tượng UserDetailsService để lấy thông tin chi tiết người dùng và
        // tạo một đối tượng UsernamePasswordAuthenticationToken để xác thực người dùng.
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
            null,
            userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }

    filterChain.doFilter(request, response);
  }

  // nhận jwt từ request(HttpServletRequest),phân tích và trả về chuỗi đó
  private String parseJwt(HttpServletRequest request) {
    String jwt = jwtUtils.getJwtFromCookies(request);
    return jwt;
  }
}
