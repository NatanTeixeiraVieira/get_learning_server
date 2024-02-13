package com.example.get_learning_server.util;

import com.example.get_learning_server.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class MethodsUtil {
  public static String generateSlug(String input) {
    final Pattern pattern = Pattern.compile("[\\p{InCombiningDiacriticalMarks}]+");

    String slug = input.trim().replaceAll(" ", "-");
    slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
    slug = pattern.matcher(slug).replaceAll("");
    return slug.toLowerCase();
  }

  public static String generateEmailConfirmationContent(String userName, String baseUrl) {
    final String confirmationLink = baseUrl.concat("/register/confirm");
    return """
          <p>%s, clique no link abaixo para verificar seu email e ativar sua conta no GetLearning: </p>
          <a href="%s">%s</a>
          <p>Caso você não tenha feito esta requisição, ignore esse email.</p><br>
          Atenciosamente, <br>
          Equipe GetLearning.
        """.formatted(userName, confirmationLink, confirmationLink);
  }

  public static User getLoggedUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
