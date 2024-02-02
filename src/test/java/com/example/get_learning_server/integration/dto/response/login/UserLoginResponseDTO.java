package com.example.get_learning_server.integration.dto.response.login;

import java.util.UUID;

public record UserLoginResponseDTO(UUID id, String login, String userName, String userSlug) {
}
