package com.example.get_learning_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@Configuration
public class FirebaseConfig {
  private final Environment environment;

  @Bean
  public FirebaseApp initFirebase() throws IOException {
    final String firebaseCredentials = environment.getProperty("FIREBASE_CREDENTIALS");
    final String storageBucket = environment.getProperty("FIREBASE_STORAGE_BUCKET");

    assert firebaseCredentials != null;
    FirebaseOptions firebaseOptions = FirebaseOptions
        .builder()
        .setCredentials(
            GoogleCredentials.fromStream(
                new ByteArrayInputStream(firebaseCredentials.getBytes(StandardCharsets.UTF_8))
            )
        )
        .setStorageBucket(storageBucket)
        .build();

    return FirebaseApp.getApps().isEmpty() ? FirebaseApp.initializeApp(firebaseOptions) : FirebaseApp.getInstance();
  }
}
