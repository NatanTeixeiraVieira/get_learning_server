package com.example.get_learning_server.dto.response.updatePost;

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
public class UpdatePostResponseDTO extends RepresentationModel<UpdatePostResponseDTO> {
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

  @Data
  public static class CategoryResponseDTO {
    private UUID id;
    private String name;
    private String slug;
  }

  @Data
  public static class TagResponseDTO {
    private UUID id;
    private String name;
    private String slug;
  }
}
