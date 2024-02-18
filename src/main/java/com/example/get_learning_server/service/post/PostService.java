package com.example.get_learning_server.service.post;

import com.example.get_learning_server.dto.response.getAllPosts.Posts;
import com.example.get_learning_server.dto.response.getPostById.GetPostByIdResponseDTO;
import com.example.get_learning_server.dto.response.savePost.SavePostResponseDTO;
import com.example.get_learning_server.dto.response.updatePost.UpdatePostResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface PostService {
  PagedModel<EntityModel<Posts>> findAllPosts(Pageable pageable);

  GetPostByIdResponseDTO findPostById(UUID postId);

  SavePostResponseDTO savePost(MultipartFile coverImageFile, String dto) throws IOException;

  UpdatePostResponseDTO updatePost(MultipartFile coverImageFile, String dto) throws IOException;

  void deletePost(UUID postId);
}
