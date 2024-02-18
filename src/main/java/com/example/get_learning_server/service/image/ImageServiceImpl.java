package com.example.get_learning_server.service.image;

import com.google.cloud.storage.BlobInfo;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {
  @Override
  public BlobInfo uploadImage(MultipartFile coverImageFile, String basePath) throws IOException {
    final String coverImagePath = basePath + UUID.randomUUID();

    return StorageClient
        .getInstance()
        .bucket()
        .create(coverImagePath, coverImageFile.getInputStream(), coverImageFile.getContentType());
  }
}
