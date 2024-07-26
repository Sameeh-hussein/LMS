package com.LibraryManagementSystem.LMS.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String uploadImageToFileSystem(MultipartFile file, String type) throws IOException;
}
