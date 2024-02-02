package com.example.get_learning_server.entity;

import com.example.get_learning_server.enums.EmailVerification;
import com.example.get_learning_server.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Email extends Auditable implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String emailFrom;

  private String emailTo;

  private String subject;

  @Column(columnDefinition = "TEXT")
  private String content;

  private LocalDateTime emailSendingDate;

  @Enumerated(EnumType.STRING)
  private EmailStatus status;

  @Enumerated(EnumType.STRING)
  private EmailVerification verification;
}
