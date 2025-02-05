package com.example.demo.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @SneakyThrows
    public List<String> saveFiles(List<MultipartFile> files,int id) {
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file->{
                    String filename = String.valueOf(id) + "_" +System.currentTimeMillis() + "_" +file.getOriginalFilename();
                    Path destination = Path.of(UPLOAD_DIR, filename);
                    try {
                        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        throw new RuntimeException("Ошибка при сохранении файла", e);
                    }
                    return UPLOAD_DIR+"/"+filename;
                })
                .toList();
    }

    @SneakyThrows
    public void deleteFiles(List<String> imageUrls){
        imageUrls.stream()
                .filter(Objects::nonNull)
                .map(imageUrl ->{
                    Path filePath = Path.of(imageUrl);
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                })
                .toList();
    }
}
