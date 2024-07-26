package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.services.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path profileImagesPath = Path.of("src/main/resources/static/images/userProfile");

    public FileStorageServiceImpl() {
        try {
            Files.createDirectories(profileImagesPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create directories for user profile images.", e);
        }
    }

    @Override
    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        String fileName = Objects.requireNonNull(file.getOriginalFilename(), "File name must not be null");
        Path filePath = profileImagesPath.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return "images/userProfile/" + fileName;
    }
}
