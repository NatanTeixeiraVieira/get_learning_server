package com.example.get_learning_server.dto.response.register;

import lombok.Data;

@Data
public class RegisterEmailVerificationResponseDTO {
  private final boolean success = true;
  private final String message = "Confirmation email sent successful";
  private final String token;

  public RegisterEmailVerificationResponseDTO(String token) {
    this.token = token;
  }
}