package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.ProjectImageDTO;
import com.structurax.root.structurax.root.service.ProjectImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-owner/images")
public class ProjectOwnerImagesController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerImagesController.class);
    private final ProjectImageService imageService;

    public ProjectOwnerImagesController(ProjectImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Get all images for a specific project
     * GET /api/project-owner/images/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getImagesByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching images for project_id={}", projectId);
            List<ProjectImageDTO> images = imageService.getImagesByProjectId(projectId);
            response.put("success", true);
            response.put("images", images);
            response.put("totalCount", images.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching images: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get a single image by image ID
     * GET /api/project-owner/images/{imageId}
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<Map<String, Object>> getImageById(@PathVariable Integer imageId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching image by image_id={}", imageId);
            ProjectImageDTO image = imageService.getImageById(imageId);

            if (image != null) {
                response.put("success", true);
                response.put("image", image);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Image not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error fetching image: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Upload a new image
     * POST /api/project-owner/images/upload
     *
     * Form-data parameters:
     * - projectId: String
     * - description: String (optional)
     * - file: MultipartFile (image file)
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("projectId") String projectId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Uploading image for project_id={}", projectId);

            ProjectImageDTO image = imageService.uploadImage(projectId, description, file);

            response.put("success", true);
            response.put("message", "Image uploaded successfully");
            response.put("image", image);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error uploading image: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error uploading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update image details (description only)
     * PUT /api/project-owner/images/{imageId}
     */
    @PutMapping("/{imageId}")
    public ResponseEntity<Map<String, Object>> updateImage(
            @PathVariable Integer imageId,
            @RequestBody ProjectImageDTO imageDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating image with image_id={}", imageId);
            imageDTO.setImageId(imageId);
            imageService.updateImage(imageDTO);

            response.put("success", true);
            response.put("message", "Image updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating image: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete an image
     * DELETE /api/project-owner/images/{imageId}
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable Integer imageId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Deleting image with image_id={}", imageId);
            imageService.deleteImage(imageId);

            response.put("success", true);
            response.put("message", "Image deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting image: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error deleting image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * View/preview an image file
     * GET /api/project-owner/images/view/{imageId}
     */
    @GetMapping("/view/{imageId}")
    public ResponseEntity<Resource> viewImage(@PathVariable Integer imageId) {
        try {
            logger.info("Viewing image with image_id={}", imageId);

            String filePath = imageService.getImageFilePath(imageId);

            if (filePath == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                String filename = path.getFileName().toString();
                String contentType = determineImageContentType(filename);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                logger.error("Image file not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Error viewing image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error viewing image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Download an image file
     * GET /api/project-owner/images/download/{imageId}
     */
    @GetMapping("/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Integer imageId) {
        try {
            logger.info("Downloading image with image_id={}", imageId);

            String filePath = imageService.getImageFilePath(imageId);

            if (filePath == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                String filename = path.getFileName().toString();

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                logger.error("Image file not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Error downloading image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error downloading image: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to determine image content type
     */
    private String determineImageContentType(String filename) {
        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerFilename.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "application/octet-stream";
        }
    }
}