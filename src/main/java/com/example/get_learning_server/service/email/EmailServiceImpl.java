package com.example.get_learning_server.service.email;

import com.example.get_learning_server.entity.Email;
import com.example.get_learning_server.enums.EmailStatus;
import com.example.get_learning_server.repository.EmailRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final EmailRepository emailRepository;
  private final JavaMailSender emailSender;

  public Email sendEmail(Email email) {
    email.setEmailSendingDate(LocalDateTime.now());
    try {
      MimeMessage mimeMessage = emailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

      helper.setFrom(email.getEmailFrom());
      helper.setTo(email.getEmailTo());
      helper.setSubject(email.getSubject());
      helper.setText(email.getContent(), true);

      emailSender.send(mimeMessage);

      email.setStatus(EmailStatus.SENT);
    } catch (MailException e) {
      email.setStatus(EmailStatus.ERROR);
    } finally {
      return emailRepository.save(email);
    }
  }
}
