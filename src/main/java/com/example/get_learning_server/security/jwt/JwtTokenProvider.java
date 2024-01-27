package com.example.get_learning_server.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.get_learning_server.entity.User;
import com.example.get_learning_server.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class JwtTokenProvider {
  @Value("${api.security.token.secret_jwt_application}")
  private String secretJwtApplication;

  @Value("${api.security.token.secret_verification_email}")
  private String secretVerificationEmail;
  public String generateAccessToken(User user) {
    final Algorithm algorithm = Algorithm.HMAC256(secretJwtApplication);
    return JWT
        .create()
        .withIssuer(Constants.applicationNameSnakeCase)
        .withSubject(user.getLogin())
        .withExpiresAt(generateExpirationDate())
        .sign(algorithm);
  }

  public String validateAccessToken(String token) {
    final Algorithm algorithm = Algorithm.HMAC256(secretJwtApplication);
    return JWT.require(algorithm).withIssuer(Constants.applicationNameSnakeCase).build().verify(token).getSubject();
  }

  public String generateVerificationEmailToken(User user) {
    final Algorithm algorithm = Algorithm.HMAC256(secretVerificationEmail);
    return JWT
        .create()
        .withIssuer(Constants.applicationNameSnakeCase)
        .withSubject(user.getLogin())
        .withExpiresAt(generateExpirationDateVerificationEmailToken())
        .sign(algorithm);
  }

  public String validateVerificationEmailToken(String token) {
    final Algorithm algorithm = Algorithm.HMAC256(secretVerificationEmail);
    return JWT.require(algorithm).withIssuer(Constants.applicationNameSnakeCase).build().verify(token).getSubject();
  }

  private Instant generateExpirationDate () {
    return LocalDateTime.now().plusDays(Constants.tokenDurationTimeInDays).toInstant(ZoneOffset.of("-03:00"));
  }

  private Instant generateExpirationDateVerificationEmailToken () {
    return LocalDateTime
        .now()
        .plusHours(Constants.tokenVerificationEmailDurationTimeInHours)
        .toInstant(ZoneOffset.of("-03:00"));
  }
}
