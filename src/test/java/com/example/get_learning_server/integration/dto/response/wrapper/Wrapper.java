package com.example.get_learning_server.integration.dto.response.wrapper;

import com.example.get_learning_server.integration.dto.response.getAllPosts.Posts;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Wrapper {
  @JsonProperty("_embedded")
  private Embedded embedded;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class Embedded {
    private List<Posts> postsList;
  }
}
