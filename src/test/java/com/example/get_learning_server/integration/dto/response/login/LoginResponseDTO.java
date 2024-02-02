package com.example.get_learning_server.integration.dto.response.login;

import lombok.Data;

@Data
public class LoginResponseDTO {
  private Boolean success;
  private UserLoginResponseDTO user;
  private String token;

  public LoginResponseDTO(UserLoginResponseDTO user, String token) {
    success = true;
    this.user = user;
    this.token = token;
  }
}
