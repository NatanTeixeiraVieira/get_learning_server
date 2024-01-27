package com.example.get_learning_server.repository;

import com.example.get_learning_server.entity.Author;
import com.example.get_learning_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
  Optional<Author> findByUser(User user);
}
