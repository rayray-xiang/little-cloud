package com.rayxiang.backupapp.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        // name the file if it is unnamed
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "unnamed-file-" + System.currentTimeMillis();
        }

        Path filePath = uploadDir.resolve(originalFilename).normalize();
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }

    public Resource loadFile(String filename) throws IOException {
        Path filePath = uploadDir.resolve(filename).normalize();
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filename);
        }
        return new UrlResource(filePath.toUri());
    }

    // List all stored files
    public List<String> listFiles() throws IOException {
        try (Stream<Path> paths = Files.list(uploadDir)) {
            return paths.map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
        }
    }
}

