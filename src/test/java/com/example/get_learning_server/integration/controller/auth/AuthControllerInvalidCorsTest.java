package com.example.get_learning_server.integration.controller.auth;

import com.example.get_learning_server.config.TestConfig;
import com.example.get_learning_server.integration.dto.request.login.LoginRequestDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.integration.testcontainers.AbstractIntegrationTest;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerInvalidCorsTest extends AbstractIntegrationTest {
  private final String userEmail = "user@user.com";
  private final String userPassword = "12345678";
  private final String invalidCorsMessage = "Invalid CORS request";

  @MockBean
  private JavaMailSender emailSender;

  @Test
  @Order(1)
  public void registerSendEmailTest() {

    doNothing().when(emailSender).send(any(MimeMessage.class));
    when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

    String userName = "Namê Last Nàme";
    final RegisterEmailVerificationRequestDTO dto =
        new RegisterEmailVerificationRequestDTO(userEmail, userPassword, userName);

    final var content = given()
        .basePath("/api/auth/v1/register/send-email")
        .port(TestConfig.SERVER_PORT)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .body(dto)
        .when()
        .post()
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertEquals(invalidCorsMessage, content);
  }

  @Test
  @Order(2)
  public void registerConfirmEmailTest() {
    final RegisterConfirmEmailRequestDTO dto = new RegisterConfirmEmailRequestDTO(UUID.randomUUID());

    final var content = given()
        .basePath("/api/auth/v1/register/confirm-email")
        .port(TestConfig.SERVER_PORT)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .body(dto)
        .when()
        .post()
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertEquals(invalidCorsMessage, content);
  }

  @Test
  @Order(3)
  public void loginTest() {
    final LoginRequestDTO dto = new LoginRequestDTO(userEmail, userPassword);

    final var content = given()
        .basePath("/api/auth/v1/login")
        .port(TestConfig.SERVER_PORT)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .body(dto)
        .when()
        .post()
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertEquals(invalidCorsMessage, content);
  }
}
