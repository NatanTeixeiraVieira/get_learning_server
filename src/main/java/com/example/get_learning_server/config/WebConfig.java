package com.example.get_learning_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Value("${cors.originPatterns}")
  private String corsOriginPatterns = "";

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    final String[] allowedOrigins = corsOriginPatterns.split(",");
    registry.addMapping("/**")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
        .allowedOrigins(allowedOrigins)
        .allowCredentials(true);
  }
}
