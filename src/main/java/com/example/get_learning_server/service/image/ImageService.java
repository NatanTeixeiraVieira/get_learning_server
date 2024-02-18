package com.example.get_learning_server.service.image;

import com.google.cloud.storage.BlobInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
  BlobInfo uploadImage(MultipartFile coverImageFile, String basePath) throws IOException;
}
