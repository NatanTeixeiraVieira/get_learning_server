package com.example.get_learning_server.repository;

import com.example.get_learning_server.entity.Category;
import com.example.get_learning_server.enums.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
  Category findByName(String categoryName);
}
