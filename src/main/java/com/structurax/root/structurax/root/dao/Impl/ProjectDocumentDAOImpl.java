package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ProjectDocumentDAO;
import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectDocumentDAOImpl implements ProjectDocumentDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDocumentDAOImpl.class);
    private final DatabaseConnection databaseConnection;

    public ProjectDocumentDAOImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<ProjectDocumentsDTO> getDocumentsByProjectId(String projectId) {
        String sql = "SELECT document_id, project_id, document_url, type, description, upload_date " +
                     "FROM project_documents WHERE project_id = ? ORDER BY upload_date DESC";
        List<ProjectDocumentsDTO> documents = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                documents.add(mapResultSetToDTO(rs));
            }
            logger.info("Retrieved {} documents for project_id: {}", documents.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving documents for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving documents: " + e.getMessage(), e);
        }
        return documents;
    }

    @Override
    public ProjectDocumentsDTO getDocumentById(Integer documentId) {
        String sql = "SELECT document_id, project_id, document_url, type, description, upload_date " +
                     "FROM project_documents WHERE document_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, documentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("Retrieved document with document_id: {}", documentId);
                return mapResultSetToDTO(rs);
            } else {
                logger.warn("No document found with document_id: {}", documentId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving document by id {}: {}", documentId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving document: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProjectDocumentsDTO> getDocumentsByProjectIdAndType(String projectId, String type) {
        String sql = "SELECT document_id, project_id, document_url, type, description, upload_date " +
                     "FROM project_documents WHERE project_id = ? AND type = ? ORDER BY upload_date DESC";
        List<ProjectDocumentsDTO> documents = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                documents.add(mapResultSetToDTO(rs));
            }
            logger.info("Retrieved {} documents for project_id: {} and type: {}", documents.size(), projectId, type);
        } catch (SQLException e) {
            logger.error("Error retrieving documents for project {} and type {}: {}", projectId, type, e.getMessage(), e);
            throw new RuntimeException("Error retrieving documents: " + e.getMessage(), e);
        }
        return documents;
    }

    @Override
    public ProjectDocumentsDTO createDocument(ProjectDocumentsDTO documentDTO) {
        String sql = "INSERT INTO project_documents (project_id, document_url, type, description) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, documentDTO.getProjectId());
            ps.setString(2, documentDTO.getDocumentUrl());
            ps.setString(3, documentDTO.getType());
            ps.setString(4, documentDTO.getDescription());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating document failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    documentDTO.setDocumentId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating document failed, no ID obtained.");
                }
            }

            logger.info("Document created successfully with ID: {}", documentDTO.getDocumentId());

            // Retrieve the complete document with upload_date
            return getDocumentById(documentDTO.getDocumentId());

        } catch (SQLException e) {
            logger.error("Error creating document: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating document: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateDocument(ProjectDocumentsDTO documentDTO) {
        String sql = "UPDATE project_documents SET description = ? WHERE document_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documentDTO.getDescription());
            ps.setInt(2, documentDTO.getDocumentId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No document found with document_id: {}", documentDTO.getDocumentId());
            } else {
                logger.info("Document updated successfully with ID: {}", documentDTO.getDocumentId());
            }

        } catch (SQLException e) {
            logger.error("Error updating document: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating document: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDocument(Integer documentId) {
        String sql = "DELETE FROM project_documents WHERE document_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, documentId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No document found with document_id: {}", documentId);
            } else {
                logger.info("Document deleted successfully with ID: {}", documentId);
            }

        } catch (SQLException e) {
            logger.error("Error deleting document: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting document: " + e.getMessage(), e);
        }
    }

    private ProjectDocumentsDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        ProjectDocumentsDTO dto = new ProjectDocumentsDTO();
        dto.setDocumentId(rs.getInt("document_id"));
        dto.setProjectId(rs.getString("project_id"));
        dto.setDocumentUrl(rs.getString("document_url"));
        dto.setType(rs.getString("type"));
        dto.setDescription(rs.getString("description"));

        Date uploadDate = rs.getDate("upload_date");
        if (uploadDate != null) {
            dto.setUploadDate(uploadDate.toLocalDate());
        }

        return dto;
    }
}
