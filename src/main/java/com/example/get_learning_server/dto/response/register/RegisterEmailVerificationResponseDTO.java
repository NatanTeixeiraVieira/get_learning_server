package com.example.get_learning_server.dto.response.register;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
public class RegisterEmailVerificationResponseDTO {
  private Boolean success;
  private String message;
  private String token;
  private UUID emailId;

  public RegisterEmailVerificationResponseDTO(String token, UUID emailId) {
    this.success = true;
    this.message = "Confirmation email sent successful";
    this.token = token;
    this.emailId = emailId;
  }
}