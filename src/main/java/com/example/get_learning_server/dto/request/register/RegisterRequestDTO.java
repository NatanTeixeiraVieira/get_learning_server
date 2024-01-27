package com.example.get_learning_server.dto.request.register;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record RegisterRequestDTO(UUID emailId) {
}
