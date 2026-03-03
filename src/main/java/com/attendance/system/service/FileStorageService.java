package com.attendance.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${app.file.upload-dir}")
    private String uploadDir;
    
    public String storeFile(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Only JPG and PNG images are allowed");
        }
        
        if (file.getSize() > 3 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 3MB limit");
        }
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }
    
    // New method for employee documents (PDF, images)
    public String storeDocument(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File content type is not recognized");
        }
        
        // Allow PDF, JPG, PNG
        boolean isValidType = contentType.equals("application/pdf") ||
                             contentType.equals("image/jpeg") ||
                             contentType.equals("image/png");
        
        if (!isValidType) {
            throw new IllegalArgumentException("Only PDF, JPG, and PNG files are allowed");
        }
        
        // 5MB limit for documents
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }
        
        Path uploadPath = Paths.get(uploadDir, "documents");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".pdf";
        String filename = "doc_" + userId + "_" + UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "documents/" + filename;
    }
    
    public String storePhoto(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && 
                                    !contentType.equals("image/png") && 
                                    !contentType.equals("image/jpg"))) {
            throw new IllegalArgumentException("Only JPG and PNG images are allowed");
        }
        
        // 3MB limit for photos
        if (file.getSize() > 3 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 3MB limit");
        }
        
        Path uploadPath = Paths.get(uploadDir, "photos");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".jpg";
        String filename = "photo_" + userId + "_" + UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "photos/" + filename;
    }
    
    public String storeSignature(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && 
                                    !contentType.equals("image/png") && 
                                    !contentType.equals("image/jpg"))) {
            throw new IllegalArgumentException("Only JPG and PNG images are allowed");
        }
        
        // 2MB limit for signatures
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 2MB limit");
        }
        
        Path uploadPath = Paths.get(uploadDir, "signatures");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".png";
        String filename = "signature_" + userId + "_" + UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "signatures/" + filename;
    }
    
    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }
    
    public void deleteFile(String filename) throws IOException {
        // Handle subdirectories (photos/, signatures/, documents/)
        Path filePath;
        if (filename.contains("/")) {
            // File is in a subdirectory
            filePath = Paths.get(uploadDir).resolve(filename);
        } else {
            // File is in root upload directory
            filePath = Paths.get(uploadDir).resolve(filename);
        }
        Files.deleteIfExists(filePath);
    }
}
