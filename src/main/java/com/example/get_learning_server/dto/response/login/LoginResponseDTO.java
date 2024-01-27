package com.example.get_learning_server.dto.response.login;

import lombok.Data;

@Data
public class LoginResponseDTO {
  private boolean success;
  private UserLoginResponseDTO user;
  private String token;

  public LoginResponseDTO(UserLoginResponseDTO user, String token) {
    success = true;
    this.user = user;
    this.token = token;
  }
}
