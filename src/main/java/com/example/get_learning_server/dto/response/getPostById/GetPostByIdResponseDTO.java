package com.example.get_learning_server.dto.response.getPostById;

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
public class GetPostByIdResponseDTO extends RepresentationModel<GetPostByIdResponseDTO> {
  private UUID id;
  private Boolean allowComments;
  private String content;
  private String title;
  private String subtitle;
  private LocalDateTime postTime;
  private AuthorDTO author;
  private CoverImageDTO coverImage;
  private List<CategoryDTO> categories;
  private List<TagDTO> tags;

  @NoArgsConstructor
  @Data
  public static class CategoryDTO {
    private UUID id;
    private String name;
    private String slug;
  }

  @NoArgsConstructor
  @Data
  public static class TagDTO {
    private UUID id;
    private String name;
    private String slug;
  }

  @NoArgsConstructor
  @Data
  public static class AuthorDTO {
    private UUID id;
    private String name;
    private String slug;
    private AuthorImageDTO authorImage;

    @NoArgsConstructor
    @Data
    public static class AuthorImageDTO {
      private UUID id;
      private String name;
      private String url;
    }
  }

  @NoArgsConstructor
  @Data
  public static class CoverImageDTO {
    private UUID id;
    private String name;
    private String url;
  }
}
