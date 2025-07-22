package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ProjectManagerDAO;
import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import com.structurax.root.structurax.root.dto.VisitRequestDTO;
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
            ps.setString(1, dto.getProjectId());
            ps.setDate(2, Date.valueOf(dto.getDate()));
            ps.setString(3, dto.getDescription());
            ps.setString(4, dto.getStatus());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

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
                        rs.getString("project_id"),
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
                        rs.getString("project_id"),
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
    @Override
    public boolean updateVisitLog(SiteVisitLogDTO dto) {
        // Validate required fields
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Visit log ID is required for update");
        }
        if (dto.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID is required and cannot be null");
        }

        String sql = "UPDATE site_visit_log SET project_id = ?, date = ?, description = ?, status = ? WHERE visit_id = ?";

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, dto.getProjectId());
            ps.setDate(2, Date.valueOf(dto.getDate()));
            ps.setString(3, dto.getDescription());
            ps.setString(4, dto.getStatus());
            ps.setInt(5, dto.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating visit log", e);
        }
    }

    @Override
    public List<VisitRequestDTO> getAllVisitRequests(){
        // Modified to only return pending requests
        String sql = "SELECT * FROM visit_request WHERE status = 'pending'";
        List<VisitRequestDTO> list = new ArrayList<>();

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()){
                VisitRequestDTO dto = new VisitRequestDTO(
                        rs.getString("project_id"),
                        rs.getDate("from_date").toLocalDate(),
                        rs.getDate("to_date").toLocalDate(),
                        rs.getString("purpose"),
                        rs.getString("status")
                );
                dto.setId(rs.getInt("request_id")); // Set the ID from database
                list.add(dto);
            }
        } catch(SQLException e) {
            throw new RuntimeException("Error reading visit requests", e);
        }
        return list;
    }


    @Override
    public boolean updateVisitRequest(VisitRequestDTO dto){
        // Fixed to use request_id instead of project_id for WHERE clause
        String sql = "UPDATE visit_request SET status = ? WHERE request_id = ?";

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setString(1, dto.getStatus());
            ps.setInt(2, dto.getId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating visit request", e);
        }
    }




}