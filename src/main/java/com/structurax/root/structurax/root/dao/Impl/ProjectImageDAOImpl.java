package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ProjectImageDAO;
import com.structurax.root.structurax.root.dto.ProjectImageDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectImageDAOImpl implements ProjectImageDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectImageDAOImpl.class);
    private final DatabaseConnection databaseConnection;

    public ProjectImageDAOImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<ProjectImageDTO> getImagesByProjectId(String projectId) {
        String sql = "SELECT image_id, project_id, image_url, description, upload_date " +
                     "FROM project_images WHERE project_id = ? ORDER BY upload_date DESC";
        List<ProjectImageDTO> images = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                images.add(mapResultSetToDTO(rs));
            }
            logger.info("Retrieved {} images for project_id: {}", images.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving images for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving images: " + e.getMessage(), e);
        }
        return images;
    }

    @Override
    public ProjectImageDTO getImageById(Integer imageId) {
        String sql = "SELECT image_id, project_id, image_url, description, upload_date " +
                     "FROM project_images WHERE image_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, imageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("Retrieved image with image_id: {}", imageId);
                return mapResultSetToDTO(rs);
            } else {
                logger.warn("No image found with image_id: {}", imageId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving image by id {}: {}", imageId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving image: " + e.getMessage(), e);
        }
    }

    @Override
    public ProjectImageDTO createImage(ProjectImageDTO imageDTO) {
        String sql = "INSERT INTO project_images (project_id, image_url, description) VALUES (?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, imageDTO.getProjectId());
            ps.setString(2, imageDTO.getImageUrl());
            ps.setString(3, imageDTO.getDescription());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating image failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    imageDTO.setImageId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating image failed, no ID obtained.");
                }
            }

            logger.info("Image created successfully with ID: {}", imageDTO.getImageId());

            // Retrieve the complete image with upload_date
            return getImageById(imageDTO.getImageId());

        } catch (SQLException e) {
            logger.error("Error creating image: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating image: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateImage(ProjectImageDTO imageDTO) {
        String sql = "UPDATE project_images SET description = ? WHERE image_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, imageDTO.getDescription());
            ps.setInt(2, imageDTO.getImageId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No image found with image_id: {}", imageDTO.getImageId());
            } else {
                logger.info("Image updated successfully with ID: {}", imageDTO.getImageId());
            }

        } catch (SQLException e) {
            logger.error("Error updating image: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating image: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(Integer imageId) {
        String sql = "DELETE FROM project_images WHERE image_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, imageId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No image found with image_id: {}", imageId);
            } else {
                logger.info("Image deleted successfully with ID: {}", imageId);
            }

        } catch (SQLException e) {
            logger.error("Error deleting image: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting image: " + e.getMessage(), e);
        }
    }

    private ProjectImageDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        ProjectImageDTO dto = new ProjectImageDTO();
        dto.setImageId(rs.getInt("image_id"));
        dto.setProjectId(rs.getString("project_id"));
        dto.setImageUrl(rs.getString("image_url"));
        dto.setDescription(rs.getString("description"));

        Date uploadDate = rs.getDate("upload_date");
        if (uploadDate != null) {
            dto.setUploadDate(uploadDate.toLocalDate());
        }

        return dto;
    }
}
