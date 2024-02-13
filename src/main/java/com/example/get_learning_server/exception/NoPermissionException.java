package com.example.get_learning_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoPermissionException extends RuntimeException {
  public NoPermissionException(String ex) {
    super(ex);
  }
}
