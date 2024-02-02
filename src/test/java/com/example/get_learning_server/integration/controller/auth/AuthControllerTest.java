package com.example.get_learning_server.integration.controller.auth;

import com.example.get_learning_server.config.TestConfig;
import com.example.get_learning_server.dto.response.register.RegisterEmailVerificationResponseDTO;
import com.example.get_learning_server.integration.dto.request.login.LoginRequestDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.integration.dto.response.login.LoginResponseDTO;
import com.example.get_learning_server.integration.testcontainers.AbstractIntegrationTest;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest extends AbstractIntegrationTest {
  private static String token;
  private static UUID emailId;
  private final String userEmail = "user@user.com";
  private final String userName = "Namê Last Nàme";
  private final String userSlug = "name-last-name";
  private final String userPassword = "12345678";

  @MockBean
  private JavaMailSender emailSender;

  @Test
  @Order(1)
  public void registerSendEmailTest() {

      doNothing().when(emailSender).send(any(MimeMessage.class));
      when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

      final RegisterEmailVerificationRequestDTO dto =
          new RegisterEmailVerificationRequestDTO(userEmail, userPassword, userName);

      final var content = given()
          .basePath("/api/auth/v1/register/send-email")
          .port(TestConfig.SERVER_PORT)
          .contentType(TestConfig.CONTENT_TYPE_JSON)
          .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
          .body(dto)
          .when()
          .post()
          .then()
          .statusCode(200)
          .extract()
          .body()
          .as(RegisterEmailVerificationResponseDTO.class);
      token = content.getToken();
      emailId = content.getEmailId();

      assertNotNull(content.getToken());
      assertNotNull(content.getEmailId());
      assertEquals("Confirmation email sent successful", content.getMessage());
      assertEquals(true, content.getSuccess());
  }

  @Test
  @Order(2)
  public void registerConfirmEmailTest() {
    final RegisterConfirmEmailRequestDTO dto = new RegisterConfirmEmailRequestDTO(emailId);

    final var content = given()
        .basePath("/api/auth/v1/register/confirm-email")
        .port(TestConfig.SERVER_PORT)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .header(TestConfig.HEADER_PARAM_AUTHORIZATION, token)
        .body(dto)
        .when()
        .post()
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(LoginResponseDTO.class);

    assertEquals(true, content.getSuccess());

    assertNotNull(content.getUser().id());
    assertEquals(userEmail, content.getUser().login());
    assertEquals(userName, content.getUser().userName());
    assertEquals(userSlug, content.getUser().userSlug());

    assertNotNull(content.getToken());
  }

  @Test
  @Order(3)
  public void loginTest() {
    final LoginRequestDTO dto = new LoginRequestDTO(userEmail, userPassword);

    final var content = given()
        .basePath("/api/auth/v1/login")
        .port(TestConfig.SERVER_PORT)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .body(dto)
        .when()
        .post()
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(LoginResponseDTO.class);

    assertEquals(true, content.getSuccess());

    assertNotNull(content.getUser().id());
    assertEquals(userEmail, content.getUser().login());
    assertEquals(userName, content.getUser().userName());
    assertEquals(userSlug, content.getUser().userSlug());

    assertNotNull(content.getToken());
  }
}
