package com.example.get_learning_server.service.user;

import com.example.get_learning_server.entity.User;
import com.example.get_learning_server.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class UserDetailsServiceImpl implements UserDetailsService {
  final UserRepository userRepository;
  @Override
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    final User user = userRepository.findByLogin(login)
        .orElseThrow(() -> new UsernameNotFoundException("User " + login + " not found"));

    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        user.getEnabled(),
        user.getAccountNonExpired(),
        user.getCredentialsNonExpired(),
        user.getAccountNonLocked(),
        user.getAuthorities());
  }
}
