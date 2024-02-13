package com.example.get_learning_server.config;

import com.example.get_learning_server.security.AuthEntryPoint;
import com.example.get_learning_server.security.jwt.JwtTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
  private final JwtTokenFilter tokenFilter;
  private final AuthEntryPoint authEntryPoint;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .httpBasic((httBasic) -> httBasic.authenticationEntryPoint(authEntryPoint))
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers(HttpMethod.POST, "/api/auth/v1/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/api/auth/v1/login").permitAll()
//                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/**").authenticated()
//                .anyRequest().hasRole("USER")
                .requestMatchers("/users").denyAll()
        )
        .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
        .cors(cors -> {})
        .build();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
      throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
