package com.example.get_learning_server.repository;

import com.example.get_learning_server.entity.CoverImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoverImageRepository extends JpaRepository<CoverImage, UUID> {
}
