package com.example.get_learning_server.dto.response.getAllPosts;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class CoverImageDTO {
  private UUID id;
  private String name;
  private String url;
}
