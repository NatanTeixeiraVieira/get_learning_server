package com.example.get_learning_server.util;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

public class UtilMethods {
  public static String generateSlug(String input) {
    final Pattern nonLatin = Pattern.compile("[^\\w-]");
    final Pattern whiteSpaces = Pattern.compile("[\\s]");

    final String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
    String slug = nonLatin.matcher(normalized).replaceAll("");
    slug = whiteSpaces.matcher(slug).replaceAll("-");
    return slug.toLowerCase();
  }

  public static String generateEmailConfirmationContent(UUID emailId, String userName, String baseUrl) {
    final String confirmationLink = baseUrl.concat("/register/confirm/").concat(emailId.toString());
    return "<p>"
        .concat(userName)
        .concat(", clique no link abaixo para verificar seu email e ativar sua conta no GetLearning: </p>")
        .concat("<a href=\"").concat(confirmationLink).concat("\">").concat(confirmationLink).concat("</a>")
        .concat("<p>Caso você não tenha feito esta requisição, ignore esse email.</p>")
        .concat("Atenciosamente, <br>")
        .concat("Equipe GetLearning");
  }
}
