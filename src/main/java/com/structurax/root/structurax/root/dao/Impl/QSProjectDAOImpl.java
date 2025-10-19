package com.structurax.root.structurax.root.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.QSProjectDAO;
import com.structurax.root.structurax.root.dto.DailyUpdateDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientDTO;
import com.structurax.root.structurax.root.dto.SiteVisitWithParticipantsDTO;
import com.structurax.root.structurax.root.dto.VisitParticipantDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;

@Repository
public class QSProjectDAOImpl implements QSProjectDAO {
    
    @Autowired
    private DatabaseConnection databaseConnection;
    
    @Override
    public List<ProjectWithClientDTO> getAllProjects() {
        Map<String, ProjectWithClientDTO> projectMap = new HashMap<>();
        
        final String sql = """
            SELECT p.project_id, p.name, p.description, p.location, p.status, 
                   p.category, p.start_date, p.due_date, p.estimated_value, 
                   p.budget, p.qs_id, p.pm_id, p.ss_id, p.client_id,
                   u.name as client_name, u.email as client_email, 
                   u.phone_number as client_phone, c.type as client_type,
                   u.address as client_address,
                   pi.image_url,
                   qs_user.name as qs_name,
                   pm_user.name as pm_name,
                   ss_user.name as ss_name
            FROM project p
            LEFT JOIN client c ON p.client_id = c.client_id
            LEFT JOIN users u ON c.user_id = u.user_id
            LEFT JOIN project_images pi ON p.project_id = pi.project_id
            LEFT JOIN employee qs_emp ON p.qs_id = qs_emp.employee_id
            LEFT JOIN users qs_user ON qs_emp.user_id = qs_user.user_id
            LEFT JOIN employee pm_emp ON p.pm_id = pm_emp.employee_id
            LEFT JOIN users pm_user ON pm_emp.user_id = pm_user.user_id
            LEFT JOIN employee ss_emp ON p.ss_id = ss_emp.employee_id
            LEFT JOIN users ss_user ON ss_emp.user_id = ss_user.user_id
            ORDER BY p.project_id, pi.image_id
            """;
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            
            while (rs.next()) {
                String projectId = rs.getString("project_id");
                
                ProjectWithClientDTO project = projectMap.get(projectId);
                if (project == null) {
                    project = new ProjectWithClientDTO();
                    project.setProjectId(projectId);
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    project.setLocation(rs.getString("location"));
                    project.setStatus(rs.getString("status"));
                    project.setCategory(rs.getString("category"));
                    
                    if (rs.getDate("start_date") != null) {
                        project.setStartDate(rs.getDate("start_date").toLocalDate());
                    }
                    if (rs.getDate("due_date") != null) {
                        project.setDueDate(rs.getDate("due_date").toLocalDate());
                    }
                    
                    project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                    project.setBudget(rs.getBigDecimal("budget"));
                    project.setQsId(rs.getString("qs_id"));
                    project.setPmId(rs.getString("pm_id"));
                    project.setSsId(rs.getString("ss_id"));
                    project.setQsName(rs.getString("qs_name"));
                    project.setPmName(rs.getString("pm_name"));
                    project.setSsName(rs.getString("ss_name"));
                    project.setClientId(rs.getString("client_id"));
                    project.setClientName(rs.getString("client_name"));
                    project.setClientEmail(rs.getString("client_email"));
                    project.setClientPhone(rs.getString("client_phone"));
                    project.setClientType(rs.getString("client_type"));
                    project.setClientAddress(rs.getString("client_address"));
                    project.setProjectImages(new ArrayList<>());
                    
                    projectMap.put(projectId, project);
                }
                
                // Add image URL if present
                String imageUrl = rs.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    project.getProjectImages().add(imageUrl);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all projects: " + e.getMessage(), e);
        }
        
        return new ArrayList<>(projectMap.values());
    }
    
