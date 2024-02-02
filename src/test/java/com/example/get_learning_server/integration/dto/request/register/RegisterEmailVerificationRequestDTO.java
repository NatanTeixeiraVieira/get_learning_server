package com.example.get_learning_server.integration.dto.request.register;

import com.example.get_learning_server.util.Regex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterEmailVerificationRequestDTO(
    @NotBlank @Email @Pattern(regexp = Regex.regexEmail)
    String login,
    @Size(min = 8, message = "Password must have at least 8 characters") @NotBlank
    String password,
    @NotBlank
    String userName) {
}
