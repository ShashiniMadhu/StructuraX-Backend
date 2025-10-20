package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ProjectImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectImageService {

    /**
     * Get all images for a specific project
     */
    List<ProjectImageDTO> getImagesByProjectId(String projectId);

    /**
     * Get a single image by image ID
     */
    ProjectImageDTO getImageById(Integer imageId);

    /**
     * Upload a new image
     */
    ProjectImageDTO uploadImage(String projectId, String description, MultipartFile file);

    /**
     * Update image details
     */
    void updateImage(ProjectImageDTO imageDTO);

    /**
     * Delete an image
     */
    void deleteImage(Integer imageId);

    /**
     * Get the file path for an image
     */
    String getImageFilePath(Integer imageId);
}