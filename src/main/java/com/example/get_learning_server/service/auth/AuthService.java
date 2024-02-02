package com.example.get_learning_server.service.auth;

import com.example.get_learning_server.dto.request.login.LoginRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.dto.response.login.LoginResponseDTO;
import com.example.get_learning_server.dto.response.register.RegisterEmailVerificationResponseDTO;
import jakarta.validation.Valid;

public interface AuthService {
  RegisterEmailVerificationResponseDTO registerSendEmailVerification(RegisterEmailVerificationRequestDTO registerData);
  LoginResponseDTO registerConfirmEmail(@Valid RegisterConfirmEmailRequestDTO registerConfirmEmailRequestDTO, String token);
  LoginResponseDTO login(@Valid LoginRequestDTO authData);
}
