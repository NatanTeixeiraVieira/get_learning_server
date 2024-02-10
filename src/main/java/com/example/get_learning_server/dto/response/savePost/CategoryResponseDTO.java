package com.example.get_learning_server.dto.response.savePost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class CategoryResponseDTO {
  private UUID id;
  private String name;
  private String slug;
}