package com.example.get_learning_server.dto.response.login;

import java.util.UUID;

public record UserLoginResponseDTO(UUID id, String login, String userName, String userSlug) {
}
