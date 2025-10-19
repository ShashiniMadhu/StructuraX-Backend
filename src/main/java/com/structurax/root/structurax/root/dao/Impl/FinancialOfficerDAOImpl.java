package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.FinancialOfficerDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Repository
public class FinancialOfficerDAOImpl implements FinancialOfficerDAO {

    @Autowired
    private DatabaseConnection databaseConnection;


    @Override
    public List<ProjectDTO> getAllProjects() {
        List<ProjectDTO> projectList = new ArrayList<>();
        final String sql = "SELECT * FROM project";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                ProjectDTO project = new ProjectDTO();
                project.setProjectId(rs.getString("project_id"));
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

                project.setClientId(rs.getString("client_id"));
                project.setQsId(rs.getString("qs_id"));
                project.setPmId(rs.getString("pm_id"));
                project.setSsId(rs.getString("ss_id"));


                projectList.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projectList;
    }

    @Override
    public ProjectDTO getProjectById(String id) {
        final String sql = "SELECT * FROM project WHERE project_id = ?";
        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    ProjectDTO project = new ProjectDTO();
                    project.setProjectId(rs.getString("project_id"));
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    project.setLocation(rs.getString("location"));
                    project.setStatus(rs.getString("status"));
                    project.setCategory(rs.getString("category"));

                    if (rs.getDate("start_date") != null)
                        project.setStartDate(rs.getDate("start_date").toLocalDate());

                    if (rs.getDate("due_date") != null)
                        project.setDueDate(rs.getDate("due_date").toLocalDate());

                    project.setEstimatedValue(rs.getBigDecimal("estimated_value"));

                    project.setBudget(rs.getBigDecimal("budget"));
                    project.setClientId(rs.getString("client_id"));
                    project.setQsId(rs.getString("qs_id"));
                    project.setPmId(rs.getString("pm_id"));
                    project.setSsId(rs.getString("ss_id"));


                    return project;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching project by ID: " + e.getMessage(), e);
        }