    @Override
    public List<ProjectWithClientDTO> getProjectsByEmployeeId(String employeeId) {
        Map<String, ProjectWithClientDTO> projectMap = new HashMap<>();
        
        final String sql = """
            SELECT p.project_id, p.name, p.description, p.location, p.status, 
                   p.category, p.start_date, p.due_date, p.estimated_value, 
                   p.budget, p.qs_id, p.pm_id, p.ss_id, p.client_id,
                   u.name as client_name, u.email as client_email, 
                   u.phone_number as client_phone, c.type as client_type,
                   u.address as client_address,
                   pi.image_url,
                   qs_user.name as qs_name,
                   pm_user.name as pm_name,
                   ss_user.name as ss_name
            FROM project p
            LEFT JOIN client c ON p.client_id = c.client_id
            LEFT JOIN users u ON c.user_id = u.user_id
            LEFT JOIN project_images pi ON p.project_id = pi.project_id
            LEFT JOIN employee qs_emp ON p.qs_id = qs_emp.employee_id
            LEFT JOIN users qs_user ON qs_emp.user_id = qs_user.user_id
            LEFT JOIN employee pm_emp ON p.pm_id = pm_emp.employee_id
            LEFT JOIN users pm_user ON pm_emp.user_id = pm_user.user_id
            LEFT JOIN employee ss_emp ON p.ss_id = ss_emp.employee_id
            LEFT JOIN users ss_user ON ss_emp.user_id = ss_user.user_id
            WHERE p.qs_id = ? OR p.pm_id = ? OR p.ss_id = ?
            ORDER BY p.project_id, pi.image_id
            """;
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, employeeId);
            preparedStatement.setString(2, employeeId);
            preparedStatement.setString(3, employeeId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    String projectId = rs.getString("project_id");
                    
                    ProjectWithClientDTO project = projectMap.get(projectId);
                    if (project == null) {
                        project = new ProjectWithClientDTO();
                        project.setProjectId(projectId);
                        project.setName(rs.getString("name"));
                        project.setDescription(rs.getString("description"));
                        project.setLocation(rs.getString("location"));
                        project.setStatus(rs.getString("status"));
                        project.setCategory(rs.getString("category"));
                        
                        if (rs.getDate("start_date") != null) {
                            project.setStartDate(rs.getDate("start_date").toLocalDate());
                        }
                        if (rs.getDate("due_date") != null) {
                            project.setDueDate(rs.getDate("due_date").toLocalDate());
                        }
                        
                        project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                        project.setBudget(rs.getBigDecimal("budget"));
                        project.setQsId(rs.getString("qs_id"));
                        project.setPmId(rs.getString("pm_id"));
                        project.setSsId(rs.getString("ss_id"));
                        project.setQsName(rs.getString("qs_name"));
                        project.setPmName(rs.getString("pm_name"));
                        project.setSsName(rs.getString("ss_name"));
                        project.setClientId(rs.getString("client_id"));
                        project.setClientName(rs.getString("client_name"));
                        project.setClientEmail(rs.getString("client_email"));
                        project.setClientPhone(rs.getString("client_phone"));
                        project.setClientType(rs.getString("client_type"));
                        project.setClientAddress(rs.getString("client_address"));
                        project.setProjectImages(new ArrayList<>());
                        
                        projectMap.put(projectId, project);
                    }
                    
                    // Add image URL if present
                    String imageUrl = rs.getString("image_url");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        project.getProjectImages().add(imageUrl);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching projects by employee ID: " + e.getMessage(), e);
        }
        
