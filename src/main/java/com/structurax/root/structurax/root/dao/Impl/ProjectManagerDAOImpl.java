package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ProjectManagerDAO;
import com.structurax.root.structurax.root.dto.*;
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

   @Override
    public List<ProjectInitiateDTO> getProjectsByPmIdAndStatus(String pmId , String status){
        List<ProjectInitiateDTO> projects = new ArrayList<>();
        String sql = "SELECT project_id, name FROM project WHERE pm_id = ? AND status = ?";
        try (Connection conn = databaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,pmId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                ProjectInitiateDTO project = new ProjectInitiateDTO();
                project.setProjectId(rs.getString("project_id"));
                project.setName(rs.getString("name"));
                projects.add(project);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return projects;
   }

    @Override
    public List<RequestSiteResourceDTO> getRequestSiteResourcesByPmId(String pmId) {
        String sql = "SELECT request_id, pm_approval, qs_approval, request_type, date, "
                + "project_id, site_supervisor_id, qs_id, pm_id, is_received "
                + "FROM request_site_resources "
                + "WHERE pm_id = ? AND is_received = 'Pending'";

        List<RequestSiteResourceDTO> results = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pmId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestSiteResourceDTO dto = new RequestSiteResourceDTO();
                    dto.setRequestId(rs.getInt("request_id"));
                    dto.setPmApproval(rs.getBoolean("pm_approval"));
                    dto.setQsApproval(rs.getBoolean("qs_approval"));
                    dto.setRequestType(rs.getString("request_type"));
                    dto.setDate(rs.getDate("date").toLocalDate());
                    dto.setProjectId(rs.getString("project_id"));
                    dto.setSiteSupervisorId(rs.getString("site_supervisor_id"));
                    dto.setQsId(rs.getString("qs_id"));
                    dto.setPmId(rs.getString("pm_id"));
                    dto.setIsReceived(rs.getString("is_received"));
                    results.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching pending request-site resources for PM " + pmId, e);
        }
        return results;
    }

    @Override
    public boolean updateRequestSiteResourceApproval(Integer requestId, boolean pmApproval) {
        String sql = "UPDATE request_site_resources SET pm_approval = ? WHERE request_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, pmApproval);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating pm_approval for request " + requestId, e);
        }
    }

    @Override
    public List<TodoDTO> getTodosByEmployeeId(String employeeId) {
        List<TodoDTO> todos = new ArrayList<>();
        String sql = "SELECT task_id, employee_id, status, description, date FROM to_do WHERE employee_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                todos.add(new TodoDTO(
                        rs.getInt("task_id"),
                        rs.getString("employee_id"),
                        rs.getString("status"),
                        rs.getString("description"),
                        rs.getDate("date")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching todos", e);
        }
        return todos;
    }

    @Override
    public TodoDTO createTodo(TodoDTO todo) {
        String sql = "INSERT INTO to_do (employee_id, status, description, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, todo.getEmployeeId());
            ps.setString(2, todo.getStatus());
            ps.setString(3, todo.getDescription());
            ps.setDate(4, todo.getDate());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Creating todo failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    todo.setTaskId(keys.getInt(1));
                }
            }
            return todo;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating todo", e);
        }
    }

    @Override
    public boolean updateTodo(TodoDTO todo) {
        String sql = "UPDATE to_do SET status = ?, description = ?, date = ? WHERE task_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, todo.getStatus());
            ps.setString(2, todo.getDescription());
            ps.setDate(3, todo.getDate());
            ps.setInt(4, todo.getTaskId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating todo", e);
        }
    }

    @Override
    public boolean deleteTodo(Integer taskId) {
        String sql = "DELETE FROM to_do WHERE task_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting todo", e);
        }
    }







}