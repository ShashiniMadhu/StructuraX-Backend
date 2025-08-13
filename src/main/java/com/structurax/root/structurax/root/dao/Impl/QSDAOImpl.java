package com.structurax.root.structurax.root.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.QSDAO;
import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientAndBOQDTO;
import com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO;
import com.structurax.root.structurax.root.dto.SiteResourceDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;

@Repository
public class QSDAOImpl implements QSDAO {
    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private com.structurax.root.structurax.root.dao.BOQDAO boqdao;
    @Override
    public List<Project1DTO> getProjectsByQSId(String qsId) {
        List<Project1DTO> projectList = new ArrayList<>();
        final String sql = "SELECT * FROM project WHERE qs_id = ?";
        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, qsId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Project1DTO project = new Project1DTO();
                    project.setProjectId(rs.getString("project_id"));
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    project.setLocation(rs.getString("location"));
                    project.setStatus(rs.getString("status"));
                    if (rs.getDate("start_date") != null)
                        project.setStartDate(rs.getDate("start_date").toLocalDate());
                    if (rs.getDate("due_date") != null)
                        project.setDueDate(rs.getDate("due_date").toLocalDate());
                    project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                    project.setQsId(rs.getString("qs_id"));
                    project.setBudget(rs.getBigDecimal("budget"));
                    project.setCategory(rs.getString("category"));
                    projectList.add(project);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching projects by QS ID: " + e.getMessage(), e);
        }
        return projectList;
    }

    @Override
    public List<ProjectWithClientAndBOQDTO> getProjectsWithClientAndBOQByQSId(String qsId) {
        List<ProjectWithClientAndBOQDTO> projectList = new ArrayList<>();
        final String sql = """
            SELECT p.*, c.client_id, c.first_name, c.last_name, c.email, 
                   c.contact_number, c.type, c.is_have_plan, c.address
            FROM project p 
            LEFT JOIN client c ON p.client_id = c.client_id 
            WHERE p.qs_id = ?
            """;
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, qsId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    ProjectWithClientAndBOQDTO projectWithData = new ProjectWithClientAndBOQDTO();
                    projectWithData.setProjectId(rs.getString("project_id"));
                    projectWithData.setName(rs.getString("name"));
                    projectWithData.setDescription(rs.getString("description"));
                    projectWithData.setLocation(rs.getString("location"));
                    projectWithData.setStatus(rs.getString("status"));
                    if (rs.getDate("start_date") != null)
                        projectWithData.setStartDate(rs.getDate("start_date").toLocalDate());
                    if (rs.getDate("due_date") != null)
                        projectWithData.setDueDate(rs.getDate("due_date").toLocalDate());
                    projectWithData.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                    projectWithData.setQsId(rs.getString("qs_id"));
                    projectWithData.setBudget(rs.getBigDecimal("budget"));
                    projectWithData.setCategory(rs.getString("category"));
                    if (rs.getString("client_id") != null) {
                        ClientDTO clientData = new ClientDTO();
                        clientData.setClientId(rs.getString("client_id"));
                        clientData.setFirstName(rs.getString("first_name"));
                        clientData.setLastName(rs.getString("last_name"));
                        clientData.setEmail(rs.getString("email"));
                        clientData.setContactNumber(rs.getString("contact_number"));
                        clientData.setType(rs.getString("type"));
                        clientData.setIsHavePlan(rs.getBoolean("is_have_plan"));
                        clientData.setAddress(rs.getString("address"));
                        projectWithData.setClientData(clientData);
                    }
                    BOQWithItemsDTO boqData = getBOQByProjectId(rs.getString("project_id"));
                    projectWithData.setBoqData(boqData);
                    projectList.add(projectWithData);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching projects with client and BOQ data by QS ID: " + e.getMessage(), e);
        }
        return projectList;
    }

    private BOQWithItemsDTO getBOQByProjectId(String projectId) {
        final String sql = "SELECT * FROM boq WHERE project_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, projectId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    BOQDTO boqDto = new BOQDTO();
                    boqDto.setBoqId(rs.getString("boq_id"));
                    boqDto.setProjectId(rs.getString("project_id"));
                    if (rs.getDate("date") != null)
                        boqDto.setDate(rs.getDate("date").toLocalDate());
                    boqDto.setQsId(rs.getString("qs_id"));
                    String status = rs.getString("status");
                    if (status != null) {
                        boqDto.setStatus(BOQDTO.Status.valueOf(status.toUpperCase()));
                    }
                    List<BOQitemDTO> items = boqdao.getBOQItemsByBOQId(boqDto.getBoqId());
                    return new BOQWithItemsDTO(boqDto, items);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching BOQ for project " + projectId + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<RequestSiteResourcesDTO> getRequestsByQSId(String qsId) {
        List<RequestSiteResourcesDTO> requests = new ArrayList<>();
        final String sql = "SELECT * FROM request_site_resources WHERE qs_id = ? AND pm_approval = '1'";
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, qsId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    RequestSiteResourcesDTO request = new RequestSiteResourcesDTO(
                            rs.getInt("request_id"),
                            rs.getString("pm_approval"),
                            rs.getString("qs_approval"),
                            rs.getString("request_type"),
                            rs.getDate("date"),
                            rs.getString("project_id"),
                            rs.getString("site_supervisor_id"),
                            rs.getString("qs_id"),
                            rs.getBoolean("is_received")
                    );
                    
                    // Get materials/resources for this request
                    List<SiteResourceDTO> materials = getMaterialsByRequestId(request.getRequestId());
                    request.setMaterials(materials);
                    
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching site resource requests by QS ID: " + e.getMessage(), e);
        }
        return requests;
    }
    
    private List<SiteResourceDTO> getMaterialsByRequestId(Integer requestId) {
        final String sql = "SELECT * FROM site_resources WHERE request_id = ?";
        List<SiteResourceDTO> materialList = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, requestId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    SiteResourceDTO dto = new SiteResourceDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setMaterialName(rs.getString("name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setPriority(rs.getString("priority"));
                    dto.setRequestId(rs.getInt("request_id"));
                    materialList.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching materials by request ID: " + e.getMessage(), e);
        }
        return materialList;
    }
    
    @Override
    public boolean updateRequestQSApproval(Integer requestId, String qsApproval) {
        final String sql = "UPDATE request_site_resources SET qs_approval = ? WHERE request_id = ?";
        
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, qsApproval);
            preparedStatement.setInt(2, requestId);
            
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating QS approval for request ID " + requestId + ": " + e.getMessage(), e);
        }
    }
}
