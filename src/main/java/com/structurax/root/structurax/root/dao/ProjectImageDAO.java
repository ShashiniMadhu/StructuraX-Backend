package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ProjectImageDTO;

import java.util.List;

public interface ProjectImageDAO {

    /**
     * Get all images for a specific project
     */
    List<ProjectImageDTO> getImagesByProjectId(String projectId);

    /**
     * Get a single image by image ID
     */
    ProjectImageDTO getImageById(Integer imageId);

    /**
     * Upload/create a new image
     */
    ProjectImageDTO createImage(ProjectImageDTO imageDTO);

    /**
     * Update image details (description)
     */
    void updateImage(ProjectImageDTO imageDTO);

    /**
     * Delete an image
     */
    void deleteImage(Integer imageId);
}