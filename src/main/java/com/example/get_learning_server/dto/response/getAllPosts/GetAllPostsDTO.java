package com.example.get_learning_server.dto.response.getAllPosts;

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
public class GetAllPostsDTO extends RepresentationModel<GetAllPostsDTO> {
  private UUID id;
  private String title;
  private String subtitle;
  private String postTime;
  private CoverImageDTO coverImage;
  private AuthorDTO author;
}
