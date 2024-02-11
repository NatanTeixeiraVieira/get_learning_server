package com.example.get_learning_server.dto.response.getAllPosts;

import com.example.get_learning_server.entity.AuthorImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class AuthorDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private UUID id;

  private String name;

  private String slug;

  private AuthorImage userImageId;
}
