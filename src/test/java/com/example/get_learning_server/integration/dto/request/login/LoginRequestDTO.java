package com.example.get_learning_server.integration.dto.request.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
    @NotBlank @Email @Size(min = 8, message = "Password must have at least 8 characters")
    String login,
    @NotBlank
    String password) {
}