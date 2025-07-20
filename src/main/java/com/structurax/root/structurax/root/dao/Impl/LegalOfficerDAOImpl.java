package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.LegalOfficerDAO;
import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class LegalOfficerDAOImpl implements LegalOfficerDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}
