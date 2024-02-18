package com.example.get_learning_server.integration.controller.post;

import com.example.get_learning_server.config.TestConfig;
import com.example.get_learning_server.integration.dto.request.savePost.SavePostRequestDTO;
import com.example.get_learning_server.integration.dto.request.updatePost.UpdatePostRequestDTO;
import com.example.get_learning_server.integration.exeption.ExceptionResponse;
import com.example.get_learning_server.integration.testcontainers.AbstractIntegrationTest;
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
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostControllerInvalidTokenTest extends AbstractIntegrationTest {
  private static final RequestSpecification specification = new RequestSpecBuilder()
      .setBasePath("/api/person/v1")
      .setPort(TestConfig.SERVER_PORT)
      .addFilter(new RequestLoggingFilter(LogDetail.ALL))
      .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
      .build();
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
  private ImageServiceImpl imageService;

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

    final ExceptionResponse content = given()
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .multiPart("coverImageFile", file)
        .multiPart(new MultiPartSpecBuilder(dto).controlName("dto").charset(StandardCharsets.UTF_8).build())
        .when()
        .post()
        .then()
        .statusCode(401)
        .extract()
        .body()
        .as(ExceptionResponse.class);

    assertNotNull(content.getTimestamp());
    assertNotNull(content.getDescription());

    assertFalse(content.getSuccess());

    assertEquals("Invalid token", content.getMessage());
    assertEquals(401, content.getStatusCode());
    assertEquals("Unauthorized", content.getStatus());
  }

  @Test
  @Order(2)
  public void testFindPostById() {
    var content = given()
        .spec(specification)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .pathParams("id", "8da648d2-625a-40d5-83fe-2fd21c413a01")
        .when()
        .get("{id}")
        .then()
        .statusCode(401)
        .extract()
        .body()
        .as(ExceptionResponse.class);

    assertNotNull(content.getTimestamp());
    assertNotNull(content.getDescription());

    assertFalse(content.getSuccess());

    assertEquals("Invalid token", content.getMessage());
    assertEquals(401, content.getStatusCode());
    assertEquals("Unauthorized", content.getStatus());
  }

  @Test
  @Order(3)
  public void testFindAll() throws JsonProcessingException {
    var content = given()
        .spec(specification)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .when()
        .get()
        .then()
        .statusCode(401)
        .extract()
        .body()
        .as(ExceptionResponse.class);

    assertNotNull(content.getTimestamp());
    assertNotNull(content.getDescription());

    assertFalse(content.getSuccess());

    assertEquals("Invalid token", content.getMessage());
    assertEquals(401, content.getStatusCode());
    assertEquals("Unauthorized", content.getStatus());
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

    final ExceptionResponse content = given()
        .spec(specification)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .multiPart("coverImageFile", file)
        .multiPart(new MultiPartSpecBuilder(dto).controlName("dto").charset(StandardCharsets.UTF_8).build())
        .when()
        .put()
        .then()
        .statusCode(401)
        .extract()
        .body()
        .as(ExceptionResponse.class);

    assertNotNull(content.getTimestamp());
    assertNotNull(content.getDescription());

    assertFalse(content.getSuccess());

    assertEquals("Invalid token", content.getMessage());
    assertEquals(401, content.getStatusCode());
    assertEquals("Unauthorized", content.getStatus());
  }

  @Test
  @Order(5)
  public void testDeletePost() {
    final ExceptionResponse content = given()
        .spec(specification)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .pathParams("id", "8da648d2-625a-40d5-83fe-2fd21c413a01")
        .when()
        .delete("{id}")
        .then()
        .statusCode(401)
        .extract()
        .body()
        .as(ExceptionResponse.class);

    assertNotNull(content.getTimestamp());
    assertNotNull(content.getDescription());

    assertFalse(content.getSuccess());

    assertEquals("Invalid token", content.getMessage());
    assertEquals(401, content.getStatusCode());
    assertEquals("Unauthorized", content.getStatus());
  }
}
