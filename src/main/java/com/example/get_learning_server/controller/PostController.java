package com.example.get_learning_server.controller;

import com.example.get_learning_server.dto.response.getAllPosts.Posts;
import com.example.get_learning_server.dto.response.getPostById.GetPostByIdResponseDTO;
import com.example.get_learning_server.dto.response.savePost.SavePostResponseDTO;
import com.example.get_learning_server.dto.response.updatePost.UpdatePostResponseDTO;
import com.example.get_learning_server.service.post.PostServiceImpl;
import com.example.get_learning_server.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping("/api/post/v1")
@RequiredArgsConstructor
public class PostController {
  private final PostServiceImpl postService;

  @Operation(
      tags = {"Post"},
      summary = "Find all posts",
      description = "It finds all posts, returning an array with all posts found in JSON",
      responses = {
          @ApiResponse(
              description = "Success",
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      array = @ArraySchema(schema = @Schema(implementation = Posts.class))
                  )
              }
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
          @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @GetMapping(produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<PagedModel<EntityModel<Posts>>> findAllPosts(
      @RequestParam(value = "page", defaultValue = "0") Integer page,
      @RequestParam(value = "limit", defaultValue = "12") Integer limit,
      @RequestParam(value = "direction", defaultValue = "asc") String direction
  ) {
    final Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    final Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, "createdAt"));

    return ResponseEntity.ok(postService.findAllPosts(pageable));
  }

  @Operation(
      tags = {"Post"},
      summary = "Find one post by id",
      description = "It finds one post by id returning the post found in JSON",
      responses = {
          @ApiResponse(
              description = "Success",
              responseCode = "200",
              content = {
                  @Content(
                      mediaType = "application/json",
                      array = @ArraySchema(schema = @Schema(implementation = GetPostByIdResponseDTO.class))
                  )
              }
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
          @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<GetPostByIdResponseDTO> findPostById(@PathVariable("id") UUID postId) {
    return ResponseEntity.ok(postService.findPostById(postId));
  }

  @Operation(
      tags = {"Post"},
      summary = "Save one post",
      description = "It saves one post on database and it returns this post in JSON",
      responses = {
          @ApiResponse(
              description = "Success",
              responseCode = "201",
              content = {@Content(schema = @Schema(implementation = SavePostResponseDTO.class))}
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
          @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @PostMapping(produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<SavePostResponseDTO> savePost(
      @RequestParam(name = "coverImageFile") MultipartFile coverImageFile,
      @RequestParam(name = "dto") String dto
  ) throws IOException, URISyntaxException {
    return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(coverImageFile, dto));
  }

  @Operation(
      tags = {"Post"},
      summary = "Update one post",
      description = "It updates one post on database and it returns this post in JSON",
      responses = {
          @ApiResponse(
              description = "Success",
              responseCode = "200",
              content = {@Content(schema = @Schema(implementation = UpdatePostResponseDTO.class))}
          ),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @PutMapping(produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<UpdatePostResponseDTO> updatePost(
      @Nullable @RequestParam(value = "coverImage") MultipartFile coverImage,
      @RequestParam(value = "dto") String dto
  ) throws IOException {
    return ResponseEntity.ok(postService.updatePost(coverImage, dto));
  }

  @Operation(
      tags = {"Post"},
      summary = "Delete one post",
      description = "It deletes one post by id from database and it doesn't returns any content",
      responses = {
          @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
          @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
          @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
          @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
          @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
      }
  )
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deletePost(@PathVariable UUID id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
  }
}
