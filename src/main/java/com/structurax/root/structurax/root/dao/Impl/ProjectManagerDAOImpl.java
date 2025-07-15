package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ProjectManagerDAO;
import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectManagerDAOImpl implements ProjectManagerDAO {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Override
    public SiteVisitLogDTO createVisitLog(SiteVisitLogDTO dto) {
        // Validate required fields
        if (dto.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required and cannot be null");
        }
        
        String sql = "INSERT INTO site_visit_log(project_id, date, description, status) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, dto.getProjectId());
            ps.setDate(2, Date.valueOf(dto.getDate()));
            ps.setString(3, dto.getDescription());
            ps.setString(4, dto.getStatus());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                dto.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating visit log", e);
        }
        return dto;
    }

    @Override
    public List<SiteVisitLogDTO> getAllVisitLogs() {
        String sql = "SELECT * FROM site_visit_log";
        List<SiteVisitLogDTO> list = new ArrayList<>();

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                SiteVisitLogDTO dto = new SiteVisitLogDTO(
                        rs.getInt("visit_id"),
                        rs.getInt("project_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("description"),
                        rs.getString("status")
                );
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading visit logs", e);
        }
        return list;
    }

    @Override
    public SiteVisitLogDTO getVisitLogById(Integer id) {
        String sql = "SELECT * FROM site_visit_log WHERE id = ?";
        SiteVisitLogDTO dto = null;

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                dto = new SiteVisitLogDTO(
                        rs.getInt("id"),
                        rs.getInt("project_id"),
                        rs.getDate("visit_date").toLocalDate(),
                        rs.getString("description"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching visit log by ID", e);
        }

        return dto;
    }

}