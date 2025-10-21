package com.structurax.root.structurax.root.dao.Impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.structurax.root.structurax.root.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.SiteSupervisorDAO;
import com.structurax.root.structurax.root.util.DatabaseConnection;

@Repository
public class SiteSupervisorDAOImpl implements SiteSupervisorDAO {

    @Autowired
    private DatabaseConnection databaseConnection;


    @Override
    public List<ProjectDTO> getProjectsBySsId(String id) {
        final String sql = "SELECT p.* FROM project p " +
                "INNER JOIN employee e ON e.employee_id = p.ss_id WHERE p.ss_id=?";

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



                    for (ProjectDTO p : projectDTOList) {
                        System.out.println("Project: " + p.getName() );
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
        final String sql =
                "SELECT rsr.*, " +
                        "p.name AS project_name, " +
                        "ssUser.name AS site_supervisor_name, " +
                        "pmUser.name AS pm_name, " +
                        "qsUser.name AS qs_name " +
                        "FROM request_site_resources rsr " +
                        "INNER JOIN project p ON rsr.project_id = p.project_id " +
                        "INNER JOIN employee ssEmp ON rsr.site_supervisor_id = ssEmp.employee_id " +
                        "INNER JOIN users ssUser ON ssEmp.user_id = ssUser.user_id " +
                        "INNER JOIN employee pmEmp ON rsr.pm_id = pmEmp.employee_id " +
                        "INNER JOIN users pmUser ON pmEmp.user_id = pmUser.user_id " +
                        "INNER JOIN employee qsEmp ON rsr.qs_id = qsEmp.employee_id " +
                        "INNER JOIN users qsUser ON qsEmp.user_id = qsUser.user_id " +
                        "WHERE rsr.request_type = 'material'";


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
                        rs.getString("qs_name")
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
        final String sql =
                "SELECT rsr.*, " +
                        "p.name AS project_name, " +
                        "ssUser.name AS site_supervisor_name, " +
                        "pmUser.name AS pm_name, " +
                        "qsUser.name AS qs_name " +
                        "FROM request_site_resources rsr " +
                        "INNER JOIN project p ON rsr.project_id = p.project_id " +
                        "INNER JOIN employee ssEmp ON rsr.site_supervisor_id = ssEmp.employee_id " +
                        "INNER JOIN users ssUser ON ssEmp.user_id = ssUser.user_id " +
                        "INNER JOIN employee pmEmp ON rsr.pm_id = pmEmp.employee_id " +
                        "INNER JOIN users pmUser ON pmEmp.user_id = pmUser.user_id " +
                        "INNER JOIN employee qsEmp ON rsr.qs_id = qsEmp.employee_id " +
                        "INNER JOIN users qsUser ON qsEmp.user_id = qsUser.user_id " +
                        "WHERE rsr.request_type = 'tool'";


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
                        rs.getString("qs_name")
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
    public List<RequestSiteResourcesDTO> getAllLaborRequests() {
        final String sql =
                "SELECT rsr.*, " +
                        "p.name AS project_name, " +
                        "ssUser.name AS site_supervisor_name, " +
                        "pmUser.name AS pm_name, " +
                        "qsUser.name AS qs_name " +
                        "FROM request_site_resources rsr " +
                        "INNER JOIN project p ON rsr.project_id = p.project_id " +
                        "INNER JOIN employee ssEmp ON rsr.site_supervisor_id = ssEmp.employee_id " +
                        "INNER JOIN users ssUser ON ssEmp.user_id = ssUser.user_id " +
                        "INNER JOIN employee pmEmp ON rsr.pm_id = pmEmp.employee_id " +
                        "INNER JOIN users pmUser ON pmEmp.user_id = pmUser.user_id " +
                        "INNER JOIN employee qsEmp ON rsr.qs_id = qsEmp.employee_id " +
                        "INNER JOIN users qsUser ON qsEmp.user_id = qsUser.user_id " +
                        "WHERE rsr.request_type = 'labor'";


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
                        rs.getString("qs_name")
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
        final String planSql = "INSERT INTO request_site_resources (pm_approval, qs_approval, request_type,date,project_id,site_supervisor_id,qs_id,pm_id,is_received) " +
                "VALUES (?, ?, ?, ?,?,?,?,? ,?)";

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
            planStmt.setString(8,requestDTO.getPmId());
            planStmt.setBoolean(9,requestDTO.getIsReceived());

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
            connection.setAutoCommit(false); // Begin transaction

            // Step 1: Check current approval statuses
            final String sqlCheckStatus =
                    "SELECT pm_approval, qs_approval FROM request_site_resources WHERE request_id = ?";
            String pmApproval = null, qsApproval = null;

            try (PreparedStatement psCheck = connection.prepareStatement(sqlCheckStatus)) {
                psCheck.setInt(1, requestSiteResourcesDTO.getRequestId());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        pmApproval = rs.getString("pm_approval");
                        qsApproval = rs.getString("qs_approval");
                    } else {
                        throw new RuntimeException(
                                "Request not found with ID: " + requestSiteResourcesDTO.getRequestId());
                    }
                }
            }

            // Step 2: Handle update logic based on approval status
            if ("Pending".equalsIgnoreCase(pmApproval) && "Pending".equalsIgnoreCase(qsApproval)) {
                // ✅ Both Pending → Allow full update (project details + materials)

                final String sqlUpdateProject =
                        "UPDATE request_site_resources SET project_id = ? WHERE request_id = ?";
                try (PreparedStatement psUpdateProject = connection.prepareStatement(sqlUpdateProject)) {
                    psUpdateProject.setString(1, requestSiteResourcesDTO.getProjectId());
                    psUpdateProject.setInt(2, requestSiteResourcesDTO.getRequestId());
                    psUpdateProject.executeUpdate();
                }

                final String sqlUpdateResources =
                        "UPDATE site_resources SET name = ?, quantity = ?, priority = ? WHERE id = ?";

                try (PreparedStatement psResources = connection.prepareStatement(sqlUpdateResources)) {
                    for (SiteResourceDTO resource : requestSiteResourcesDTO.getMaterials()) {
                        if (resource.getId() == 0) {
                            throw new RuntimeException("Invalid resource ID for update: " + resource);
                        }

                        psResources.setString(1, resource.getMaterialName());
                        psResources.setInt(2, resource.getQuantity());
                        psResources.setString(3, resource.getPriority());
                        psResources.setInt(4, resource.getId());

                        int rows = psResources.executeUpdate();
                        if (rows == 0) {
                            throw new RuntimeException("Resource update failed for ID: " + resource.getId());
                        }
                    }
                }

            } else if ("Approved".equalsIgnoreCase(pmApproval) && "Approved".equalsIgnoreCase(qsApproval)) {
                // ✅ Both Approved → Only allow updating is_received

                final String sqlUpdateIsReceived =
                        "UPDATE request_site_resources SET is_received = ? WHERE request_id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlUpdateIsReceived)) {
                    ps.setBoolean(1, requestSiteResourcesDTO.getIsReceived());
                    ps.setInt(2, requestSiteResourcesDTO.getRequestId());

                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        throw new RuntimeException("Failed to update 'is_received' for approved request.");
                    }
                }

            } else {
                // ❌ One Approved, One Pending → No updates allowed
                throw new RuntimeException(
                        "Cannot update request. One approval is already given — updates are restricted.");
            }

            connection.commit();
            return requestSiteResourcesDTO;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Rollback failed: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw new RuntimeException("Error updating resource request: " + e.getMessage(), e);

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }



    @Override
    public RequestSiteResourcesDTO getRequestById(int requestId) {
        final String sql =
                "SELECT rsr.*, " +
                        "       p.name AS project_name, " +
                        "       ssUser.name AS site_supervisor_name, " +
                        "       pmUser.name AS pm_name, " +
                        "       qsUser.name AS qs_name " +
                        "FROM request_site_resources rsr " +
                        "INNER JOIN project p ON rsr.project_id = p.project_id " +
                        "INNER JOIN employee ssEmp ON rsr.site_supervisor_id = ssEmp.employee_id " +
                        "INNER JOIN users ssUser ON ssEmp.user_id = ssUser.user_id " +
                        "INNER JOIN employee pmEmp ON rsr.pm_id = pmEmp.employee_id " +
                        "INNER JOIN users pmUser ON pmEmp.user_id = pmUser.user_id " +
                        "INNER JOIN employee qsEmp ON rsr.qs_id = qsEmp.employee_id " +
                        "INNER JOIN users qsUser ON qsEmp.user_id = qsUser.user_id " +
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
                            rs.getString("qs_name") // make sure this matches the alias in SQL
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

    @Override
    public DailyUpdatesDTO insertDailyUpdates(DailyUpdatesDTO dailyUpdatesDTO) {
        final String sql = "INSERT INTO daily_updates(project_id, date, note, employee_id) VALUES(?, ?, ?, ?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, dailyUpdatesDTO.getProjectId());
            preparedStatement.setDate(2, Date.valueOf(dailyUpdatesDTO.getDate()));
            preparedStatement.setString(3, dailyUpdatesDTO.getNote());
            preparedStatement.setString(4, dailyUpdatesDTO.getEmployeeId());

            preparedStatement.executeUpdate();

            return dailyUpdatesDTO;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting to-do: " + e.getMessage(), e);
        }

    }

    @Override
    public List<DailyUpdatesDTO> getAllDailyUpdatesBySsId(String ssId) {
        final String sql = "SELECT * FROM daily_updates WHERE employee_id = ?";

        List<DailyUpdatesDTO> updatesDTOS = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, ssId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    updatesDTOS.add(new DailyUpdatesDTO(
                            rs.getInt("update_id"),
                            rs.getString("project_id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("note"),
                            rs.getString("employee_id")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching to-dos by employee ID: " + e.getMessage(), e);
        }

        return updatesDTOS;
    }

    @Override
    public DailyUpdatesDTO updateDailyUpdates(DailyUpdatesDTO dailyUpdatesDTO) {
        final String sql = "UPDATE  daily_updates SET project_id = ?, date = ?, note = ? WHERE update_id=?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {


            preparedStatement.setString(1, dailyUpdatesDTO.getProjectId());
            preparedStatement.setDate(2, Date.valueOf(dailyUpdatesDTO.getDate()));
            preparedStatement.setString(3, dailyUpdatesDTO.getNote());
            preparedStatement.setInt(4, dailyUpdatesDTO.getUpdateId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No labor attendance record found with id=" + dailyUpdatesDTO.getUpdateId());
            }

            return dailyUpdatesDTO;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating labor attendance: " + e.getMessage(), e);
        }

    }

    @Override
    public boolean deleteDailyUpdate(int updateId) {
        final String sql = "DELETE FROM daily_updates WHERE update_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, updateId);
            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting daily update: " + e.getMessage(), e);
        }
    }

    @Override
    public WBSDTO updateWbsStatus(WBSDTO wbsdto) {
        final String sql = "UPDATE  wbs SET status = ? WHERE task_id=? AND parent_id =? AND project_id=?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {


            preparedStatement.setString(1, wbsdto.getStatus());
            preparedStatement.setInt(2, wbsdto.getTaskId());
            preparedStatement.setInt(3, wbsdto.getParentId());
            preparedStatement.setString(4,wbsdto.getProjectId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No task found with task id=" + wbsdto.getTaskId());
            }

            return wbsdto;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating wbs task status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<WBSDTO> getAllWbsBySsId(String ssId) {
        final String sql = "SELECT w.task_id, w.project_id, w.parent_id, w.name, w.status, w.milestone " +
                "FROM wbs w " +
                "INNER JOIN project p ON w.project_id = p.project_id " +
                "WHERE p.ss_id = ?";

        List<WBSDTO> wbsdtos = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, ssId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    WBSDTO dto = new WBSDTO(
                            rs.getInt("task_id"),
                            rs.getString("project_id"),
                            rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null,
                            rs.getString("name"),
                            rs.getString("status"),
                            rs.getBoolean("milestone")
                    );
                    wbsdtos.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching WBS records by site supervisor ID: " + e.getMessage(), e);
        }

        return wbsdtos;
    }

    @Override
    public PettyCashRecordDTO insertPettyCashRecord(PettyCashRecordDTO recordDTO) {
        try (Connection connection = databaseConnection.getConnection()) {
            // Get total spent for this petty cash
            final String totalSql = "SELECT SUM(expense_amount) FROM petty_cash_record WHERE petty_cash_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(totalSql)) {
                ps.setInt(1, recordDTO.getPettyCashId());
                ResultSet rs = ps.executeQuery();
                BigDecimal totalSpent = BigDecimal.ZERO;
                if (rs.next()) {
                    totalSpent = rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
                }

                // Get petty cash total amount
                final String pettyCashSql = "SELECT amount FROM petty_cash WHERE petty_cash_id = ?";
                try (PreparedStatement ps2 = connection.prepareStatement(pettyCashSql)) {
                    ps2.setInt(1, recordDTO.getPettyCashId());
                    ResultSet rs2 = ps2.executeQuery();
                    BigDecimal pettyCashAmount = BigDecimal.ZERO;
                    if (rs2.next()) {
                        pettyCashAmount = rs2.getBigDecimal("amount");
                    }

                    BigDecimal newTotal = totalSpent.add(recordDTO.getExpenseAmount());
                    if (newTotal.compareTo(pettyCashAmount) > 0) {
                        throw new RuntimeException("Cannot add record: total expenses exceed petty cash amount");
                    }
                }
            }

            // Insert record
            final String sql = "INSERT INTO petty_cash_record (petty_cash_id, expense_amount, date, description) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, recordDTO.getPettyCashId());
                preparedStatement.setBigDecimal(2, recordDTO.getExpenseAmount());
                preparedStatement.setDate(3, Date.valueOf(recordDTO.getDate()));
                preparedStatement.setString(4, recordDTO.getDescription());
                preparedStatement.executeUpdate();
            }

            return recordDTO;

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting petty cash record: " + e.getMessage(), e);
        }
    }


    @Override
    public List<PettyCashDTO> getPettyCashBySsId(String ssId) {
        final String sql = "SELECT pc.petty_cash_id, pc.project_id, pc.date AS cash_date, pc.amount, pc.employee_id, " +
                "pcr.record_id, pcr.petty_cash_id AS record_cash_id, pcr.expense_amount, pcr.date AS record_date, pcr.description " +
                "FROM petty_cash pc " +
                "LEFT JOIN petty_cash_record pcr ON pc.petty_cash_id = pcr.petty_cash_id " +
                "WHERE pc.employee_id = ? " +
                "ORDER BY pc.petty_cash_id";

        List<PettyCashDTO> pettyCashList = new ArrayList<>();
        Map<Integer, PettyCashDTO> pettyCashMap = new HashMap<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, ssId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    int pettyCashId = rs.getInt("petty_cash_id");

                    // Check if petty cash already exists in map
                    PettyCashDTO cashDTO = pettyCashMap.get(pettyCashId);
                    if (cashDTO == null) {
                        cashDTO = new PettyCashDTO();
                        cashDTO.setPettyCashId(pettyCashId);
                        cashDTO.setProjectId(rs.getString("project_id"));
                        cashDTO.setDate(rs.getDate("cash_date").toLocalDate());
                        cashDTO.setAmount(rs.getBigDecimal("amount"));
                        cashDTO.setEmployeeId(rs.getString("employee_id"));
                        cashDTO.setPettyCashRecords(new ArrayList<>()); // Initialize records list

                        pettyCashMap.put(pettyCashId, cashDTO);
                        pettyCashList.add(cashDTO);
                    }

                    // Map petty cash record if exists
                    int recordId = rs.getInt("record_id");
                    if (!rs.wasNull()) {
                        PettyCashRecordDTO recordDTO = new PettyCashRecordDTO();
                        recordDTO.setRecordId(recordId);
                        recordDTO.setPettyCashId(pettyCashId);
                        recordDTO.setExpenseAmount(rs.getBigDecimal("expense_amount"));
                        recordDTO.setDate(rs.getDate("record_date").toLocalDate());
                        recordDTO.setDescription(rs.getString("description"));

                        cashDTO.getPettyCashRecords().add(recordDTO);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching petty cash by site supervisor ID: " + e.getMessage(), e);
        }

        return pettyCashList;
    }

    @Override
    public PettyCashRecordDTO updatePettyCashRecord(PettyCashRecordDTO pettyCashRecordDTO) {
        final String sql = "UPDATE petty_cash_record SET expense_amount = ?, date =? , description =?  WHERE record_id=? ";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {


            preparedStatement.setBigDecimal(1, pettyCashRecordDTO.getExpenseAmount());
            preparedStatement.setDate(2, Date.valueOf(pettyCashRecordDTO.getDate()));
            preparedStatement.setString(3, pettyCashRecordDTO.getDescription());
            preparedStatement.setInt(4,pettyCashRecordDTO.getRecordId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No petty cash record found with record id=" + pettyCashRecordDTO.getRecordId());
            }

            return pettyCashRecordDTO;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating petty cash record: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletePettyCashRecord(int recordId) {
        final String sql = "DELETE FROM petty_cash_record WHERE record_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, recordId);
            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting petty cash record: " + e.getMessage(), e);
        }
    }


}



