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
        // Create uploads folder if it doesn't exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    // Saves a file into uploadDir
    public String saveFile(MultipartFile file) throws IOException {
        String folder = getFolderByType(file);
        Path uploadPath = Paths.get("uploads").resolve(folder);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // create folder if it doesn't exist
        }

        String filename = file.getOriginalFilename();

        // Name the file if it is unnamed
        if (filename == null || filename.isBlank()) {
            filename = "unnamed-file-" + System.currentTimeMillis();
        }

        Path filePath = uploadPath.resolve(filename);

        // Rename file if needed to prevent overwriting
//        int count = 1;
//        while (Files.exists(filePath)) {
//            String name = FilenameUtils.getBaseName(filename);
//        }

        file.transferTo(filePath.toFile());

        return filePath.toString();
    }

    // Loads a file from uploadDir
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
            return paths.map(p -> p.getFileName().toString()).collect(Collectors.toList());
        }
    }

    private String getFolderByType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return "others";
        if (contentType.startsWith("image/")) return "images";
        if (contentType.equals("application/pdf") || contentType.equals("application/msword"))
            return "docs";
        if (contentType.startsWith("video/")) return "videos";
        return "others";
    }
}

