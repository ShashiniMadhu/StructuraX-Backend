package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.ProjectImageDAO;
import com.structurax.root.structurax.root.dto.ProjectImageDTO;
import com.structurax.root.structurax.root.service.ProjectImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProjectImageServiceImpl implements ProjectImageService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectImageServiceImpl.class);
    private static final String UPLOAD_DIR = "uploads/project-images/";

    private final ProjectImageDAO projectImageDAO;

    public ProjectImageServiceImpl(ProjectImageDAO projectImageDAO) {
        this.projectImageDAO = projectImageDAO;
        // Create upload directory if it doesn't exist
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Created upload directory: {}", UPLOAD_DIR);
            } else {
                logger.warn("Failed to create upload directory: {}", UPLOAD_DIR);
            }
        }
    }

    @Override
    public List<ProjectImageDTO> getImagesByProjectId(String projectId) {
        logger.info("Fetching all images for project_id: {}", projectId);
        return projectImageDAO.getImagesByProjectId(projectId);
    }

    @Override
    public ProjectImageDTO getImageById(Integer imageId) {
        logger.info("Fetching image with image_id: {}", imageId);
        return projectImageDAO.getImageById(imageId);
    }

    @Override
    public ProjectImageDTO uploadImage(String projectId, String description, MultipartFile file) {
        logger.info("Uploading image for project_id: {}", projectId);

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        // Validate file type (images only)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = timestamp + "_" + originalFilename;
            String filePath = UPLOAD_DIR + filename;

            // Save file to disk
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            logger.info("Image saved successfully: {}", filePath);

            // Create DTO and save to database
            ProjectImageDTO imageDTO = new ProjectImageDTO();
            imageDTO.setProjectId(projectId);
            imageDTO.setImageUrl(filePath);
            imageDTO.setDescription(description);

            return projectImageDAO.createImage(imageDTO);

        } catch (IOException e) {
            logger.error("Error uploading image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateImage(ProjectImageDTO imageDTO) {
        logger.info("Updating image with image_id: {}", imageDTO.getImageId());
        projectImageDAO.updateImage(imageDTO);
    }

    @Override
    public void deleteImage(Integer imageId) {
        logger.info("Deleting image with image_id: {}", imageId);

        // Get image details to delete the file
        ProjectImageDTO image = projectImageDAO.getImageById(imageId);

        if (image != null) {
            // Delete file from disk
            try {
                Path filePath = Paths.get(image.getImageUrl());
                Files.deleteIfExists(filePath);
                logger.info("Image file deleted successfully: {}", image.getImageUrl());
            } catch (IOException e) {
                logger.error("Error deleting image file: {}", e.getMessage(), e);
            }

            // Delete from database
            projectImageDAO.deleteImage(imageId);
        } else {
            logger.warn("Image not found with image_id: {}", imageId);
        }
    }

    @Override
    public String getImageFilePath(Integer imageId) {
        logger.info("Fetching file path for image_id: {}", imageId);
        ProjectImageDTO image = projectImageDAO.getImageById(imageId);

        if (image != null) {
            return image.getImageUrl();
        } else {
            logger.warn("Image not found with image_id: {}", imageId);
            return null;
        }
    }
}
