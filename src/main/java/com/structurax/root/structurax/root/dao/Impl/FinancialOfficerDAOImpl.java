package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.FinancialOfficerDAO;
import com.structurax.root.structurax.root.dto.InstallmentDTO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                project.setProjectId(rs.getInt("project_id"));
                project.setName(rs.getString("name"));
                project.setDescription(rs.getString("description"));
                project.setLocation(rs.getString("location"));
                project.setStatus(rs.getString("status"));
                project.setType(rs.getString("type"));

                if (rs.getDate("start_date") != null) {
                    project.setStartDate(rs.getDate("start_date").toLocalDate());
                }

                if (rs.getDate("due_date") != null) {
                    project.setDueDate(rs.getDate("due_date").toLocalDate());
                }

                project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                project.setAmountSpent(rs.getBigDecimal("amount_spent"));
                project.setBaseAmount(rs.getBigDecimal("base_amount"));
                project.setOwnerId(rs.getInt("owner_id"));
                project.setQsId(rs.getInt("qs_id"));
                project.setSqsId(rs.getInt("sqs_id"));
                project.setSpId(rs.getInt("sp_id"));
                project.setPlanId(rs.getInt("plan_id"));

                projectList.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projectList;
    }

    @Override
    public ProjectDTO getProjectById(Integer id) {
        final String sql = "SELECT * FROM project WHERE project_id = ?";
        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    ProjectDTO project = new ProjectDTO();
                    project.setProjectId(rs.getInt("project_id"));
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    project.setLocation(rs.getString("location"));
                    project.setStatus(rs.getString("status"));
                    project.setType(rs.getString("type"));

                    if (rs.getDate("start_date") != null)
                        project.setStartDate(rs.getDate("start_date").toLocalDate());

                    if (rs.getDate("due_date") != null)
                        project.setDueDate(rs.getDate("due_date").toLocalDate());

                    project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
                    project.setAmountSpent(rs.getBigDecimal("amount_spent"));
                    project.setBaseAmount(rs.getBigDecimal("base_amount"));
                    project.setOwnerId(rs.getInt("owner_id"));
                    project.setQsId(rs.getInt("qs_id"));
                    project.setSqsId(rs.getInt("sqs_id"));
                    project.setSpId(rs.getInt("sp_id"));
                    project.setPlanId(rs.getInt("plan_id"));

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
                            rs.getDate("created_date"),
                            rs.getDouble("total_amount"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getInt("no_of_installments"),
                            rs.getInt("project_id")
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
                    dto.setMilestone(rs.getString("milestone"));
                    dto.setAmount(rs.getDouble("amount"));
                    dto.setDueDate(rs.getDate("due_date"));
                    dto.setStatus(rs.getString("status"));

                    installmentList.add(dto);
                  ;
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
    public PaymentPlanDTO getPaymentPlanByProjectId(Integer projectId) {
        final String sql = "SELECT * FROM payment_plan WHERE project_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, projectId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    PaymentPlanDTO paymentPlan = new PaymentPlanDTO(
                            rs.getInt("payment_plan_id"),
                            rs.getDate("created_date"),
                            rs.getDouble("total_amount"),
                            rs.getDate("start_date"),
                            rs.getDate("end_date"),
                            rs.getInt("no_of_installments"),
                            rs.getInt("project_id")
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
            planStmt.setInt(6,paymentPlanDTO.getProjectId());

            int rows = planStmt.executeUpdate();
            if (rows == 0) throw new SQLException("Creating payment plan failed.");

            // 2. Get generated payment_plan_id
            try (ResultSet generatedKeys = planStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedPlanId = generatedKeys.getInt(1);
                    paymentPlanDTO.setPaymentPlanId(generatedPlanId);

                    // 3. Insert each installment
                    String installmentSql = "INSERT INTO installment (payment_plan_id, milestone, amount, due_date, status) " +
                            "VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement installmentStmt = connection.prepareStatement(installmentSql)) {
                        for (InstallmentDTO installment : paymentPlanDTO.getInstallments()) {
                            installmentStmt.setInt(1, generatedPlanId);
                            installmentStmt.setString(2, installment.getMilestone());
                            installmentStmt.setDouble(3, installment.getAmount());
                            installmentStmt.setDate(4, new java.sql.Date(installment.getDueDate().getTime()));
                            installmentStmt.setString(5, installment.getStatus() != null ? installment.getStatus() : "Pending");
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

            final String sqlUpdatePlan = "UPDATE payment_plan SET created_date = ?, total_amount = ?, start_date = ?, end_date = ?, no_of_installments = ? WHERE project_id = ?";
            try (PreparedStatement psPlan = connection.prepareStatement(sqlUpdatePlan)) {
                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                psPlan.setDate(1, currentDate);

                psPlan.setDouble(2, paymentPlanDTO.getTotalAmount());
                // If startDate is null, use current date instead
                if (paymentPlanDTO.getStartDate() != null) {
                    psPlan.setDate(3, new java.sql.Date(paymentPlanDTO.getStartDate().getTime()));
                } else {
                    psPlan.setDate(3, currentDate);  // <-- fallback to current date
                }

                // For endDate, allow null, or set date
                if (paymentPlanDTO.getEndDate() != null) {
                    psPlan.setDate(4, new java.sql.Date(paymentPlanDTO.getEndDate().getTime()));
                } else {
                    psPlan.setDate(4, currentDate);
                }
                psPlan.setInt(5, paymentPlanDTO.getNumberOfInstallments());
                psPlan.setInt(6, paymentPlanDTO.getProjectId());

                int updatedPlanRows = psPlan.executeUpdate();
                if (updatedPlanRows == 0) {
                    throw new RuntimeException("Payment Plan update failed. Project ID may not exist: " + paymentPlanDTO.getProjectId());
                }
            }

            final String sqlUpdateInstallment = "UPDATE installment SET milestone = ?, amount = ?, due_date = ?, status = ? WHERE installment_id = ?";
            try (PreparedStatement psInstallment = connection.prepareStatement(sqlUpdateInstallment)) {
                for (InstallmentDTO installment : paymentPlanDTO.getInstallments()) {
                    if (installment.getInstallmentId() == 0) {
                        throw new RuntimeException("Invalid installment ID for update: " + installment);
                    }

                    psInstallment.setString(1, installment.getMilestone());
                    psInstallment.setDouble(2, installment.getAmount());
                    psInstallment.setDate(3, installment.getDueDate());
                    psInstallment.setString(4, installment.getStatus());
                    psInstallment.setInt(5, installment.getInstallmentId());

                    int updatedInstallmentRows = psInstallment.executeUpdate();
                    if (updatedInstallmentRows == 0) {
                        throw new RuntimeException("Installment update failed. ID may not exist: " + installment.getInstallmentId());
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
    public PaymentPlanDTO deletePaymentPlanById(Integer id) {
        PaymentPlanDTO paymentPlan = getPaymentPlanByProjectId(id);
        if (paymentPlan == null) {
            throw new RuntimeException("No payment plan found with project id : " + id);
        }

        final String sql = "DELETE FROM payment_plan WHERE project_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment plan: " + e.getMessage(), e);
        }

        return paymentPlan;
    }

}
