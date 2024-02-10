package com.example.get_learning_server.dto.response.getAllPosts;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.hateoas.RepresentationModel;


import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Posts extends RepresentationModel<Posts> {
  private UUID id;
  private String title;
  private String subtitle;
  private String postTime;
  private CoverImageDTO coverImage;
  private AuthorDTO author;
}
