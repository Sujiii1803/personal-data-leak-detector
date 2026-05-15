package com.suji.dataleakdetector.service;

import com.suji.dataleakdetector.dto.AuthResponse;
import com.suji.dataleakdetector.dto.LoginRequest;
import com.suji.dataleakdetector.dto.RegisterRequest;
import com.suji.dataleakdetector.entity.User;
import com.suji.dataleakdetector.exception.ConflictException;
import com.suji.dataleakdetector.repository.UserRepository;
import com.suji.dataleakdetector.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  @Transactional
  public void register(RegisterRequest request) {
    String email = request.getEmail().trim().toLowerCase();
    if (userRepository.existsByEmail(email)) {
      throw new ConflictException("Email already registered");
    }

    User user = new User();
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);
  }

  public AuthResponse login(LoginRequest request) {
    try {
      Authentication auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.getEmail().trim().toLowerCase(), request.getPassword()));
      UserDetails principal = (UserDetails) auth.getPrincipal();
      String token = jwtUtil.generateToken(principal);
      return new AuthResponse(token, principal.getUsername());
    } catch (org.springframework.security.core.AuthenticationException ex) {
      throw new BadCredentialsException("Invalid email or password");
    }
  }
}

