package com.example.get_learning_server.integration.dto.response.savePost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class SavePostResponseDTO extends RepresentationModel<SavePostResponseDTO> {
  private UUID id;
  private Boolean allowComments;
  private String content;
  private String title;
  private String subtitle;
  private UUID authorId;
  private UUID coverImageId;
  private LocalDateTime postTime;
  private List<CategoryResponseDTO> categories;
  private List<TagResponseDTO> tags;
}
