package com.example.get_learning_server.controller;

import com.example.get_learning_server.dto.request.login.LoginRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.dto.response.login.LoginResponseDTO;
import com.example.get_learning_server.dto.response.register.RegisterEmailVerificationResponseDTO;
import com.example.get_learning_server.service.auth.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/v1")
@AllArgsConstructor
public class AuthController {
  private AuthServiceImpl authService;
  @PostMapping("/register/send-email")
  public ResponseEntity<RegisterEmailVerificationResponseDTO> registerSendEmail(
      @RequestBody @Valid RegisterEmailVerificationRequestDTO registerData) {
    return ResponseEntity.ok(authService.registerSendEmailVerification(registerData));
  }

  @PostMapping("/register/confirm-email")
  public ResponseEntity<LoginResponseDTO> registerConfirmEmail(
      @RequestBody @Valid RegisterConfirmEmailRequestDTO registerConfirmEmailRequestDTO,
      @RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(authService.registerConfirmEmail(registerConfirmEmailRequestDTO, token));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO authData) {
    return ResponseEntity.ok(authService.login(authData));
  }
}
