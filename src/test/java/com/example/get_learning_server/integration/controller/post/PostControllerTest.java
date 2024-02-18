package com.example.get_learning_server.integration.controller.post;

import com.example.get_learning_server.config.TestConfig;
import com.example.get_learning_server.dto.response.getPostById.GetPostByIdResponseDTO;
import com.example.get_learning_server.dto.response.register.RegisterEmailVerificationResponseDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.integration.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.integration.dto.request.savePost.SavePostRequestDTO;
import com.example.get_learning_server.integration.dto.request.updatePost.UpdatePostRequestDTO;
import com.example.get_learning_server.integration.dto.response.getAllPosts.Posts;
import com.example.get_learning_server.integration.dto.response.login.LoginResponseDTO;
import com.example.get_learning_server.integration.dto.response.savePost.CategoryResponseDTO;
import com.example.get_learning_server.integration.dto.response.savePost.SavePostResponseDTO;
import com.example.get_learning_server.integration.dto.response.savePost.TagResponseDTO;
import com.example.get_learning_server.integration.dto.response.updatePost.UpdatePostResponseDTO;
import com.example.get_learning_server.integration.dto.response.wrapper.Wrapper;
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
public class PostControllerTest extends AbstractIntegrationTest {
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
        new RegisterEmailVerificationRequestDTO("testinho@email.com", Constants.userPassword, Constants.userName);

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

    var content = given()
        .spec(specification)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .multiPart("coverImageFile", file)
        .multiPart(new MultiPartSpecBuilder(dto).controlName("dto").charset(StandardCharsets.UTF_8).build())
        .when()
        .post()
        .then()
        .statusCode(201)
        .extract()
        .body()
        .asString();

    final SavePostResponseDTO postSaved = objectMapper.readValue(content, SavePostResponseDTO.class);
    postId = postSaved.getId();
    coverImageId = postSaved.getCoverImageId();

    assertNotNull(postSaved.getId());

    assertEquals("Title", postSaved.getTitle());
    assertEquals("Subtitle", postSaved.getSubtitle());
    assertEquals("<h1>Content</h1>", postSaved.getContent());

    assertNotNull(postSaved.getCoverImageId());
    assertNotNull(postSaved.getAuthorId());

    final CategoryResponseDTO firstCategory = postSaved.getCategories().get(0);
    assertNotNull(firstCategory.getId());
    assertEquals("Viagem", firstCategory.getName());
    assertEquals("viagem", firstCategory.getSlug());

    final CategoryResponseDTO secondCategory = postSaved.getCategories().get(1);
    assertNotNull(secondCategory.getId());
    assertEquals("Automóveis", secondCategory.getName());
    assertEquals("automoveis", secondCategory.getSlug());

    final TagResponseDTO firstTag = postSaved.getTags().get(0);
    assertNotNull(firstTag.getId());
    assertEquals("Avião", firstTag.getName());
    assertEquals("aviao", firstTag.getSlug());
    firstTagId = firstTag.getId();

    final TagResponseDTO secondTag = postSaved.getTags().get(1);
    assertNotNull(secondTag.getId());
    assertEquals("Países", secondTag.getName());
    assertEquals("paises", secondTag.getSlug());
    secondTagId = secondTag.getId();

