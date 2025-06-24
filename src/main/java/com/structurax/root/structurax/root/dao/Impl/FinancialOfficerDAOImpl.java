package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.FinancialOfficerDAO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Override
    public PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        final String sql = "INSERT INTO payment_plan (milestone, amount, status) VALUES (?, ?, ?)";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, paymentPlanDTO.getMilestone());
            preparedStatement.setBigDecimal(2, paymentPlanDTO.getAmount());
            preparedStatement.setString(3, paymentPlanDTO.getStatus());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting payment plan: " + e.getMessage(), e);
        }

        return paymentPlanDTO;
    }

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
                            rs.getString("milestone"),
                            rs.getBigDecimal("amount"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payment plan by ID: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        final String sql = "UPDATE payment_plan SET milestone = ?, amount = ?, status = ? WHERE payment_plan_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, paymentPlanDTO.getMilestone());
            preparedStatement.setBigDecimal(2, paymentPlanDTO.getAmount());
            preparedStatement.setString(3, paymentPlanDTO.getStatus());
            preparedStatement.setInt(4, paymentPlanDTO.getPaymentPlanId());

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
    public PaymentPlanDTO deletePaymentPlanById(Integer id) {
        PaymentPlanDTO paymentPlan = getPaymentPlanById(id);
        if (paymentPlan == null) {
            throw new RuntimeException("No payment plan found with id: " + id);
        }

        final String sql = "DELETE FROM payment_plan WHERE payment_plan_id = ?";

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
