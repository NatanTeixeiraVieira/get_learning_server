package com.example.get_learning_server.integration.exeption;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Data
public class ExceptionResponse implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  private Date timestamp;
  private Boolean success;
  private String message;
  private int statusCode;
  private String status;
  private String description;
}
