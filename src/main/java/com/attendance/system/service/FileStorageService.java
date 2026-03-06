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
    
    private Path getUploadPath() {
        // Normalize the path to handle relative paths like "./uploads"
        Path path = Paths.get(uploadDir);
        return path.normalize().toAbsolutePath();
    }
    
    private Path getUploadPath(String subdirectory) {
        Path basePath = getUploadPath();
        if (subdirectory != null && !subdirectory.isEmpty()) {
            return basePath.resolve(subdirectory).normalize();
        }
        return basePath;
    }
    
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
        
        Path uploadPath = getUploadPath();
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new IOException("Failed to create upload directory: " + uploadPath, e);
            }
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
        
        Path uploadPath = getUploadPath("documents");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new IOException("Failed to create documents directory: " + uploadPath, e);
            }
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
        
        Path uploadPath = getUploadPath("photos");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new IOException("Failed to create photos directory: " + uploadPath, e);
            }
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
        
        Path uploadPath = getUploadPath("signatures");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new IOException("Failed to create signatures directory: " + uploadPath, e);
            }
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
        return getUploadPath().resolve(filename).normalize();
    }
    
    public void deleteFile(String filename) throws IOException {
        // Handle subdirectories (photos/, signatures/, documents/)
        Path filePath = getUploadPath().resolve(filename).normalize();
        Files.deleteIfExists(filePath);
    }
}
