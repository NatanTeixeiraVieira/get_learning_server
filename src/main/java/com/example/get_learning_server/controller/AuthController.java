package com.example.get_learning_server.controller;

import com.example.get_learning_server.dto.request.login.LoginRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.dto.response.login.LoginResponseDTO;
import com.example.get_learning_server.dto.response.register.RegisterEmailVerificationResponseDTO;
import com.example.get_learning_server.dto.response.savePost.SavePostResponseDTO;
import com.example.get_learning_server.service.auth.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/v1")
@AllArgsConstructor
public class AuthController {
  private AuthServiceImpl authService;

  @Operation(
      tags = {"Auth"},
      summary = "Register send email",
      description = "It is the first part of register. " +
          "It creates the account disabled and send one email in order to verify the email veracity " +
          "and it returns the email id and the token to confirm email",
      responses = {
          @ApiResponse(
              description = "Success",
              responseCode = "200",
              content = {@Content(schema = @Schema(implementation = SavePostResponseDTO.class))}
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @PostMapping("/register/send-email")
  public ResponseEntity<RegisterEmailVerificationResponseDTO> registerSendEmail(
      @RequestBody @Valid RegisterEmailVerificationRequestDTO registerData) {
    return ResponseEntity.ok(authService.registerSendEmailVerification(registerData));
  }

  @Operation(
      tags = {"Auth"},
      summary = "Register confirm email",
      description = "It is the second part of register. It enables the account and make the login " +
          "and it returns the same response that in the login",
      responses = {
          @ApiResponse(
              description = "Success",
              responseCode = "200",
              content = {@Content(schema = @Schema(implementation = SavePostResponseDTO.class))}
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @PostMapping("/register/confirm-email")
  public ResponseEntity<LoginResponseDTO> registerConfirmEmail(
      @RequestBody @Valid RegisterConfirmEmailRequestDTO registerConfirmEmailRequestDTO,
      @RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(authService.registerConfirmEmail(registerConfirmEmailRequestDTO, token));
  }

  @Operation(
      tags = {"Auth"},
      summary = "Login",
      description = "It makes the login and it returns the authentication token with user information",
      responses = {
          @ApiResponse(
              description = "Success",
              responseCode = "200",
              content = {@Content(schema = @Schema(implementation = SavePostResponseDTO.class))}
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO authData) {
    return ResponseEntity.ok(authService.login(authData));
  }
}
