package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.SiteSupervisorDAO;
import com.structurax.root.structurax.root.dto.*;

import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SiteSupervisorDAOImpl implements SiteSupervisorDAO {

    @Autowired
    private DatabaseConnection databaseConnection;


    @Override
    public List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendanceList) {
        final String sql = "INSERT INTO labor_attendance (project_id, date, hiring_type, labor_type, count, company) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            for (LaborAttendanceDTO dto : laborAttendanceList) {
                preparedStatement.setInt(1, dto.getProject_id());
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
                laborAttendanceRecord.setId(rs.getInt("id"));
                laborAttendanceRecord.setProject_id(rs.getInt("project_id"));
                laborAttendanceRecord.setDate(rs.getDate("date"));
                laborAttendanceRecord.setHiring_type(rs.getString("hiring_type"));
                laborAttendanceRecord.setLabor_type(rs.getString("labor_type"));
                laborAttendanceRecord.setCount(rs.getInt("count"));
                laborAttendanceRecord.setCompany(rs.getString("company"));

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
        final String sql = "UPDATE labor_attendance SET project_id = ?, date = ?, hiring_type = ?, labor_type = ?, count = ?, company = ? " +
                "WHERE id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            System.out.println("Updating: id=" + laborAttendanceDTO.getId() +
                    ", project_id=" + laborAttendanceDTO.getProject_id() +
                    ", date=" + laborAttendanceDTO.getDate() +
                    ", hiring_type=" + laborAttendanceDTO.getHiring_type() +
                    ", labor_type=" + laborAttendanceDTO.getLabor_type() +
                    ", count=" + laborAttendanceDTO.getCount() +
                    ", company=" + laborAttendanceDTO.getCompany());

            preparedStatement.setInt(1, laborAttendanceDTO.getProject_id());
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
    public List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(Integer project_id, java.sql.Date date) {
        final String sql = "SELECT * FROM labor_attendance l INNER JOIN project p ON p.project_id=l.project_id" +
                " WHERE l.project_id = ? AND DATE(l.date) = ?";

        List<LaborAttendanceDTO> laborAttendanceDTOS = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, project_id);
            preparedStatement.setDate(2, date);  // java.sql.Date is fine here

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    LaborAttendanceDTO dto = new LaborAttendanceDTO();
                    dto.setId(rs.getInt("id"));            // or rs.getLong if ID is long
                    dto.setProject_id(rs.getInt("project_id"));
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
    public List<LaborAttendanceDTO> deleteLaborAttendanceRecord(Integer project_id, Date date) {
        final String selectSql = "SELECT * FROM labor_attendance WHERE project_id = ? AND date = ?";
        final String deleteSql = "DELETE FROM labor_attendance WHERE project_id = ? AND date = ?";
        List<LaborAttendanceDTO> deletedRecords = new ArrayList<>();

        try (Connection connection = databaseConnection.getConnection()) {

            // Step 1: Fetch records before deleting
            try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
                selectStmt.setInt(1, project_id);
                selectStmt.setDate(2, date);
                ResultSet rs = selectStmt.executeQuery();

                while (rs.next()) {
                    LaborAttendanceDTO dto = new LaborAttendanceDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setProject_id(rs.getInt("project_id"));
                    dto.setDate(rs.getDate("date"));
                    dto.setHiring_type(rs.getString("hiring_type"));
                    dto.setLabor_type(rs.getString("labor_type"));
                    dto.setCount(rs.getInt("count"));
                    dto.setCompany(rs.getString("company"));

                    deletedRecords.add(dto);
                }
            }

            // Step 2: Delete the records
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, project_id);
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
        final String sql = "SELECT * FROM material m " +
                "INNER JOIN request r ON m.request_id = r.request_id " +
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
    public List<RequestDTO> getAllMaterialRequests() {
        final String sql = "SELECT * FROM request WHERE request_type='materials'";
        List<RequestDTO> requests = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                RequestDTO request = new RequestDTO(
                        rs.getInt("request_id"),
                        rs.getString("approval_status"),
                        rs.getString("request_type"),
                        rs.getDate("date"),
                        rs.getInt("project_id")
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
    public List<RequestDTO> getAllToolRequests() {
        final String sql = "SELECT * FROM request WHERE request_type='tool'";
        List<RequestDTO> requests = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                RequestDTO request = new RequestDTO(
                        rs.getInt("request_id"),
                        rs.getString("approval_status"),
                        rs.getString("request_type"),
                        rs.getDate("date"),
                        rs.getInt("project_id")
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
    public RequestDTO createMaterialRequest(RequestDTO requestDTO) {
        final String planSql = "INSERT INTO request (approval_status, request_type, date,project_id) " +
                "VALUES (?, ?, ?, ? )";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement planStmt = connection.prepareStatement(planSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            // 1. Insert request
            planStmt.setString(1, requestDTO.getApprovalStatus());
            planStmt.setString(2, requestDTO.getRequestType());
            planStmt.setDate(3, requestDTO.getDate());
            planStmt.setInt(4,requestDTO.getProjectId());

            int rows = planStmt.executeUpdate();
            if (rows == 0) throw new SQLException("Creating material request failed.");

            // 2. Get generated request_id
            try (ResultSet generatedKeys = planStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedRequestId = generatedKeys.getInt(1);
                    requestDTO.setRequestId(generatedRequestId);

                    // 3. Insert each material
                    String materialSql = "INSERT INTO material (name, quantity, priority, request_id) " +
                            "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement materialStmt = connection.prepareStatement(materialSql)) {
                        for (SiteResourceDTO material : requestDTO.getMaterials()) {

                            materialStmt.setString(1, material.getMaterialName());
                            materialStmt.setInt(2, material.getQuantity());
                            materialStmt.setString(3, material.getPriority());
                            materialStmt.setInt(4, generatedRequestId);
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