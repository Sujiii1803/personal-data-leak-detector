package com.suji.dataleakdetector.security;

import com.suji.dataleakdetector.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final CustomUserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
    this.jwtUtil = jwtUtil;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = extractBearerToken(request);
    if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        String username = jwtUtil.extractUsername(token);
        if (username != null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          if (jwtUtil.isTokenValid(token, userDetails)) {
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
          }
        }
      } catch (Exception ignored) {
        // Invalid/expired token -> continue without authentication
      }
    }

    filterChain.doFilter(request, response);
  }

  private String extractBearerToken(HttpServletRequest request) {
    String auth = request.getHeader("Authorization");
    if (auth == null) return null;
    if (!auth.startsWith("Bearer ")) return null;
    String token = auth.substring(7).trim();
    return token.isEmpty() ? null : token;
  }
}