    assertTrue(postSaved.getAllowComments());
  }

  @Test
  @Order(2)
  public void testFindPostById() throws JsonProcessingException {
    var content = given()
        .spec(specification)
        .contentType(TestConfig.CONTENT_TYPE_JSON)
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .pathParams("id", postId)
        .when()
        .get("{id}")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .asString();

    GetPostByIdResponseDTO postFound = objectMapper.readValue(content, GetPostByIdResponseDTO.class);

    assertNotNull(postFound.getId());
    assertNotNull(postFound.getPostTime());
    assertNotNull(postFound.getAuthor().getId());
    assertNotNull(postFound.getCoverImage().getId());

    assertEquals("Title", postFound.getTitle());
    assertEquals("Subtitle", postFound.getSubtitle());
    assertEquals("<h1>Content</h1>", postFound.getContent());
    assertEquals(Constants.userName, postFound.getAuthor().getName());
    assertEquals(Constants.userSlug, postFound.getAuthor().getSlug());
    assertEquals("coverImage/blob-test", postFound.getCoverImage().getName());

    final GetPostByIdResponseDTO.CategoryDTO firstCategory = postFound.getCategories().get(0);
    assertNotNull(firstCategory.getId());
    assertEquals("Automóveis", firstCategory.getName());
    assertEquals("automoveis", firstCategory.getSlug());

    final GetPostByIdResponseDTO.CategoryDTO secondCategory = postFound.getCategories().get(1);
    assertNotNull(secondCategory.getId());
    assertEquals("Viagem", secondCategory.getName());
    assertEquals("viagem", secondCategory.getSlug());

    final GetPostByIdResponseDTO.TagDTO firstTag = postFound.getTags().get(0);
    assertNotNull(firstTag.getId());
    assertEquals("Avião", firstTag.getName());
    assertEquals("aviao", firstTag.getSlug());

    final GetPostByIdResponseDTO.TagDTO secondTag = postFound.getTags().get(1);
    assertNotNull(secondTag.getId());
    assertEquals("Países", secondTag.getName());
    assertEquals("paises", secondTag.getSlug());

    assertTrue(postFound.getAllowComments());
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
        .statusCode(200)
        .extract()
        .body()
        .asString();

    Wrapper postFound = objectMapper.readValue(content, Wrapper.class);
    List<Posts> posts = postFound.getEmbedded().getPostsList();
    System.out.println(posts);

    assertEquals(12, posts.size());

    posts.forEach((post) -> {
      assertNotNull(post.getId());
      assertNotNull(post.getTitle());
      assertNotNull(post.getSubtitle());
      assertNotNull(post.getPostTime());
      assertNotNull(post.getCoverImage().getId());
      assertNotNull(post.getCoverImage().getName());
      assertNotNull(post.getCoverImage().getUrl());
      assertNotNull(post.getAuthor().getId());
      assertNotNull(post.getAuthor().getName());
      assertNotNull(post.getAuthor().getSlug());
    });

    final Posts firstPost = posts.get(0);
    assertEquals("Título do post", firstPost.getTitle());
    assertEquals("Subtítulo do post", firstPost.getSubtitle());
    assertEquals("2024-02-17T17:25:08.225217", firstPost.getPostTime());
    assertTrue(firstPost.getCoverImage().getName().contains("coverImage/"));
    assertEquals(firstPost.getAuthor().getName(), "Teste");
    assertEquals(firstPost.getAuthor().getSlug(), "teste");

    final Posts middlePost = posts.get(6);
    assertEquals("Título do post", middlePost.getTitle());
    assertEquals("Subtítulo do post", middlePost.getSubtitle());
    assertEquals("2024-02-17T17:25:08.225217", middlePost.getPostTime());
    assertTrue(middlePost.getCoverImage().getName().contains("coverImage/"));
    assertEquals(middlePost.getAuthor().getName(), "Teste");
    assertEquals(middlePost.getAuthor().getSlug(), "teste");

    final Posts lastPost = posts.get(posts.size() - 1);
    assertEquals("Título do post", lastPost.getTitle());
    assertEquals("Subtítulo do post", lastPost.getSubtitle());
    assertEquals("2024-02-17T17:25:08.225217", lastPost.getPostTime());
    assertTrue(lastPost.getCoverImage().getName().contains("coverImage/"));
    assertEquals(lastPost.getAuthor().getName(), "Teste");
    assertEquals(lastPost.getAuthor().getSlug(), "teste");
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
        .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
        .multiPart("coverImageFile", file)
        .multiPart(new MultiPartSpecBuilder(dto).controlName("dto").charset(StandardCharsets.UTF_8).build())
        .when()
        .put()
        .then()
        .statusCode(200)
        .extract()
        .body()
        .asString();

    final UpdatePostResponseDTO postSaved = objectMapper.readValue(content, UpdatePostResponseDTO.class);

    assertNotNull(postSaved.getId());

    assertEquals("Title novo", postSaved.getTitle());
    assertEquals("Subtitle novo", postSaved.getSubtitle());
    assertEquals("<h1>Content novo</h1>", postSaved.getContent());
    assertEquals(coverImageId, postSaved.getCoverImageId());
    assertEquals(postId, postSaved.getId());

    assertNotNull(postSaved.getCoverImageId());
    assertNotNull(postSaved.getAuthorId());


    final UpdatePostResponseDTO.CategoryResponseDTO firstCategory = postSaved.getCategories().get(0);
    assertNotNull(firstCategory.getId());
    assertEquals("Entretenimento", firstCategory.getName());
    assertEquals("entretenimento", firstCategory.getSlug());

    final UpdatePostResponseDTO.CategoryResponseDTO secondCategory = postSaved.getCategories().get(1);
    assertNotNull(secondCategory.getId());
    assertEquals("Música", secondCategory.getName());
    assertEquals("musica", secondCategory.getSlug());

    final UpdatePostResponseDTO.TagResponseDTO firstTag = postSaved.getTags().get(0);
    assertNotNull(firstTag.getId());
    assertEquals("Nova Tag primeira", firstTag.getName());
    assertEquals("nova-tag-primeira", firstTag.getSlug());

    final UpdatePostResponseDTO.TagResponseDTO secondTag = postSaved.getTags().get(1);
    assertNotNull(secondTag.getId());
    assertEquals("Nova Tag segunda", secondTag.getName());
    assertEquals("nova-tag-segunda", secondTag.getSlug());

    assertFalse(postSaved.getAllowComments());
  }

  @Test
  @Order(5)
  public void testDeletePost() {
    given()
      .spec(specification)
      .header(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.RIGHT_ORIGIN)
      .pathParams("id", postId)
      .when()
      .delete("{id}")
      .then()
      .statusCode(204);
  }
}