        return null;
    }

    /*
    @Override
    public PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        final String sql = "INSERT INTO payment_plan (created_date, total_amount, start_date, end_date, no_of_installments, project_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setDate(1, paymentPlanDTO.getCreatedDate());
            preparedStatement.setDouble(2, paymentPlanDTO.getTotalAmount());
            preparedStatement.setDate(3, paymentPlanDTO.getStartDate());
            preparedStatement.setDate(4, paymentPlanDTO.getEndDate());
            preparedStatement.setInt(5, paymentPlanDTO.getNumberOfInstallments());
            preparedStatement.setInt(6, paymentPlanDTO.getProjectId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating payment plan failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    paymentPlanDTO.setPaymentPlanId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating payment plan failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting payment plan: " + e.getMessage(), e);
        }

        return paymentPlanDTO;
    }*/


    @Override
    public PaymentPlanDTO getPaymentPlanById(Integer id) {
        final String sql = "SELECT * FROM payment_plan WHERE payment_plan_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new PaymentPlanDTO(
                            rs.getInt("payment_plan_id"),
                            rs.getString("project_id"),
                            rs.getDate("created_date"),
                            rs.getDouble("total_amount"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getInt("no_of_installments")

                    );


                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payment plan by ID: " + e.getMessage(), e);
        }

        return null;
    }

   /* @Override
    public PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        final String sql = "UPDATE payment_plan SET created_date = ?, total_amount = ?, start_date = ?, end_date=?,no_of_installments=? WHERE payment_plan_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setDate(1, paymentPlanDTO.getCreatedDate());
            preparedStatement.setDouble(2, paymentPlanDTO.getTotalAmount());
            preparedStatement.setDate(3, paymentPlanDTO.getStartDate());
            preparedStatement.setDate(4, paymentPlanDTO.getEndDate());
            preparedStatement.setInt(5, paymentPlanDTO.getNumberOfInstallments());
            preparedStatement.setInt(6,paymentPlanDTO.getPaymentPlanId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Payment Plan update failed. Payment plan ID may not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment plan: " + e.getMessage(), e);
        }

        return paymentPlanDTO;
    }



    @Override
    public InstallmentDTO createInstallment(InstallmentDTO installmentDTO) {
        final String sql = "INSERT INTO installment (payment_plan_id,milestone, amount, due_date,status) " +
                "VALUES (?, ?, ?,?,?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, installmentDTO.getPaymentPlanId());
            preparedStatement.setString(2, installmentDTO.getMilestone());
            preparedStatement.setDouble(3, installmentDTO.getAmount());
            preparedStatement.setDate(4, (Date) installmentDTO.getDueDate());
            preparedStatement.setString(5, installmentDTO.getStatus());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting payment plan: " + e.getMessage(), e);
        }

        return installmentDTO;
    }*/

    @Override
    public List<InstallmentDTO> getInstallmentsByPaymentPlanId(Integer id) {
        final String sql = "SELECT * FROM installment e " +
                "INNER JOIN payment_plan p ON e.payment_plan_id = p.payment_plan_id " +
                "WHERE e.payment_plan_id = ?";

        List<InstallmentDTO> installmentList = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    InstallmentDTO dto = new InstallmentDTO();
                    dto.setInstallmentId(rs.getInt("installment_id"));
                    dto.setPaymentPlanId(rs.getInt("payment_plan_id"));

                    dto.setAmount(rs.getDouble("amount"));
                    dto.setDueDate(rs.getDate("due_date"));
                    dto.setStatus(rs.getString("status"));
                    dto.setPaidDate(rs.getDate("paid_date"));

                    installmentList.add(dto);

                   // System.out.println("Installments fetched: " + installmentList);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching installments by Payment Plan ID: " + e.getMessage(), e);
        }

        return installmentList;
    }

    /*@Override
    public InstallmentDTO getInstallmentById(Integer id) {
        final String sql = "SELECT * FROM installment WHERE installment_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new InstallmentDTO(
                            rs.getInt("installment_id"),
                            rs.getInt("payment_plan_id"),
                            rs.getString("milestone"),
                            rs.getDouble("amount"),
                            rs.getDate("due_date"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching installment by ID: " + e.getMessage(), e);
        }

        return null;
    }


    @Override
    public InstallmentDTO updateInstallment(InstallmentDTO installmentDTO) {
        final String sql = "UPDATE installment SET milestone = ?, amount = ?, due_date = ?, status=? WHERE installment_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, installmentDTO.getMilestone());
            preparedStatement.setDouble(2, installmentDTO.getAmount());
            preparedStatement.setDate(3, installmentDTO.getDueDate());
            preparedStatement.setString(4, installmentDTO.getStatus());
            preparedStatement.setInt(5,installmentDTO.getInstallmentId());


            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Installment update failed. Installment ID may not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating installment: " + e.getMessage(), e);
        }

        return installmentDTO;
    }

    @Override
    public List<InstallmentDTO> deleteInstallmentsByPaymentPlanId(Integer id) {
        List<InstallmentDTO> installments = getInstallmentsByPaymentPlanId(id);

        if (installments == null || installments.isEmpty()) {
            throw new RuntimeException("No installments found with Payment Plan ID: " + id);
        }

        final String sql = "DELETE FROM installment WHERE payment_plan_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting installments: " + e.getMessage(), e);
        }

        return installments;
    }

    @Override
    public InstallmentDTO deleteInstallmentById(Integer id) {
        InstallmentDTO installment = getInstallmentById(id);
        if (installment == null) {
            throw new RuntimeException("No installment found with id: " + id);
        }

        final String sql = "DELETE FROM installment WHERE installment_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting installment: " + e.getMessage(), e);
        }

        return installment;
    }*/



    /*---------CRUD for full payment plan with installments ------------*/
    @Override
    public PaymentPlanDTO getPaymentPlanByProjectId(String projectId) {
        final String sql = "SELECT * FROM payment_plan WHERE project_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, projectId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    PaymentPlanDTO paymentPlan = new PaymentPlanDTO(
                            rs.getInt("payment_plan_id"),
                            rs.getString("project_id"),
                            rs.getDate("created_date"),
                            rs.getDouble("total_amount"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getInt("no_of_installments")

                    );

                    // ✅ Fetch Installments for this Payment Plan
                    List<InstallmentDTO> installments = getInstallmentsByPaymentPlanId(paymentPlan.getPaymentPlanId());
                    paymentPlan.setInstallments(installments);

                    return paymentPlan;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payment plan by project ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public PaymentPlanDTO createFullPaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        final String planSql = "INSERT INTO payment_plan (created_date, total_amount, start_date, end_date, no_of_installments,project_id) " +
                "VALUES (?, ?, ?, ?, ?,?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement planStmt = connection.prepareStatement(planSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            // 1. Insert payment plan
            planStmt.setDate(1, paymentPlanDTO.getCreatedDate());
            planStmt.setDouble(2, paymentPlanDTO.getTotalAmount());
            planStmt.setDate(3, paymentPlanDTO.getStartDate());
            planStmt.setDate(4, paymentPlanDTO.getEndDate());
            planStmt.setInt(5, paymentPlanDTO.getNumberOfInstallments());
            planStmt.setString(6,paymentPlanDTO.getProjectId());

            int rows = planStmt.executeUpdate();
            if (rows == 0) throw new SQLException("Creating payment plan failed.");

            // 2. Get generated payment_plan_id
            try (ResultSet generatedKeys = planStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedPlanId = generatedKeys.getInt(1);
                    paymentPlanDTO.setPaymentPlanId(generatedPlanId);

                    // 3. Insert each installment
                    String installmentSql = "INSERT INTO installment (payment_plan_id, amount, due_date, status,paid_date) " +
                            "VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement installmentStmt = connection.prepareStatement(installmentSql)) {
                        for (InstallmentDTO installment : paymentPlanDTO.getInstallments()) {
                            installmentStmt.setInt(1, generatedPlanId);
                            //installmentStmt.setString(2, installment.getMilestone());
                            installmentStmt.setDouble(2, installment.getAmount());
                            installmentStmt.setDate(3, new java.sql.Date(installment.getDueDate().getTime()));
                            installmentStmt.setString(4, installment.getStatus() != null ? installment.getStatus() : "Pending");
                            installmentStmt.setDate(5, new java.sql.Date(installment.getPaidDate().getTime()));
                            installmentStmt.addBatch(); // Batch insert for efficiency
                        }
                        installmentStmt.executeBatch();
                    }
                } else {
                    throw new SQLException("Failed to retrieve payment_plan_id.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error creating full payment plan: " + e.getMessage(), e);
        }

        return paymentPlanDTO;
    }


    @Override
    public PaymentPlanDTO updateFullPaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        Connection connection = null;
        try {
            connection = databaseConnection.getConnection();
            connection.setAutoCommit(false);  // begin transaction

            // Update payment plan
            final String sqlUpdatePlan = "UPDATE payment_plan SET created_date = ?, total_amount = ?, start_date = ?, end_date = ?, no_of_installments = ? WHERE project_id = ?";
            try (PreparedStatement psPlan = connection.prepareStatement(sqlUpdatePlan)) {
                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                psPlan.setDate(1, currentDate);

                psPlan.setDouble(2, paymentPlanDTO.getTotalAmount());
                if (paymentPlanDTO.getStartDate() != null) {
                    psPlan.setDate(3, new java.sql.Date(paymentPlanDTO.getStartDate().getTime()));
                } else {
                    psPlan.setDate(3, currentDate);
                }
                if (paymentPlanDTO.getEndDate() != null) {
                    psPlan.setDate(4, new java.sql.Date(paymentPlanDTO.getEndDate().getTime()));
                } else {
                    psPlan.setDate(4, currentDate);
                }
                psPlan.setInt(5, paymentPlanDTO.getNumberOfInstallments());
                psPlan.setString(6, paymentPlanDTO.getProjectId());

                int updatedPlanRows = psPlan.executeUpdate();
                if (updatedPlanRows == 0) {
                    throw new RuntimeException("Payment Plan update failed. Project ID may not exist: " + paymentPlanDTO.getProjectId());
                }
            }

            // Prepare statements for insert and update installments
            final String sqlUpdateInstallment = "UPDATE installment SET amount = ?, due_date = ?, status = ?, paid_date = ? WHERE installment_id = ?";
            final String sqlInsertInstallment = "INSERT INTO installment (payment_plan_id, amount, due_date, status, paid_date) VALUES (?, ?, ?, ?, ?)";

            try (
                    PreparedStatement psUpdate = connection.prepareStatement(sqlUpdateInstallment);
                    PreparedStatement psInsert = connection.prepareStatement(sqlInsertInstallment)
            ) {
                for (InstallmentDTO installment : paymentPlanDTO.getInstallments()) {
                    if (installment.getInstallmentId() == 0) {
                        // Insert new installment
                        psInsert.setInt(1, paymentPlanDTO.getPaymentPlanId()); // make sure this is set correctly
                        psInsert.setDouble(2, installment.getAmount());
                        psInsert.setDate(3, installment.getDueDate());
                        psInsert.setString(4, installment.getStatus());
                        psInsert.setDate(5, installment.getPaidDate());

                        psInsert.addBatch();
                        System.out.println("Inserting new installment with amount: " + installment.getAmount());
                    } else {
                        // Update existing installment
                        psUpdate.setDouble(1, installment.getAmount());
                        psUpdate.setDate(2, installment.getDueDate());
                        psUpdate.setString(3, installment.getStatus());
                        psUpdate.setDate(4, installment.getPaidDate());
                        psUpdate.setInt(5, installment.getInstallmentId());

                        psUpdate.addBatch();
                        System.out.println("Updating installment ID: " + installment.getInstallmentId());
                    }
                }


                psUpdate.executeBatch();
                psInsert.executeBatch();
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
            throw new RuntimeException("Error updating full payment plan: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {}
            }
        }

        return paymentPlanDTO;
    }


    @Override
    public List<LaborAttendanceDTO> getLaborAttendanceByProjectId(String projectId, Date date) {
        final String sql = "SELECT la.*,ls.* FROM labor_attendance la " +
                "LEFT JOIN labor_salary ls ON ls.attendance_id=la.attendance_id WHERE la.project_id=? AND la.date =?";

        List<LaborAttendanceDTO> attendanceList = new ArrayList<>();

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, projectId);
            preparedStatement.setDate(2,new java.sql.Date(date.getTime()));

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    LaborAttendanceDTO dto = new LaborAttendanceDTO();
                    dto.setId(rs.getInt("attendance_id"));
                    dto.setProject_id(rs.getString("project_id"));

                    dto.setDate(rs.getDate("date"));
                    dto.setHiring_type(rs.getString("hiring_type"));
                    dto.setLabor_type(rs.getString("labor_type"));
                    dto.setCount(rs.getInt("count"));
                    dto.setCompany(rs.getString("company_name"));

                    BigDecimal cost = rs.getBigDecimal("cost");

                    if (cost != null) {
                        LaborSalaryDTO laborSalaryDTO = new LaborSalaryDTO();
                        laborSalaryDTO.setSalaryId(rs.getInt("salary_id"));
                        laborSalaryDTO.setCost(cost);
                        laborSalaryDTO.setAttendanceId(rs.getInt("attendance_id"));
                        laborSalaryDTO.setProjectId(rs.getString("project_id"));
                        laborSalaryDTO.setLaborRate(rs.getBigDecimal("labor_rate"));
                        dto.setSalary(laborSalaryDTO);
                    }

                    attendanceList.add(dto);

                    // System.out.println("Installments fetched: " + installmentList);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching installments by Payment Plan ID: " + e.getMessage(), e);
        }

        return attendanceList;
    }

    @Override
    public LaborAttendanceDTO getAttendanceById(int attendanceId) {
        final String sql = "SELECT la.*, ls.cost  FROM labor_attendance la " +
                "LEFT JOIN labor_salary ls ON ls.attendance_id = la.attendance_id " +
                "WHERE la.attendance_id = ?";
        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, attendanceId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    LaborAttendanceDTO attendance = new LaborAttendanceDTO();
                    attendance.setId(rs.getInt("attendance_id"));
                    attendance.setProject_id(rs.getString("project_id"));
                    attendance.setDate(rs.getDate("date"));
                    attendance.setHiring_type(rs.getString("hiring_type"));
                    attendance.setLabor_type(rs.getString("labor_type"));
                    attendance.setCount(rs.getInt("count"));
                    attendance.setCompany(rs.getString("company_name"));
                    // directly set salary here

                    BigDecimal cost = rs.getBigDecimal("cost");
                    if (cost != null) {
                        LaborSalaryDTO laborSalaryDTO = new LaborSalaryDTO();
                        laborSalaryDTO.setCost(cost);
                        attendance.setSalary(laborSalaryDTO);
                    }


                    System.out.println("Returning: " + attendance.getSalary().getCost());


                    return attendance;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching attendance by ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public LaborSalaryDTO insertSalary(LaborSalaryDTO laborSalaryDTO) {
        final String sql = "INSERT INTO labor_salary (project_id, attendance_id, labor_rate,cost) " +
                "VALUES (?, ?, ?,?)"; // No cost here

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, laborSalaryDTO.getProjectId());
            preparedStatement.setInt(2, laborSalaryDTO.getAttendanceId());
            preparedStatement.setBigDecimal(3, laborSalaryDTO.getLaborRate());
            preparedStatement.setBigDecimal(4,laborSalaryDTO.getCost());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting labor salary: " + e.getMessage(), e);
        }

        return laborSalaryDTO;
    }

    @Override
    public LaborSalaryDTO getSalaryRecordById(int salaryId) {
        final String sql = "SELECT * FROM labor_salary WHERE salary_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, salaryId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new LaborSalaryDTO(
                            rs.getInt("salary_id"),

                            rs.getInt("attendance_id"),
                            rs.getString("project_id"),
                            rs.getBigDecimal("labor_rate"),
                            rs.getBigDecimal("cost")

                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching salary record by ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public LaborSalaryDTO deleteSalaryRecordById(int salaryId) {
        LaborSalaryDTO salaryRecord = getSalaryRecordById(salaryId);
        if (salaryRecord == null) {
            throw new RuntimeException("No salary record found with id: " + salaryId);
        }

        final String sql = "DELETE FROM labor_salary WHERE salary_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, salaryId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting salary record: " + e.getMessage(), e);
        }

        return salaryRecord;
    }

    public Integer getSalaryIdByAttendanceId(int attendanceId) {
        final String sql = "SELECT salary_id FROM labor_salary WHERE attendance_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, attendanceId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("salary_id");
                } else {
                    return null; // no record found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching salary ID: " + e.getMessage(), e);
        }
    }


    @Override
    public List<LaborSalaryDTO> updateSalaryRecord(List<LaborSalaryDTO> laborSalaryDTOList) {
        final String sql = "UPDATE labor_salary SET labor_rate = ? WHERE salary_id = ? AND attendance_id=?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            for (LaborSalaryDTO dto : laborSalaryDTOList) {
                // Fetch the real salaryId
                Integer salaryId = getSalaryIdByAttendanceId(dto.getAttendanceId());
                if (salaryId == null) {
                    throw new RuntimeException("No salary record found for Attendance ID: " + dto.getAttendanceId());
                }

                // Set the retrieved salaryId into the DTO
                dto.setSalaryId(salaryId);

                // Perform update
                preparedStatement.setBigDecimal(1, dto.getLaborRate());
                preparedStatement.setInt(2, dto.getSalaryId());
                preparedStatement.setInt(3, dto.getAttendanceId());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("Update failed for Salary ID: " + dto.getSalaryId());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating salary records: " + e.getMessage(), e);
        }

        return laborSalaryDTOList;
    }


    @Override
    public PaymentPlanDTO deletePaymentPlanById(String id) {
        PaymentPlanDTO paymentPlan = getPaymentPlanByProjectId(id);
        if (paymentPlan == null) {
            throw new RuntimeException("No payment plan found with project id : " + id);
        }

        try (Connection connection = databaseConnection.getConnection()) {
            connection.setAutoCommit(false); // start transaction

            // Delete installments first
            final String sqlDeleteInstallments = "DELETE FROM installment WHERE payment_plan_id = ?";
            try (PreparedStatement psInstallments = connection.prepareStatement(sqlDeleteInstallments)) {
                psInstallments.setInt(1, paymentPlan.getPaymentPlanId());
                psInstallments.executeUpdate();
            }

            // Delete payment plan
            final String sqlDeletePaymentPlan = "DELETE FROM payment_plan WHERE project_id = ?";
            try (PreparedStatement psPlan = connection.prepareStatement(sqlDeletePaymentPlan)) {
                psPlan.setString(1, id);
                psPlan.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment plan: " + e.getMessage(), e);
        }

        return paymentPlan;
    }


    /*-------------- Labor payments made by financial officer -----------*/

    @Override
    public LaborPaymentDTO createLaborPayment(LaborPaymentDTO paymentDTO) {
        String sql = "INSERT INTO labor_payment (project_id, amount, comment, date, receipt) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, paymentDTO.getProjectId());
            preparedStatement.setBigDecimal(2, paymentDTO.getAmount());
            preparedStatement.setString(3, paymentDTO.getComment());
            preparedStatement.setDate(4, java.sql.Date.valueOf(paymentDTO.getDate()));

            // Set BLOB from MultipartFile
            if (paymentDTO.getReceipt() != null && !paymentDTO.getReceipt().isEmpty()) {
                preparedStatement.setBinaryStream(
                        5,
                        paymentDTO.getReceipt().getInputStream(),
                        (int) paymentDTO.getReceipt().getSize()
                );
            } else {
                preparedStatement.setNull(5, java.sql.Types.BLOB);
            }

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error inserting labor payment: " + e.getMessage(), e);
        }

        return paymentDTO;
    }

    @Override
    public List<LaborPaymentDTO> getAllLaborPayments() {
        List<LaborPaymentDTO> paymentList = new ArrayList<>();
        final String sql = "SELECT * FROM labor_payment";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                LaborPaymentDTO payment = new LaborPaymentDTO();
                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setProjectId(rs.getString("project_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setComment(rs.getString("comment"));
                payment.setDate(rs.getDate("date").toLocalDate());

                Blob blob = rs.getBlob("receipt");
                if(blob != null){
                    byte[] bytes = blob.getBytes(1,(int) blob.length());
                    payment.setReceiptData(bytes);
                }


                paymentList.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paymentList;
    }

    @Override
    public LaborPaymentDTO updateLaborPaymentRecord(LaborPaymentDTO laborPaymentDTO) {
        final String sql = "UPDATE labor_payment SET project_id = ?, amount = ?, comment = ?, date = ?, receipt = ? WHERE payment_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, laborPaymentDTO.getProjectId());
            preparedStatement.setBigDecimal(2, laborPaymentDTO.getAmount());
            preparedStatement.setString(3, laborPaymentDTO.getComment());
            preparedStatement.setDate(4, java.sql.Date.valueOf(laborPaymentDTO.getDate()));
            // Set BLOB from MultipartFile
            if (laborPaymentDTO.getReceipt() != null && !laborPaymentDTO.getReceipt().isEmpty()) {
                preparedStatement.setBinaryStream(
                        5,
                        laborPaymentDTO.getReceipt().getInputStream(),
                        (int) laborPaymentDTO.getReceipt().getSize()
                );
            } else {
                preparedStatement.setNull(5, java.sql.Types.BLOB);
            }

            preparedStatement.setInt(6,laborPaymentDTO.getPaymentId());


            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Labor payment record update failed. Payment ID may not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating labor payment record: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return laborPaymentDTO;
    }

    @Override
    public LaborPaymentDTO getLaborPaymentRecordById(int paymentId) {
        final String sql = "SELECT * FROM labor_payment WHERE payment_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, paymentId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    LaborPaymentDTO payment = new LaborPaymentDTO();
                    payment.setPaymentId(rs.getInt("payment_id"));
                    payment.setProjectId(rs.getString("project_id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setComment(rs.getString("comment"));
                    payment.setDate(rs.getDate("date").toLocalDate());

                    // Retrieve receipt (BLOB)
                    Blob receiptBlob = rs.getBlob("receipt");
                    if (receiptBlob != null) {
                        payment.setReceiptData(receiptBlob.getBytes(1, (int) receiptBlob.length()));
                    } else {
                        payment.setReceiptData(null);
                    }

                    return payment;
                } else {
                    return null; // no record found
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching labor payment record: " + e.getMessage(), e);
        }
    }

    @Override
    public LaborPaymentDTO deletePaymentRecordById(int paymentId) {
        // Step 1: Get the existing record before deleting
        LaborPaymentDTO paymentDTO = getLaborPaymentRecordById(paymentId);

        // If the record doesn’t exist, return null or throw an exception
        if (paymentDTO == null) {
            throw new RuntimeException("Payment record not found with ID: " + paymentId);
        }

        final String sql = "DELETE FROM labor_payment WHERE payment_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, paymentId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to delete payment record with ID: " + paymentId);
            }

            // Step 2: Return the deleted record details
            return paymentDTO;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment record: " + e.getMessage(), e);
        }
    }


    /*---------- confirm order payments ---------------*/

    @Override
    public List<PurchaseOrderDTO> getAllOrders() {
        List<PurchaseOrderDTO> orderList = new ArrayList<>();
        final String sql = "SELECT * FROM purchase_order";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                PurchaseOrderDTO order = new PurchaseOrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setProjectId(rs.getString("project_id"));
                order.setSupplierId(rs.getInt("supplier_id"));
                order.setResponseId(rs.getInt("response_id"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setEstimatedDeliveryDate(rs.getDate("estimated_delivery_date").toLocalDate());
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setOrderStatus(rs.getBoolean("order_status"));


                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderList;
    }

    @Override
    public PurchaseOrderDTO updateOrderPaymentStatus(PurchaseOrderDTO orderDTO) {
        final String sql = "UPDATE purchase_order SET payment_status = ?  WHERE order_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, orderDTO.getPaymentStatus());
            preparedStatement.setInt(2, orderDTO.getOrderId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Order payment record update failed. Oder ID may not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order payment status: " + e.getMessage(), e);
        }

        return orderDTO;
    }

    /*---------- petty cash -------------*/

    @Override
    public PettyCashDTO insertPettyCash(PettyCashDTO pettyCashDTO) {
        final String sql = "INSERT INTO petty_cash(project_id, date, amount, employee_id) " +
                " VALUES(?,?,?,?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, pettyCashDTO.getProjectId());
            preparedStatement.setDate(2, java.sql.Date.valueOf(pettyCashDTO.getDate()));
            preparedStatement.setBigDecimal(3, pettyCashDTO.getAmount());
            preparedStatement.setString(4, pettyCashDTO.getEmployeeId());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error inserting petty cash: " + e.getMessage(), e);
        }

        return pettyCashDTO;
    }

    @Override
    public Boolean updatePettyCash(PettyCashDTO pettyCashDTO) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM petty_cash_record WHERE petty_cash_id = ?";
        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement checkStmt = connection.prepareStatement(checkSql)
        ) {
            checkStmt.setInt(1, pettyCashDTO.getPettyCashId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new IllegalStateException("Cannot update petty cash with existing expense records");
            }
        }

        String updateSql = "UPDATE petty_cash SET amount = ?, date = ?, employee_id = ? WHERE petty_cash_id = ?";
        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement updateStmt = connection.prepareStatement(updateSql)
        ) {
            updateStmt.setBigDecimal(1, pettyCashDTO.getAmount());
            updateStmt.setDate(2, java.sql.Date.valueOf(pettyCashDTO.getDate()));
            updateStmt.setString(3, pettyCashDTO.getEmployeeId());
            updateStmt.setInt(4, pettyCashDTO.getPettyCashId());
            return updateStmt.executeUpdate() > 0;
        }
    }

    @Override
    public Boolean deletePettyCash(int pettyCashId) {
        // Check if petty cash has any related expenses
        String checkSql = "SELECT COUNT(*) FROM petty_cash_record WHERE petty_cash_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement checkStmt = connection.prepareStatement(checkSql)
        ) {
            checkStmt.setInt(1, pettyCashId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // There are related records → cannot delete
                throw new IllegalStateException("Cannot delete petty cash with existing expense records");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Proceed with delete if no related records
        String deleteSql = "DELETE FROM petty_cash WHERE petty_cash_id = ?";
        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)
        ) {
            deleteStmt.setInt(1, pettyCashId);
            return deleteStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<PettyCashDTO> getAllPettyCash() {
        List<PettyCashDTO> pettyCashDTOList = new ArrayList<>();
        final String sql = "SELECT * FROM petty_cash";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery()
        ) {
            while (rs.next()) {
                PettyCashDTO pettyCashDTO = new PettyCashDTO();
                pettyCashDTO.setPettyCashId(rs.getInt("petty_cash_id"));
                pettyCashDTO.setProjectId(rs.getString("project_id"));
                pettyCashDTO.setAmount(rs.getBigDecimal("amount"));
                pettyCashDTO.setDate(rs.getDate("date").toLocalDate());
                pettyCashDTO.setEmployeeId(rs.getString("employee_id"));

                pettyCashDTOList.add(pettyCashDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pettyCashDTOList;

    }


}
