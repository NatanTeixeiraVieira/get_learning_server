package com.example.get_learning_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoPostFoundException extends RuntimeException {
  public NoPostFoundException(String ex) {
    super(ex);
  }
}
