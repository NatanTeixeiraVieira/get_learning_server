package com.example.get_learning_server.integration.controller.post;

import com.example.get_learning_server.config.TestConfig;
import com.example.get_learning_server.dto.response.register.RegisterEmailVerificationResponseDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.integration.dto.request.savePost.SavePostRequestDTO;
import com.example.get_learning_server.integration.dto.request.updatePost.UpdatePostRequestDTO;
import com.example.get_learning_server.integration.dto.response.login.LoginResponseDTO;
import com.example.get_learning_server.integration.testcontainers.AbstractIntegrationTest;
import com.example.get_learning_server.integration.utils.Constants;
import com.example.get_learning_server.service.image.ImageServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.storage.BlobInfo;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostControllerInvalidCorsTest extends AbstractIntegrationTest {
  private static RequestSpecification specification;
  private static ObjectMapper objectMapper;
  private static UUID postId;
  private static UUID coverImageId;
  private static UUID firstTagId;
  private static UUID secondTagId;

  @BeforeAll
  public static void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.registerModule(new JavaTimeModule());
  }

  @MockBean
  private JavaMailSender emailSender;

  @MockBean
  private ImageServiceImpl imageService;

  @Test
  @Order(0)
  public void authorization() {
    doNothing().when(emailSender).send(any(MimeMessage.class));
    when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

    final RegisterEmailVerificationRequestDTO sendEmailDto =
        new RegisterEmailVerificationRequestDTO(Constants.userEmail, Constants.userPassword, Constants.userName);

    final var content = given()
        .basePath("/api/auth/v1/register/send-email")
        .port(TestConfig.SERVER_PORT)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .body(sendEmailDto)
        .when()
        .post()
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(RegisterEmailVerificationResponseDTO.class);

    final RegisterConfirmEmailRequestDTO emailVerificationDto =
        new RegisterConfirmEmailRequestDTO(content.getEmailId());

    final var response = given()
        .basePath("/api/auth/v1/register/confirm-email")
        .port(TestConfig.SERVER_PORT)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .header(TestConfig.HEADER_PARAM_AUTHORIZATION, content.getToken())
        .body(emailVerificationDto)
        .when()
        .post()
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(LoginResponseDTO.class);

    specification = new RequestSpecBuilder()
        .addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + response.getToken())
        .setBasePath("/api/post/v1")
        .setPort(TestConfig.SERVER_PORT)
        .addFilter(new RequestLoggingFilter(LogDetail.ALL))
        .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
        .build();
  }

  @Test
  @Order(1)
  public void testCreatePost() throws IOException {
    final List<String> categories = Arrays.asList("Viagem", "Automóveis");
    final List<String> tags = Arrays.asList("Avião", "Países");
    final SavePostRequestDTO dto = new SavePostRequestDTO(
        "Title",
        "Subtitle",
        "<h1>Content</h1>",
        true,
        categories,
        tags
    );

    final File file = new File(System.getProperty("user.dir") + "/src/test/resources/images/book.jpg");

    final String bucketName = System.getenv("FIREBASE_STORAGE_BUCKET");

    final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, "coverImage/blob-test").build();
    when(imageService.uploadImage(any(MultipartFile.class), eq("coverImage/"))).thenReturn(blobInfo);

    final String content = given()
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .multiPart("coverImageFile", file)
        .multiPart(new MultiPartSpecBuilder(dto).controlName("dto").charset(StandardCharsets.UTF_8).build())
        .when()
        .post()
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content);
  }

  @Test
  @Order(2)
  public void testFindPostById() {
    var content = given()
        .spec(specification)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .pathParams("id", "8da648d2-625a-40d5-83fe-2fd21c413a01")
        .when()
        .get("{id}")
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content);
  }

  @Test
  @Order(3)
  public void testFindAll() throws JsonProcessingException {
    final String content = given()
        .spec(specification)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .when()
        .get()
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content);
  }

  @Test
  @Order(4)
  public void testUpdatePost() throws IOException {
    final List<String> categories = Arrays.asList("Entretenimento", "Música");
    final List<UpdatePostRequestDTO.Tag> tags = List.of(
        new UpdatePostRequestDTO.Tag(firstTagId, "Nova Tag primeira"),
        new UpdatePostRequestDTO.Tag(secondTagId, "Nova Tag segunda")
    );
    final UpdatePostRequestDTO dto = new UpdatePostRequestDTO(
        postId,
        "Title novo",
        "Subtitle novo",
        "<h1>Content novo</h1>",
        false,
        coverImageId,
        categories,
        tags
    );

    final File file = new File(System.getProperty("user.dir") + "/src/test/resources/images/night.jpg");

    final String bucketName = System.getenv("FIREBASE_STORAGE_BUCKET");

    final BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, "coverImage/blob-test").build();
    when(imageService.uploadImage(any(MultipartFile.class), eq("coverImage/"))).thenReturn(blobInfo);

    final String content = given()
        .spec(specification)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .multiPart("coverImageFile", file)
        .multiPart(new MultiPartSpecBuilder(dto).controlName("dto").charset(StandardCharsets.UTF_8).build())
        .when()
        .put()
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content);
  }

  @Test
  @Order(5)
  public void testDeletePost() {
    final String content = given()
        .spec(specification)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.WRONG_ORIGIN)
        .pathParams("id", "8da648d2-625a-40d5-83fe-2fd21c413a01")
        .when()
        .delete("{id}")
        .then()
        .statusCode(403)
        .extract()
        .body()
        .asString();

    assertNotNull(content);
    assertEquals("Invalid CORS request", content);
  }
}
