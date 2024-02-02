package com.example.get_learning_server.service.auth;

import com.example.get_learning_server.dto.request.login.LoginRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterEmailVerificationRequestDTO;
import com.example.get_learning_server.dto.request.register.RegisterConfirmEmailRequestDTO;
import com.example.get_learning_server.dto.response.register.RegisterEmailVerificationResponseDTO;
import com.example.get_learning_server.dto.response.login.LoginResponseDTO;
import com.example.get_learning_server.dto.response.login.UserLoginResponseDTO;
import com.example.get_learning_server.entity.Author;
import com.example.get_learning_server.entity.Email;
import com.example.get_learning_server.entity.Role;
import com.example.get_learning_server.entity.User;
import com.example.get_learning_server.enums.EmailVerification;
import com.example.get_learning_server.enums.UserRoles;
import com.example.get_learning_server.repository.AuthorRepository;
import com.example.get_learning_server.repository.EmailRepository;
import com.example.get_learning_server.repository.RoleRepository;
import com.example.get_learning_server.repository.UserRepository;
import com.example.get_learning_server.security.jwt.JwtTokenProvider;
import com.example.get_learning_server.service.email.EmailServiceImpl;
import com.example.get_learning_server.util.Constants;
import com.example.get_learning_server.util.MethodsUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Data
@EqualsAndHashCode
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final EmailServiceImpl emailService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AuthorRepository authorRepository;
  private final EmailRepository emailRepository;

  @Value("${spring.mail.username}")
  private String emailFrom = "";

  @Value("${cors.originPatterns}")
  private String baseUrl = "";

  public AuthServiceImpl(AuthenticationManager authenticationManager,
                         JwtTokenProvider tokenProvider,
                         EmailServiceImpl emailService,
                         UserRepository userRepository,
                         RoleRepository roleRepository,
                         AuthorRepository authorRepository,
                         EmailRepository emailRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.emailService = emailService;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.authorRepository = authorRepository;
    this.emailRepository = emailRepository;
  }

  @Override
  public RegisterEmailVerificationResponseDTO registerSendEmailVerification(
      @Valid RegisterEmailVerificationRequestDTO registerData) {
    final Optional<User> verifyLoginAlreadyExists = userRepository.findByLogin(registerData.login());

    // TODO create a custom exception for existing emails
    if (verifyLoginAlreadyExists.isPresent()) throw new BadCredentialsException("Email already exists");

    final String encryptedPassword = new BCryptPasswordEncoder().encode(registerData.password());
    final Role userRole = roleRepository.findByName(UserRoles.USER);
    final User user = new User(registerData.login(), encryptedPassword, userRole);
    final Author author = new Author();
    final String authorSlug = MethodsUtil.generateSlug(registerData.userName());

    author.setName(registerData.userName());
    author.setSlug(authorSlug);
    author.setUser(user);

    userRepository.save(user);
    authorRepository.save(author);

    final UUID emailId = UUID.randomUUID();
    final Email email = new Email();

    email.setId(emailId);
    email.setEmailFrom(emailFrom);
    email.setEmailTo(registerData.login());
    email.setSubject(Constants.confirmationEmailSubject);
    email.setContent(MethodsUtil.generateEmailConfirmationContent(registerData.userName(), baseUrl));
    email.setVerification(EmailVerification.NON_VERIFIED);
    email.setUser(user);

    Email emailSent = emailService.sendEmail(email);

    emailRepository.save(emailSent);

    final String token = tokenProvider.generateVerificationEmailToken(user);
    return new RegisterEmailVerificationResponseDTO(token, emailId);
  }

  @Override
  public LoginResponseDTO registerConfirmEmail(
      @Valid RegisterConfirmEmailRequestDTO registerConfirmEmailRequestDTO,
      String token) {
    final String login = tokenProvider.validateVerificationEmailToken(token);

    final User user = userRepository
        .findByLogin(login)
        .orElseThrow(() -> new BadCredentialsException("User not found"));

    if (login == null) throw new BadCredentialsException("Invalid token");

    final Email email = emailRepository
        .findById(registerConfirmEmailRequestDTO.emailId())
        .orElseThrow(() -> new BadCredentialsException(""));

    email.setVerification(EmailVerification.VERIFIED);
    emailRepository.save(email);

    user.setEnabled(true);
    userRepository.save(user);

    return loginWithoutAuthentication(user.getLogin());
  }

  @Override
  public LoginResponseDTO login(@Valid LoginRequestDTO authData) {
    final var usernamePassword = new UsernamePasswordAuthenticationToken(authData.login(), authData.password());

    authenticationManager.authenticate(usernamePassword);

    return loginWithoutAuthentication(authData.login());
  }

  private LoginResponseDTO loginWithoutAuthentication(@jakarta.validation.constraints.Email @NotBlank String login) {
    final User user = userRepository
        .findByLogin(login)
        .orElseThrow(() -> new BadCredentialsException("Email not fount"));
    final Author author = authorRepository
        .findByUser(user)
        .orElseThrow(() -> new BadCredentialsException("Author not found"));

    final String token = tokenProvider.generateAccessToken(user);

    final UserLoginResponseDTO userLoginResponseDTO =
        new UserLoginResponseDTO(user.getId(), user.getLogin(), author.getName(), author.getSlug());

    return new LoginResponseDTO(userLoginResponseDTO, token);
  }
}
