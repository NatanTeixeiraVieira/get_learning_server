package com.example.get_learning_server.dto.request.register;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterEmailVerificationRequestDTO(@NotBlank @Email String login,
                                                  @NotBlank String password,
                                                  @NotBlank String userName) {
}