        return new ArrayList<>(projectMap.values());
    }
    
    @Override
    public List<DesignDTO> getDesignFilesByProjectId(String projectId) {
        List<DesignDTO> designList = new ArrayList<>();
        
        final String sql = """
            SELECT d.design_id, d.project_id, d.name, d.type, d.due_date, 
                   d.priority, d.price, d.design_link, d.description, 
                   d.additional_note, d.status, d.client_id, d.employee_id,
                   u.name as employee_name, u.type as employee_position
            FROM design d
            LEFT JOIN employee e ON d.employee_id = e.employee_id
            LEFT JOIN users u ON e.user_id = u.user_id
            WHERE d.project_id = ?
            ORDER BY d.due_date DESC
            """;
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    DesignDTO design = new DesignDTO();
                    design.setDesignId(rs.getString("design_id"));
                    design.setProjectId(rs.getString("project_id"));
                    design.setName(rs.getString("name"));
                    design.setType(rs.getString("type"));
                    design.setDueDate(rs.getDate("due_date"));
                    design.setPriority(rs.getString("priority"));
                    design.setPrice(rs.getBigDecimal("price"));
                    design.setDesignLink(rs.getString("design_link"));
                    design.setDescription(rs.getString("description"));
                    design.setAdditionalNote(rs.getString("additional_note"));
                    design.setStatus(rs.getString("status"));
                    design.setClientId(rs.getString("client_id"));
                    design.setEmployeeId(rs.getString("employee_id"));
                    design.setEmployeeName(rs.getString("employee_name"));
                    design.setEmployeePosition(rs.getString("employee_position"));
                    
                    designList.add(design);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching design files by project ID: " + e.getMessage(), e);
        }
        
        return designList;
    }
    
    @Override
    public List<DailyUpdateDTO> getDailyUpdatesByProjectId(String projectId) {
        List<DailyUpdateDTO> dailyUpdateList = new ArrayList<>();
        
        final String sql = """
            SELECT du.update_id, du.project_id, du.date, du.note, du.employee_id,
                   u.name as employee_name
            FROM daily_updates du
            LEFT JOIN employee e ON du.employee_id = e.employee_id
            LEFT JOIN users u ON e.user_id = u.user_id
            WHERE du.project_id = ?
            ORDER BY du.date DESC
            """;
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    DailyUpdateDTO dailyUpdate = new DailyUpdateDTO();
                    dailyUpdate.setUpdateId(rs.getInt("update_id"));
                    dailyUpdate.setProjectId(rs.getString("project_id"));
                    
                    if (rs.getDate("date") != null) {
                        dailyUpdate.setDate(rs.getDate("date").toLocalDate());
                    }
                    
                    dailyUpdate.setNote(rs.getString("note"));
                    dailyUpdate.setEmployeeId(rs.getString("employee_id"));
                    dailyUpdate.setEmployeeName(rs.getString("employee_name"));
                    
                    dailyUpdateList.add(dailyUpdate);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching daily updates by project ID: " + e.getMessage(), e);
        }
        
        return dailyUpdateList;
    }
    
    @Override
    public List<SiteVisitWithParticipantsDTO> getSiteVisitsByProjectId(String projectId) {
        Map<Integer, SiteVisitWithParticipantsDTO> visitMap = new HashMap<>();
        
        final String sql = """
            SELECT sv.visit_id, sv.project_id, sv.date, sv.description, sv.status,
                   vp.employee_id, u.name as employee_name, u.type as employee_type
            FROM site_visit_log sv
            LEFT JOIN visit_person vp ON sv.visit_id = vp.visit_id
            LEFT JOIN employee e ON vp.employee_id = e.employee_id
            LEFT JOIN users u ON e.user_id = u.user_id
            WHERE sv.project_id = ?
            ORDER BY sv.date DESC, sv.visit_id
            """;
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setString(1, projectId);
            
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Integer visitId = rs.getInt("visit_id");
                    
                    SiteVisitWithParticipantsDTO visit = visitMap.get(visitId);
                    if (visit == null) {
                        visit = new SiteVisitWithParticipantsDTO();
                        visit.setVisitId(visitId);
                        visit.setProjectId(rs.getString("project_id"));
                        
                        if (rs.getDate("date") != null) {
                            visit.setDate(rs.getDate("date").toLocalDate());
                        }
                        
                        visit.setDescription(rs.getString("description"));
                        visit.setStatus(rs.getString("status"));
                        visit.setParticipants(new ArrayList<>());
                        
                        visitMap.put(visitId, visit);
                    }
                    
                    // Add participant if present
                    String employeeId = rs.getString("employee_id");
                    if (employeeId != null && !employeeId.isEmpty()) {
                        VisitParticipantDTO participant = new VisitParticipantDTO();
                        participant.setEmployeeId(employeeId);
                        participant.setEmployeeName(rs.getString("employee_name"));
                        participant.setEmployeeType(rs.getString("employee_type"));
                        
                        visit.getParticipants().add(participant);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching site visits by project ID: " + e.getMessage(), e);
        }
        
        return new ArrayList<>(visitMap.values());
    }
}
