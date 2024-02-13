package com.example.get_learning_server.exception.handler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.get_learning_server.exception.ExceptionResponse;
import com.example.get_learning_server.exception.NoPermissionException;
import com.example.get_learning_server.exception.NoPostFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(AccountStatusException.class)
  private ResponseEntity<ExceptionResponse> handleAccountStatusException(
      AccountStatusException ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        "User account is abnormal",
        request.getDescription(false),
        HttpStatus.UNAUTHORIZED);

    return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ValidationException.class)
  private ResponseEntity<ExceptionResponse> handleValidationException(
      ValidationException ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        "Invalid data",
        request.getDescription(false),
        HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TokenExpiredException.class)
  private ResponseEntity<ExceptionResponse> handleTokenExpiredException(
      TokenExpiredException ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        "Token expired",
        request.getDescription(false),
        HttpStatus.UNAUTHORIZED);

    return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(InsufficientAuthenticationException.class)
  private ResponseEntity<ExceptionResponse> handleInvalidJwtTokenException(
      InsufficientAuthenticationException ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        "Invalid token",
        request.getDescription(false),
        HttpStatus.UNAUTHORIZED);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  private ResponseEntity<ExceptionResponse> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        "No permission",
        request.getDescription(false),
        HttpStatus.FORBIDDEN);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
  private ResponseEntity<ExceptionResponse> handleCredentialsException(
      Exception ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        "Invalid login or password",
        request.getDescription(false),
        HttpStatus.UNAUTHORIZED);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(JWTCreationException.class)
  private ResponseEntity<ExceptionResponse> handleJwtCreationException(
      JWTCreationException ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        "Error while generating token",
        request.getDescription(false),
        HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NoPostFoundException.class)
  private ResponseEntity<ExceptionResponse> handlePostNotFoundException(
      Exception ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        ex.toString(),
        request.getDescription(false),
        HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoPermissionException.class)
  private ResponseEntity<ExceptionResponse> handleNoPermissionException(
      Exception ex, WebRequest request) {

    ExceptionResponse exceptionResponse = new ExceptionResponse(
        ex.toString(),
        request.getDescription(false),
        HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(Exception.class)
  private ResponseEntity<ExceptionResponse> handleGenericExceptions(Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(
//        "A server internal error occurred"
        ex.toString(),
        request.getDescription(false),
        HttpStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
