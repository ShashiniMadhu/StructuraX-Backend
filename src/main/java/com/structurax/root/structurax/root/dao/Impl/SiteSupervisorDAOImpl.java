package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.SiteSupervisorDAO;
import com.structurax.root.structurax.root.dto.InstallmentDTO;
import com.structurax.root.structurax.root.dto.LaborAttendanceDTO;

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


}
