package com.example.get_learning_server.controller;

import com.example.get_learning_server.dto.response.getAllPosts.Posts;
import com.example.get_learning_server.dto.response.getPostById.GetPostByIdResponseDTO;
import com.example.get_learning_server.dto.response.savePost.SavePostResponseDTO;
import com.example.get_learning_server.dto.response.updatePost.UpdatePostResponseDTO;
import com.example.get_learning_server.service.post.PostServiceImpl;
import com.example.get_learning_server.util.MediaType;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/post/v1")
@RequiredArgsConstructor
public class PostController {
  private final PostServiceImpl postService;

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

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<GetPostByIdResponseDTO> findPostById(@PathVariable("id") UUID postId) {
    return ResponseEntity.ok(postService.findPostById(postId));
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<SavePostResponseDTO> savePost(
      @RequestParam(name = "coverImageFile") MultipartFile coverImageFile,
      @RequestParam(name = "dto") String dto
  ) throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(coverImageFile, dto));
  }

  @PutMapping(produces = MediaType.APPLICATION_JSON)
  public ResponseEntity<UpdatePostResponseDTO> updatePost(
      @Nullable @RequestParam(value = "coverImage") MultipartFile coverImage,
      @RequestParam(value = "dto") String dto
  ) throws IOException {
    return ResponseEntity.ok(postService.updatePost(coverImage, dto));
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deletePost(@PathVariable UUID id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
  }
}
