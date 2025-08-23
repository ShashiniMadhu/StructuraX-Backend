package com.structurax.root.structurax.root.dao.Impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.SiteSupervisorDAO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.LaborAttendanceDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO;
import com.structurax.root.structurax.root.dto.SiteResourceDTO;
import com.structurax.root.structurax.root.dto.TodoDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;

@Repository
public class SiteSupervisorDAOImpl implements SiteSupervisorDAO {

    @Autowired
    private DatabaseConnection databaseConnection;


    @Override
    public List<ProjectDTO> getProjectsBySsId(String id) {
        final String sql = "SELECT p.*, c.first_name,c.last_name FROM project p " +
                "INNER JOIN client c ON p.client_id=c.client_id  WHERE ss_id = ?";

        List<ProjectDTO> projectDTOList = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    ProjectDTO dto = new ProjectDTO();
                    dto.setProjectId(rs.getString("project_id"));
                    dto.setName(rs.getString("name"));

                    dto.setStatus(rs.getString("status"));
                    dto.setBudget(rs.getBigDecimal("budget"));
                    dto.setDescription(rs.getString("description"));
                    dto.setLocation(rs.getString("location"));
                    dto.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                    if (rs.getDate("start_date") != null) {
                        dto.setStartDate(rs.getDate("start_date").toLocalDate());
                    }
                    if (rs.getDate("due_date") != null) {
                        dto.setDueDate(rs.getDate("due_date").toLocalDate());
                    }

                    dto.setClientId(rs.getString("client_id"));
                    dto.setQsId(rs.getString("qs_id"));
                    dto.setPmId(rs.getString("pm_id"));
                    dto.setSsId(rs.getString("ss_id"));
                    dto.setCategory(rs.getString("category"));



                    ClientDTO client = new ClientDTO();
                    client.setFirstName(rs.getString("first_name"));
                    client.setLastName(rs.getString("last_name"));
                    dto.setClient(client);

                    for (ProjectDTO p : projectDTOList) {
                        System.out.println("Project: " + p.getName() + ", Client: " + p.getClient().getFirstName() + " " + p.getClient().getLastName());
                    }

                    projectDTOList.add(dto);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching installments by Payment Plan ID: " + e.getMessage(), e);
        }

