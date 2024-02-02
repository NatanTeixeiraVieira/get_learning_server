package com.example.get_learning_server.dto.request.register;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterConfirmEmailRequestDTO(@NotNull UUID emailId) {
}
