package com.attendance.system.controller;

import com.attendance.system.exception.ResourceNotFoundException;
import com.attendance.system.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File serving APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class FileController {
    
    private final FileStorageService fileStorageService;
    
    @GetMapping
    @Operation(summary = "Download file", description = "Download uploaded files (photos, signatures, documents) - requires authentication")
    public ResponseEntity<Resource> downloadFile(@RequestParam String path) {
        try {
            Path filePath = fileStorageService.loadFile(path);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = getContentType(filePath);
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                throw new ResourceNotFoundException("File not found: " + path);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("File not found: " + path);
        }
    }
    
    private String getContentType(Path filePath) {
        String filename = filePath.getFileName().toString().toLowerCase();
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "application/octet-stream";
    }
}