        return projectDTOList;
    }

    @Override
    public List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendanceList) {
        final String sql = "INSERT INTO labor_attendance (project_id, date, hiring_type, labor_type, count, company_name) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            for (LaborAttendanceDTO dto : laborAttendanceList) {
                preparedStatement.setString(1, dto.getProject_id());
                preparedStatement.setDate(2, dto.getDate());
                preparedStatement.setString(3, dto.getHiring_type());
                preparedStatement.setString(4, dto.getLabor_type());
                preparedStatement.setInt(5, dto.getCount());
                preparedStatement.setString(6, dto.getCompany());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            // Optionally, you can return the input list or fetch generated keys if you want
            return laborAttendanceList;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting labor attendance batch: " + e.getMessage(), e);
        }
    }


    @Override
    public List<LaborAttendanceDTO> getAllLaborAttendance() {
        List<LaborAttendanceDTO> laborAttendance = new ArrayList<>();
        final String sql = "SELECT * FROM labor_attendance";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                LaborAttendanceDTO laborAttendanceRecord = new LaborAttendanceDTO();
                laborAttendanceRecord.setId(rs.getInt("attendance_id"));
                laborAttendanceRecord.setProject_id(rs.getString("project_id"));
                laborAttendanceRecord.setDate(rs.getDate("date"));
                laborAttendanceRecord.setHiring_type(rs.getString("hiring_type"));
                laborAttendanceRecord.setLabor_type(rs.getString("labor_type"));
                laborAttendanceRecord.setCount(rs.getInt("count"));
                laborAttendanceRecord.setCompany(rs.getString("company_name"));

                if (rs.getDate("date") != null) {
                    laborAttendanceRecord.setDate(Date.valueOf(rs.getDate("date").toLocalDate()));
                }




                laborAttendance.add(laborAttendanceRecord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return laborAttendance;
    }

    @Override
    public LaborAttendanceDTO updateLaborAttendance(LaborAttendanceDTO laborAttendanceDTO) {
        final String sql = "UPDATE labor_attendance SET project_id = ?, date = ?, hiring_type = ?, labor_type = ?, count = ?, company_name = ? " +
                "WHERE attendance_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {


            preparedStatement.setString(1, laborAttendanceDTO.getProject_id());
            preparedStatement.setDate(2, laborAttendanceDTO.getDate());
            preparedStatement.setString(3, laborAttendanceDTO.getHiring_type());
            preparedStatement.setString(4, laborAttendanceDTO.getLabor_type());
            preparedStatement.setInt(5, laborAttendanceDTO.getCount());
            preparedStatement.setString(6, laborAttendanceDTO.getCompany());
            preparedStatement.setInt(7, laborAttendanceDTO.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No labor attendance record found with id=" + laborAttendanceDTO.getId());
            }

            return laborAttendanceDTO;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating labor attendance: " + e.getMessage(), e);
        }
    }




    @Override
    public List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(String project_id, java.sql.Date date) {
        final String sql = "SELECT * FROM labor_attendance l INNER JOIN project p ON p.project_id=l.project_id" +
                " WHERE l.project_id = ? AND DATE(l.date) = ?";

        List<LaborAttendanceDTO> laborAttendanceDTOS = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, project_id);
            preparedStatement.setDate(2, date);  // java.sql.Date is fine here

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    LaborAttendanceDTO dto = new LaborAttendanceDTO();
                    dto.setId(rs.getInt("id"));            // or rs.getLong if ID is long
                    dto.setProject_id(rs.getString("project_id"));
                    dto.setDate(rs.getDate("date"));
                    dto.setHiring_type(rs.getString("hiring_type"));
                    dto.setLabor_type(rs.getString("labor_type"));
                    dto.setCount(rs.getInt("count"));
                    dto.setCompany(rs.getString("company"));

                    laborAttendanceDTOS.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching labor attendance by projectId and date: " + e.getMessage(), e);
        }

        return laborAttendanceDTOS;
    }

    @Override
    public List<LaborAttendanceDTO> deleteLaborAttendanceRecord(String project_id, Date date) {
        final String selectSql = "SELECT * FROM labor_attendance WHERE project_id = ? AND date = ?";
        final String deleteSql = "DELETE FROM labor_attendance WHERE project_id = ? AND date = ?";
        List<LaborAttendanceDTO> deletedRecords = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection()) {

            // Step 1: Fetch records before deleting
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setString(1, project_id);
                selectStmt.setDate(2, date);
                ResultSet rs = selectStmt.executeQuery();

                while (rs.next()) {
                    LaborAttendanceDTO dto = new LaborAttendanceDTO();
                    dto.setId(rs.getInt("attendance_id"));
                    dto.setProject_id(rs.getString("project_id"));
                    dto.setDate(rs.getDate("date"));
                    dto.setHiring_type(rs.getString("hiring_type"));
                    dto.setLabor_type(rs.getString("labor_type"));
                    dto.setCount(rs.getInt("count"));
                    dto.setCompany(rs.getString("company_name"));

                    deletedRecords.add(dto);
                }
            }

            // Step 2: Delete the records
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, project_id);
                deleteStmt.setDate(2, date);
                deleteStmt.executeUpdate();
            }

            return deletedRecords;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting labor attendance records: " + e.getMessage(), e);
        }
    }



    //material request crud
    @Override
    public List<SiteResourceDTO> getMaterialsByRequestId(Integer id) {
        final String sql = "SELECT * FROM site_resources m " +
                "INNER JOIN request_site_resources r ON m.request_id = r.request_id " +
                "WHERE m.request_id = ?";

        List<SiteResourceDTO> materialList = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    SiteResourceDTO dto = new SiteResourceDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setMaterialName(rs.getString("name"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setPriority(rs.getString("priority"));
                    dto.setRequestId(rs.getInt("request_id"));

                    materialList.add(dto);
                    ;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching material list by request ID: " + e.getMessage(), e);
        }

        return materialList;
    }


    /*@Override
    public RequestDTO getRequestById(Integer id) {
        final String sql = "SELECT * FROM request WHERE request_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new RequestDTO(
                            rs.getInt("request_id"),
                            rs.getString("approval_status"),
                            rs.getString("request_type"),
                            rs.getDate("date"),
                            rs.getInt("project_id")
                    );


                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching request by ID: " + e.getMessage(), e);
        }

        return null;
    }*/

    @Override
    public List<RequestSiteResourcesDTO> getAllMaterialRequests() {
        final String sql = "SELECT rsr.*, p.name as project_name, " +
                          "ss.name as site_supervisor_name, " +
                          "qs.name as qs_officer_name " +
                          "FROM request_site_resources rsr " +
                          "LEFT JOIN project p ON rsr.project_id = p.project_id " +
                          "LEFT JOIN employee ss ON rsr.site_supervisor_id = ss.employee_id " +
                          "LEFT JOIN employee qs ON rsr.qs_id = qs.employee_id " +
                          "WHERE rsr.request_type='material'";
        List<RequestSiteResourcesDTO> requests = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
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
                        rs.getBoolean("is_received"),
                        rs.getString("site_supervisor_name"),
                        rs.getString("project_name"),
                        rs.getString("qs_officer_name")
                );

                List<SiteResourceDTO> materials = getMaterialsByRequestId(request.getRequestId());
                request.setMaterials(materials);

                requests.add(request);
            }

            return requests;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching requests: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RequestSiteResourcesDTO> getAllToolRequests() {
        final String sql = "SELECT rsr.*, p.name as project_name, " +
                          "ss.name as site_supervisor_name, " +
                          "qs.name as qs_officer_name " +
                          "FROM request_site_resources rsr " +
                          "LEFT JOIN project p ON rsr.project_id = p.project_id " +
                          "LEFT JOIN employee ss ON rsr.site_supervisor_id = ss.employee_id " +
                          "LEFT JOIN employee qs ON rsr.qs_id = qs.employee_id " +
                          "WHERE rsr.request_type='tool'";
        List<RequestSiteResourcesDTO> requests = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
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
                        rs.getBoolean("is_received"),
                        rs.getString("site_supervisor_name"),
                        rs.getString("project_name"),
                        rs.getString("qs_officer_name")
                );

                List<SiteResourceDTO> materials = getMaterialsByRequestId(request.getRequestId());
                request.setMaterials(materials);

                requests.add(request);
            }

            return requests;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching requests: " + e.getMessage(), e);
        }
    }



    @Override
    public RequestSiteResourcesDTO createMaterialRequest(RequestSiteResourcesDTO requestDTO) {
        final String planSql = "INSERT INTO request_site_resources (pm_approval, qs_approval, request_type,date,project_id,site_supervisor_id,qs_id,is_received) " +
                "VALUES (?, ?, ?, ?,?,?,?,? )";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement planStmt = connection.prepareStatement(planSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            // 1. Insert request
            planStmt.setString(1, requestDTO.getPmApproval());
            planStmt.setString(2, requestDTO.getQsApproval());
            planStmt.setString(3, requestDTO.getRequestType());
            planStmt.setDate(4,requestDTO.getDate());
            planStmt.setString(5,requestDTO.getProjectId());
            planStmt.setString(6,requestDTO.getSiteSupervisorId());
            planStmt.setString(7,requestDTO.getQsId());
            planStmt.setBoolean(8,requestDTO.getIsReceived());

            int rows = planStmt.executeUpdate();
            if (rows == 0) throw new SQLException("Creating material request failed.");

            // 2. Get generated request_id
            try (ResultSet generatedKeys = planStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedRequestId = generatedKeys.getInt(1);
                    requestDTO.setRequestId(generatedRequestId);

                    // 3. Insert each material
                    String materialSql = "INSERT INTO site_resources (request_id,name, quantity, priority) " +
                            "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement materialStmt = connection.prepareStatement(materialSql)) {
                        for (SiteResourceDTO material : requestDTO.getMaterials()) {

                            materialStmt.setInt(1, generatedRequestId);
                            materialStmt.setString(2, material.getMaterialName());
                            materialStmt.setInt(3, material.getQuantity());
                            materialStmt.setString(4, material.getPriority());

                            materialStmt.addBatch(); // Batch insert for efficiency
                        }
                        materialStmt.executeBatch();
                    }
                } else {
                    throw new SQLException("Failed to retrieve request id.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating material request: " + e.getMessage(), e);
        }

        return requestDTO;
    }

    @Override
    public RequestSiteResourcesDTO updateRequest(RequestSiteResourcesDTO requestSiteResourcesDTO) {
        Connection connection = null;
        try {
            connection = databaseConnection.getConnection();
            connection.setAutoCommit(false);  // begin transaction

            final String sqlUpdateRequest = "UPDATE request_site_resources SET is_received = ?, project_id = ? " +
            "WHERE request_id = ? AND pm_approval = 'pending' AND qs_approval = 'pending'";;
            try (PreparedStatement psPlan = connection.prepareStatement(sqlUpdateRequest)) {

                psPlan.setBoolean(1, requestSiteResourcesDTO.getIsReceived());

                psPlan.setString(2, requestSiteResourcesDTO.getProjectId());
                psPlan.setInt(3, requestSiteResourcesDTO.getRequestId());


                int updatedRequestRows = psPlan.executeUpdate();
                if (updatedRequestRows == 0) {
                    throw new RuntimeException("Request update failed. Either it's already approved or the Site Supervisor ID is incorrect.");
                }
            }

            final String sqlUpdateResources = "UPDATE site_resources SET name = ?, quantity = ?, priority = ? WHERE id = ?";
            try (PreparedStatement psResources = connection.prepareStatement(sqlUpdateResources)) {
                for (SiteResourceDTO resource : requestSiteResourcesDTO.getMaterials()) {
                    if (resource.getId() == 0) {
                        throw new RuntimeException("Invalid resource ID for update: " + resource);
                    }


                    psResources.setString(1, resource.getMaterialName());
                    psResources.setInt(2, resource.getQuantity());
                    psResources.setString(3, resource.getPriority());

                    psResources.setInt(4, resource.getId());



                    int updateResourcesRows = psResources.executeUpdate();
                    if (updateResourcesRows == 0) {
                        throw new RuntimeException("Resource update failed. ID may not exist: " + resource.getId());
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Rollback failed: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw new RuntimeException("Error updating full resource request: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {}
            }
        }

        return requestSiteResourcesDTO;
    }

    @Override
    public RequestSiteResourcesDTO getRequestById(int requestId) {
        final String sql = "SELECT rsr.*, p.name as project_name, " +
                          "ss.name as site_supervisor_name, " +
                          "qs.name as qs_officer_name " +
                          "FROM request_site_resources rsr " +
                          "LEFT JOIN project p ON rsr.project_id = p.project_id " +
                          "LEFT JOIN employee ss ON rsr.site_supervisor_id = ss.employee_id " +
                          "LEFT JOIN employee qs ON rsr.qs_id = qs.employee_id " +
                          "WHERE rsr.request_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, requestId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new RequestSiteResourcesDTO(
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
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching request by ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public RequestSiteResourcesDTO deleteRequest(int requestId) {
        RequestSiteResourcesDTO request = getRequestById(requestId);
        if (request == null) {
            throw new RuntimeException("No request found with project id : " + requestId);
        }

        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false); // start transaction

            // Delete installments first
            final String sqlDeleteInstallments = "DELETE FROM site_resources WHERE request_id = ?";
            try (PreparedStatement psInstallments = connection.prepareStatement(sqlDeleteInstallments)) {
                psInstallments.setInt(1, request.getRequestId());
                psInstallments.executeUpdate();
            }

            // Delete payment plan
            final String sqlDeletePaymentPlan = "DELETE FROM request_site_resources WHERE request_id = ?";
            try (PreparedStatement psPlan = connection.prepareStatement(sqlDeletePaymentPlan)) {
                psPlan.setInt(1, requestId);
                psPlan.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting request: " + e.getMessage(), e);
        }

        return request;
    }

    @Override
    public TodoDTO createToDo(TodoDTO todoDTO) {
        final String sql = "INSERT INTO to_do (employee_id, status, description, date) VALUES (?, ?, ?, ?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, todoDTO.getEmployeeId());
            preparedStatement.setString(2, todoDTO.getStatus());
            preparedStatement.setString(3, todoDTO.getDescription());
            preparedStatement.setDate(4, todoDTO.getDate());

            preparedStatement.executeUpdate();

            return todoDTO;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting to-do: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TodoDTO> getToDoBySpId(String id) {
        final String sql = "SELECT td.task_id, td.employee_id, td.status, td.description, td.date " +
                "FROM to_do td WHERE td.employee_id = ?";

        List<TodoDTO> todoList = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    todoList.add(new TodoDTO(
                            rs.getInt("task_id"),
                            rs.getString("employee_id"),
                            rs.getString("status"),
                            rs.getString("description"),
                            rs.getDate("date")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching to-dos by employee ID: " + e.getMessage(), e);
        }

        return todoList;
    }


    @Override
    public boolean updateTodo(TodoDTO todoDTO) {
        final String sql = "UPDATE to_do SET status = ?, description = ?, date = ? WHERE task_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, todoDTO.getStatus());
            preparedStatement.setString(2, todoDTO.getDescription());
            preparedStatement.setDate(3, todoDTO.getDate());
            preparedStatement.setInt(4, todoDTO.getTaskId());

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating to-do: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteToDoTask(Integer taskId) {
        final String sql = "DELETE FROM to_do WHERE task_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, taskId);

            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting to-do: " + e.getMessage(), e);
        }
    }


}