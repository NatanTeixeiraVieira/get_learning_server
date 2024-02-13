package com.example.get_learning_server.dto.request.updatePost;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class UpdatePostRequestDTO {
  private UUID id;
  private String title;
  private String subtitle;
  private String content;
  private Boolean allowComments;
  @Nullable
  private UUID coverImageId;
  private List<String> categories;
  private List<Tag> tags;

  @Data
  public static class Tag {
    private UUID id;
    private String name;
  }
}
