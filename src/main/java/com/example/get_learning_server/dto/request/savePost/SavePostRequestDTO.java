package com.example.get_learning_server.dto.request.savePost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SavePostRequestDTO {
  private String title;
  private String subtitle;
  private String content;
  private Boolean allowComments;
  private List<String> categories;
  private List<String> tags;
}
