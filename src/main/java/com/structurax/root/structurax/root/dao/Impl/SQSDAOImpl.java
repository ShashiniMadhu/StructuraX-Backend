package com.structurax.root.structurax.root.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.SQSDAO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;

@Repository
public class SQSDAOImpl implements SQSDAO {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Override
    public Project1DTO getProjectById(String id) {
        String sql = "SELECT * FROM project WHERE project_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Project1DTO project = new Project1DTO();
                    project.setProjectId(rs.getString("project_id"));
                    project.setName(rs.getString("name"));
                    project.setStatus(rs.getString("status"));
                    project.setBudget(rs.getBigDecimal("budget"));
                    project.setDescription(rs.getString("description"));
                    project.setLocation(rs.getString("location"));
                    project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                    project.setStartDate(rs.getDate("start_date").toLocalDate());
                    project.setDueDate(rs.getDate("due_date").toLocalDate());
                    project.setOwnerId(rs.getString("client_id"));
                    project.setQsId(rs.getString("qs_id"));
                    project.setSpId(rs.getString("pm_id"));
                    project.setCategory(rs.getString("category"));
                    return project;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean assignQsToProject(String pid, String eid) {
        String sql = "UPDATE project SET qs_id = ? WHERE project_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eid);
            stmt.setString(2, pid);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get projects where qs_id is null or empty, returning project name, category, and client_id.
     */
    public java.util.List<ProjectInfo> getProjectsWithoutQs() {
        String sql = "SELECT project_id, name, category, client_id FROM project WHERE qs_id IS NULL OR qs_id = ''";
        java.util.List<ProjectInfo> projects = new java.util.ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ProjectInfo info = new ProjectInfo();
                info.setProjectId(rs.getString("project_id"));
                info.setName(rs.getString("name"));
                info.setCategory(rs.getString("category"));
                info.setClientId(rs.getString("client_id"));
                projects.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }


    /**
     * Get all QS Officers from the employee table.
     */
    @Override
    public java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> getQSOfficers() {
        java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> officers = new java.util.ArrayList<>();
        String sql = "SELECT * FROM employee WHERE type = 'QS_Officer'";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                com.structurax.root.structurax.root.dto.EmployeeDTO emp = new com.structurax.root.structurax.root.dto.EmployeeDTO();
                emp.setEmployeeId(rs.getString("employee_id"));
                emp.setName(rs.getString("name"));
                emp.setEmail(rs.getString("email"));
                emp.setPhoneNumber(rs.getString("phone_number"));
                emp.setAddress(rs.getString("address"));
                emp.setType(rs.getString("type"));
                java.sql.Date joinedDate = rs.getDate("joined_date");
                if (joinedDate != null) {
                    emp.setJoinedDate(joinedDate.toLocalDate());
                }
                emp.setPassword(rs.getString("password"));
                emp.setAvailability(rs.getString("availability"));
                emp.setProfileImageUrl(rs.getString("profile_image_url"));
                officers.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return officers;
    }











    // Helper DTO for project info
    public static class ProjectInfo {
        private String projectId;
        private String name;
        private String category;
        private String clientId;

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }

}
