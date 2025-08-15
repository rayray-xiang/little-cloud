package com.rayxiang.backupapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    public FileStorageService() throws IOException {
        // create uploads folder if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();

        // name file if client uploads an unnamed file
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "unnamed-file-" + System.currentTimeMillis();
        }

        Path filePath = uploadDir.resolve(originalFilename);

        // debug output
        System.out.println("Upload directory: " + uploadDir.toAbsolutePath());
        System.out.println("Saving file: " + originalFilename + " -> " + filePath.toAbsolutePath());
        System.out.println("File is empty? " + file.isEmpty());

        file.transferTo(filePath.toFile());

        System.out.println("File saved successfully!");
        return filePath.toString();
    }
}

