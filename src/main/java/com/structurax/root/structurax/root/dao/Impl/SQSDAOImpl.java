package com.structurax.root.structurax.root.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.SQSDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
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
     * Get projects where qs_id is null or empty, returning project name, category, client_id, and client name.
     */
    @Override
    public java.util.List<ProjectInfo> getProjectsWithoutQs() {
        String sql = """
            SELECT p.project_id, p.name, p.category, p.client_id, 
                   u.name as client_name
            FROM project p
            LEFT JOIN client c ON p.client_id = c.client_id
            LEFT JOIN users u ON c.user_id = u.user_id
            WHERE p.qs_id IS NULL OR p.qs_id = ''
            """;
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
                info.setClientName(rs.getString("client_name"));
                projects.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }


    /**
     * Get all QS Officers and SQS Officers from the employee table.
     */
    @Override
    public List<EmployeeDTO> getQSOfficers() {
        List<EmployeeDTO> officers = new ArrayList<>();
        String sql = """
        SELECT 
            e.employee_id,
            e.user_id,
            u.name,
            u.email,
            u.phone_number,
            u.address,
            u.type,
            u.joined_date,
            e.availability,
            u.profile_image_url
        FROM employee e
        INNER JOIN users u ON e.user_id = u.user_id
        WHERE u.type IN ('QS_Officer', 'Senior_QS_Officer')
    """;

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                EmployeeDTO emp = new EmployeeDTO();
                emp.setEmployeeId(rs.getString("employee_id"));
                emp.setUserId(rs.getInt("user_id"));
                
                // Parse the full name into first_name and last_name
                String fullName = rs.getString("name");
                if (fullName != null && !fullName.trim().isEmpty()) {
                    String[] nameParts = fullName.trim().split("\\s+", 2);
                    emp.setFirstName(nameParts[0]);
                    emp.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                    emp.setName(fullName); // Keep full name as fallback
                }
                
                emp.setRole(rs.getString("type"));
                emp.setEmail(rs.getString("email"));
                emp.setPhoneNumber(rs.getString("phone_number"));
                emp.setAddress(rs.getString("address"));
                
                // Handle joined_date
                java.sql.Date joinedDate = rs.getDate("joined_date");
                if (joinedDate != null) {
                    emp.setJoinedDate(joinedDate.toLocalDate());
                }
                
                emp.setAvailability(rs.getString("availability"));
                emp.setProfileImageUrl(rs.getString("profile_image_url"));
                
                officers.add(emp);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching QS Officers: " + e.getMessage(), e);
        }

        return officers;
    }


    /**
     * Get all requests in the system
     */
    @Override
    public java.util.List<com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO> getAllRequests() {
        java.util.List<com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO> requests = new java.util.ArrayList<>();
        String sql = """
            SELECT rsr.*, p.name as project_name, 
                   u_ss.name as site_supervisor_name, 
                   u_qs.name as qs_officer_name 
            FROM request_site_resources rsr 
            LEFT JOIN project p ON rsr.project_id = p.project_id 
            LEFT JOIN employee ss ON rsr.site_supervisor_id = ss.employee_id 
            LEFT JOIN users u_ss ON ss.user_id = u_ss.user_id 
            LEFT JOIN employee qs ON rsr.qs_id = qs.employee_id 
            LEFT JOIN users u_qs ON qs.user_id = u_qs.user_id 
            ORDER BY rsr.date DESC
            """;
        
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO request = 
                    new com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO(
                        rs.getInt("request_id"),
                        rs.getString("pm_approval"),
                        rs.getString("qs_approval"),
                        rs.getString("request_type"),
                        rs.getDate("date"),
                        rs.getString("project_id"),
                        rs.getString("site_supervisor_id"),
                        rs.getString("qs_id"),
                        rs.getBoolean("is_received"),
                        rs.getString("site_supervisor_name"),
                        rs.getString("project_name"),
                        rs.getString("qs_officer_name")
                    );
                
                // Get materials for this request
                java.util.List<com.structurax.root.structurax.root.dto.SiteResourceDTO> materials = 
                    getMaterialsByRequestId(request.getRequestId());
                request.setMaterials(materials);
                
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     * Helper method to get materials by request ID
     */
    private java.util.List<com.structurax.root.structurax.root.dto.SiteResourceDTO> getMaterialsByRequestId(Integer requestId) {
        java.util.List<com.structurax.root.structurax.root.dto.SiteResourceDTO> materials = new java.util.ArrayList<>();
        String sql = "SELECT * FROM site_resources WHERE request_id = ?";
        
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    com.structurax.root.structurax.root.dto.SiteResourceDTO material = 
                        new com.structurax.root.structurax.root.dto.SiteResourceDTO();
                    material.setId(rs.getInt("id"));
                    material.setMaterialName(rs.getString("name"));
                    material.setQuantity(rs.getInt("quantity"));
                    material.setPriority(rs.getString("priority"));
                    material.setRequestId(rs.getInt("request_id"));
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }











    // Helper DTO for project info
    public static class ProjectInfo {
        private String projectId;
        private String name;
        private String category;
        private String clientId;
        private String clientName;

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

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }
    }

}
