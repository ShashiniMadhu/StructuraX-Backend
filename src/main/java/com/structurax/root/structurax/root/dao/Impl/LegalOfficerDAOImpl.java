package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.LegalOfficerDAO;

import com.structurax.root.structurax.root.dto.LegalDocumentDTO;
import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;

import java.util.List;

@Repository
public class LegalOfficerDAOImpl implements LegalOfficerDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DatabaseConnection databaseConnection;


    @Override
    public List<ProjectDocumentsDTO> findAllLegalDocuments() {
        String sql = "SELECT document_id, project_id, document_url, description, upload_date FROM project_documents WHERE type = 'legal'";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
            ProjectDocumentsDTO dto = new ProjectDocumentsDTO();
            dto.setDocumentId(rs.getInt("document_id"));
            dto.setProjectId(rs.getString("project_id"));
            dto.setDocumentUrl(rs.getString("document_url"));
            dto.setDescription(rs.getString("description"));
            dto.setUploadDate(rs.getDate("upload_date").toLocalDate());
            return dto;
        });
    }


    @Override
    public LegalDocumentDTO adddocument(LegalDocumentDTO dto) {
        // Since document_id is auto-increment, exclude it from INSERT
        String sql = "INSERT INTO legal_document(project_id, document_url, date, type, description) VALUES ( ?, ?, ?, ?, ?)";

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, dto.getProjectId());
            ps.setString(2, dto.getDocumentUrl());
            ps.setDate(3, Date.valueOf(dto.getDate() != null ? dto.getDate() : LocalDate.now()));
            ps.setString(4, dto.getType());
            ps.setString(5, dto.getDescription());

            ps.executeUpdate();

            // Get the generated document_id
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                dto.setDocumentId(rs.getInt(1));
            }
            rs.close();

        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage() + " SQL State: " + e.getSQLState(), e);
        }
        return dto;
    }

    @Override
    public List<LegalDocumentDTO> findLegalDocumentsByProjectId(String projectId) {
        String sql = "SELECT legal_document_id, project_id, document_url, date, type, description FROM legal_document WHERE project_id = ?";
        return jdbcTemplate.query(sql, new Object[]{projectId}, (ResultSet rs, int rowNum) -> {
            LegalDocumentDTO dto = new LegalDocumentDTO();
            dto.setDocumentId(rs.getInt("legal_document_id"));
            dto.setProjectId(rs.getString("project_id"));
            dto.setDocumentUrl(rs.getString("document_url"));
            dto.setDate(rs.getDate("date").toLocalDate());
            dto.setType(rs.getString("type"));
            dto.setDescription(rs.getString("description"));
            return dto;
        });
    }
}


