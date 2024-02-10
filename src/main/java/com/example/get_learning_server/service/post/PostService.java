package com.example.get_learning_server.service.post;

import com.example.get_learning_server.dto.response.getAllPosts.Posts;
import com.example.get_learning_server.dto.response.savePost.SavePostResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {
  PagedModel<EntityModel<Posts>> findAllPosts(Pageable pageable);

  SavePostResponseDTO savePost(MultipartFile coverImageFile,
                               String title,
                               String subtitle,
                               String content,
                               String allowComments,
                               String categories,
                               String tags) throws IOException;
}
