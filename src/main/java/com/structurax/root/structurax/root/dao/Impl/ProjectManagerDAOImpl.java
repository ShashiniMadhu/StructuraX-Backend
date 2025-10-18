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
    public List<SiteVisitLogDTO> getSiteVisitLogsByPmId(String pmId) {
        String sql = """
        SELECT svl.visit_id, svl.project_id, svl.date, svl.description, svl.status 
        FROM site_visit_log svl
        INNER JOIN project p ON svl.project_id = p.project_id
        WHERE p.pm_id = ?
        ORDER BY svl.date DESC
        """;

        List<SiteVisitLogDTO> siteVisitLogs = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pmId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SiteVisitLogDTO dto = new SiteVisitLogDTO(
                            rs.getInt("visit_id"),
                            rs.getString("project_id"),
                            rs.getDate("date") != null ? rs.getDate("date").toLocalDate() : null,
                            rs.getString("description"),
                            rs.getString("status")
                    );
                    siteVisitLogs.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching site visit logs by PM ID: " + pmId, e);
        }

        return siteVisitLogs;
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
    public List<VisitRequestDTO> getAllVisitRequests(String pmId) {
        // Modified to only return pending requests
        String sql = "SELECT * FROM visit_request WHERE pm_id = ? AND status = 'pending'";
        List<VisitRequestDTO> list = new ArrayList<>();

        try (
                Connection conn = databaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, pmId); // Set the pmId parameter

            try (ResultSet rs = ps.executeQuery()) {
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
        String sql = "SELECT project_id, name ,status ,budget,description,location,category,estimated_value,start_date,due_date,client_id,qs_id,pm_id,ss_id FROM project WHERE pm_id = ? AND status = ?";
        try (Connection conn = databaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,pmId);
            pstmt.setString(2, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                ProjectInitiateDTO project = new ProjectInitiateDTO();
                project.setProjectId(rs.getString("project_id"));
                project.setName(rs.getString("name"));
                project.setStatus(rs.getString("status"));
                project.setBudget(rs.getBigDecimal("budget"));
                project.setDescription(rs.getString("description"));
                project.setLocation(rs.getString("location"));
                project.setCategory(rs.getString("category"));
                project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                Date startDate = rs.getDate("start_date");
                if (startDate != null) {
                    project.setStartDate(startDate.toLocalDate());
                }
                Date dueDate = rs.getDate("due_date");
                if (dueDate != null) {
                    project.setDueDate(dueDate.toLocalDate());

                }
                project.setClientId(rs.getString("client_id"));
                project.setQsId(rs.getString("qs_id"));
                project.setPmId(rs.getString("pm_id"));
                project.setSsId(rs.getString("ss_id"));
                projects.add(project);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return projects;
   }


    @Override
    public boolean updateRequestSiteResourceApproval(Integer requestId, String pmApproval) {
        String sql = "UPDATE request_site_resources SET pm_approval = ? WHERE request_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pmApproval);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating pm_approval for request " + requestId, e);
        }
    }

    @Override
    public List<RequestSiteResourceDTO> getPendingRequestsByPmId(String pmId) {
        List<RequestSiteResourceDTO> requests = new ArrayList<>();
        String sql = """
        SELECT request_id, project_id, date, site_supervisor_id, pm_id, pm_approval
        FROM request_site_resources 
        WHERE pm_id = ? AND pm_approval = 'pending'
    """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pmId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RequestSiteResourceDTO dto = new RequestSiteResourceDTO();
                dto.setRequestId(rs.getInt("request_id"));
                dto.setProjectId(rs.getString("project_id"));
                dto.setDate(rs.getDate("date").toLocalDate());
                dto.setSiteSupervisorId(rs.getString("site_supervisor_id"));
                dto.setPmId(rs.getString("pm_id"));
                dto.setPmApproval(rs.getString("pm_approval"));
                requests.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching pending requests for PM: " + pmId, e);
        }
        return requests;
    }

    @Override
    public List<SiteResourcesDTO> getSiteResourcesByRequestId(Integer requestId) {
        List<SiteResourcesDTO> resources = new ArrayList<>();
        String sql = """
        SELECT id, request_id, name, quantity, priority
        FROM site_resources 
        WHERE request_id = ?
    """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SiteResourcesDTO dto = new SiteResourcesDTO();
                dto.setId(rs.getInt("id"));
                dto.setRequestId(rs.getInt("request_id"));
                dto.setName(rs.getString("name"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setPriority(rs.getString("priority"));
                resources.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching site resources for request: " + requestId, e);
        }
        return resources;
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

    @Override
    public List<DailyUpdatesDTO> getDailyUpdatesByPmId(String pmId) {
        String sql = "SELECT du.update_id, du.project_id, du.date, du.note, du.employee_id " +
                "FROM daily_updates du " +
                "INNER JOIN project p ON du.project_id = p.project_id " +
                "WHERE p.pm_id = ? " +
                "ORDER BY du.date DESC";

        List<DailyUpdatesDTO> dailyUpdates = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pmId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DailyUpdatesDTO update = new DailyUpdatesDTO(
                            rs.getInt("update_id"),
                            rs.getString("project_id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("note"),
                            rs.getString("employee_id")
                    );
                    dailyUpdates.add(update);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching daily updates for PM " + pmId, e);
        }

        return dailyUpdates;
    }

    @Override
    public List<ProjectInitiateDTO> getProjectsWithNullLocationByPmId(String pmId) {
        List<ProjectInitiateDTO> projects = new ArrayList<>();
        String sql = "SELECT project_id, name, status, budget, description, location, " +
                "estimated_value, start_date, due_date, client_id, qs_id, pm_id, ss_id, category " +
                "FROM project WHERE pm_id = ? AND (location IS NULL OR location = '')";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pmId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProjectInitiateDTO project = new ProjectInitiateDTO();
                project.setProjectId(rs.getString("project_id"));
                project.setName(rs.getString("name"));
                project.setStatus(rs.getString("status"));
                project.setBudget(rs.getBigDecimal("budget"));
                project.setDescription(rs.getString("description"));
                project.setLocation(rs.getString("location"));
                project.setEstimatedValue(rs.getBigDecimal("estimated_value"));

                // Handle dates safely
                Date startDate = rs.getDate("start_date");
                if (startDate != null) {
                    project.setStartDate(startDate.toLocalDate());
                }

                Date dueDate = rs.getDate("due_date");
                if (dueDate != null) {
                    project.setDueDate(dueDate.toLocalDate());
                }

                project.setClientId(rs.getString("client_id"));
                project.setQsId(rs.getString("qs_id"));
                project.setPmId(rs.getString("pm_id"));
                project.setSsId(rs.getString("ss_id"));
                project.setCategory(rs.getString("category"));

                projects.add(project);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching projects with null location for PM: " + pmId, e);
        }

        return projects;
    }

    @Override
    public boolean updateProjectLocation(String projectId, String location) {
        String sql = "UPDATE project SET location = ? WHERE project_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, location);
            ps.setString(2, projectId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating project location for project: " + projectId, e);
        }
    }


    @Override
    public boolean insertProjectMaterials(ProjectMaterialsDTO projectMaterials) {
        String sql = "INSERT INTO project_materials (project_id, tools) VALUES (?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectMaterials.getProjectId());
            ps.setString(2, projectMaterials.getTools());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting project material for project: " + projectMaterials.getProjectId(), e);
        }
    }

    @Override
    public List<ProjectInitiateDTO> getCompletedProjectsByPmId(String pmId) {
        return getProjectsByPmIdAndStatus(pmId, "completed");
    }

    @Override
    public List<DesignDTO>getDesignLink(String projectId) {
        List<DesignDTO> designLink = new ArrayList<>();
        String sql = "SELECT d.design_link, d.description, d.due_date, d.name FROM design d WHERE d.project_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, projectId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    DesignDTO design = new DesignDTO();
                    design.setDesignLink(resultSet.getString("design_link"));
                    design.setDescription(resultSet.getString("description"));
                    Date dueDate = resultSet.getDate("due_date");
                    if (dueDate != null) {
                        design.setDueDate(dueDate);
                    }
                    design.setName(resultSet.getString("name"));
                    designLink.add(design);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return designLink;
    }

    @Override
    public List<WBSDTO> getWBSByProjectId(String projectId) {
        List<WBSDTO> wbsList = new ArrayList<>();
        String query = "SELECT * FROM wbs WHERE project_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, projectId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    WBSDTO wbs = new WBSDTO();
                    wbs.setTaskId(resultSet.getInt("task_id"));
                    wbs.setProjectId(resultSet.getString("project_id"));
                    wbs.setParentId(resultSet.getInt("parent_id"));
                    wbs.setName(resultSet.getString("name"));
                    wbs.setStatus(resultSet.getString("status"));
                    wbs.setMilestone(resultSet.getBoolean("milestone"));
                    wbsList.add(wbs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to throw a custom exception here
        }

        return wbsList;
    }

    @Override
    public List<BOQitemDTO> getBOQItemsByProjectId(String projectId) {
        List<BOQitemDTO> boqItems = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection()) {
            // First get the BOQ ID for the project
            String boqQuery = "SELECT boq_id FROM boq WHERE project_id = ?";
            PreparedStatement boqStmt = connection.prepareStatement(boqQuery);
            boqStmt.setString(1, projectId);
            ResultSet boqRs = boqStmt.executeQuery();

            if (boqRs.next()) {
                String boqId = boqRs.getString("boq_id");

                // Then get all BOQ items for this BOQ ID
                String itemsQuery = "SELECT * FROM boq_item WHERE boq_id = ?";
                PreparedStatement itemsStmt = connection.prepareStatement(itemsQuery);
                itemsStmt.setString(1, boqId);
                ResultSet itemsRs = itemsStmt.executeQuery();

                while (itemsRs.next()) {
                    BOQitemDTO item = new BOQitemDTO(
                            itemsRs.getString("item_id"),
                            itemsRs.getString("boq_id"),
                            itemsRs.getString("item_description"),
                            itemsRs.getDouble("rate"),
                            itemsRs.getString("unit"),
                            itemsRs.getDouble("quantity"),
                            itemsRs.getDouble("amount")
                    );
                    boqItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider throwing a custom exception or handling the error appropriately
        }

        return boqItems;
    }


    @Override
    public List<PaymentDTO> getPaymentByProjectId(String projectId) {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = """
        SELECT p.* FROM payment p 
        INNER JOIN payment_confirmation pc ON p.payment_id = pc.payment_id 
        WHERE pc.project_id = ?
    """;

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, projectId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PaymentDTO payment = new PaymentDTO();
                payment.setPaymentId(resultSet.getInt("payment_id"));
                payment.setInvoiceId(resultSet.getInt("invoice_id"));
                payment.setDuedate(resultSet.getDate("due_date") != null ?
                        resultSet.getDate("due_date").toLocalDate() : null);
                payment.setPaiddate(resultSet.getDate("paid_date") != null ?
                        resultSet.getDate("paid_date").toLocalDate() : null);
                payment.setStatus(resultSet.getString("status"));
                payment.setAmount(resultSet.getDouble("amount"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Log the error properly
        }
        return payments;
    }

    @Override
    public List<ProjectMaterialsDTO> getProjectMaterialsByProjectId(String projectId) {
        List<ProjectMaterialsDTO> materials = new ArrayList<>();
        String sql = "SELECT * FROM project_materials WHERE project_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, projectId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ProjectMaterialsDTO material = new ProjectMaterialsDTO();
                    material.setMaterialId(resultSet.getInt("materials_id"));
                    material.setProjectId(resultSet.getString("project_id"));
                    material.setTools(resultSet.getString("tools"));
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    @Override
    public List<String> getOngoingProjectIds() {
        List<String> ongoingProjectIds = new ArrayList<>();
        String sql = "SELECT project_id FROM project WHERE status = 'ongoing'";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ongoingProjectIds.add(resultSet.getString("project_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ongoingProjectIds;
    }



}