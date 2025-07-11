package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdminDAOImpl implements AdminDAO {
    @Autowired
    private DatabaseConnection databaseConnection;

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        // BCrypt encoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(employeeDTO.getPassword());

        // Generate employee ID if not provided
        if (employeeDTO.getEmployeeId() == null || employeeDTO.getEmployeeId().trim().isEmpty()) {
            employeeDTO.setEmployeeId(generateEmployeeId());
        }

        try {
            final String sql = "INSERT INTO employee (employee_id, name, email, phone_number, address, type, joined_date, password, availability, profile_image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, employeeDTO.getEmployeeId());
            preparedStatement.setString(2, employeeDTO.getName());
            preparedStatement.setString(3, employeeDTO.getEmail());
            preparedStatement.setString(4, employeeDTO.getPhoneNumber());
            preparedStatement.setString(5, employeeDTO.getAddress());
            preparedStatement.setString(6, employeeDTO.getType());
            preparedStatement.setDate(7, java.sql.Date.valueOf(employeeDTO.getJoinedDate()));
            preparedStatement.setString(8, hashedPassword);
            preparedStatement.setBoolean(9, employeeDTO.getAvailability());
            preparedStatement.setString(10, employeeDTO.getProfileImageUrl());


            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to create employee");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting employee: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }
        return employeeDTO;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        final List<EmployeeDTO> employeeList = new ArrayList<>();
        final String sql = "SELECT * FROM employee";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                EmployeeDTO employee = new EmployeeDTO(
                        resultSet.getString("employee_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address"),
                        resultSet.getString("type"),
                        resultSet.getDate("joined_date").toLocalDate(),
                        null, // Don't expose password in responses
                        resultSet.getBoolean("availability"),
                        resultSet.getString("profile_image_url")
                );
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all employees: " + e.getMessage(), e);
        }

        return employeeList;
    }

    @Override
    public EmployeeDTO getEmployeeById(String empId) {
        final String sql = "SELECT * FROM employee WHERE employee_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, empId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new EmployeeDTO(
                        resultSet.getString("employee_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address"),
                        resultSet.getString("type"),
                        resultSet.getDate("joined_date").toLocalDate(),
                        null, // Don't expose password
                        resultSet.getBoolean("availability"),
                        resultSet.getString("profile_image_url")

                );
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching employee by ID: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, preparedStatement, connection);
        }
    }

    @Override
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        // Only hash password if it's provided and not null
        String hashedPassword = null;
        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().trim().isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            hashedPassword = passwordEncoder.encode(employeeDTO.getPassword());
        }

        final String sql = "UPDATE employee SET name = ?, email = ?, phone_number = ?, address = ?, " +
                "type = ?, joined_date = ?, password = ?, availability = ?, profile_image_url = ? WHERE employee_id = ?";

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, employeeDTO.getName());
            preparedStatement.setString(2, employeeDTO.getEmail());
            preparedStatement.setString(3, employeeDTO.getPhoneNumber());
            preparedStatement.setString(4, employeeDTO.getAddress());
            preparedStatement.setString(5, employeeDTO.getType());
            preparedStatement.setDate(6, java.sql.Date.valueOf(employeeDTO.getJoinedDate()));
            preparedStatement.setString(7, hashedPassword);
            preparedStatement.setBoolean(8, employeeDTO.getAvailability());
            preparedStatement.setString(9, employeeDTO.getProfileImageUrl()); 

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Employee update failed. Employee ID may not exist: " + employeeDTO.getEmployeeId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }

        return employeeDTO;
    }

    @Override
    public EmployeeDTO deleteEmployeeById(String empId) {
        EmployeeDTO employee = getEmployeeById(empId);

        if (employee == null) {
            throw new RuntimeException("No employee found with id: " + empId);
        }

        final String sql = "DELETE FROM employee WHERE employee_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, empId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to delete employee with id: " + empId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee: " + e.getMessage(), e);
        }

        return employee;
    }

    // Helper method to generate employee ID
    private String generateEmployeeId() {
        String sql = "SELECT employee_id FROM employee ORDER BY employee_id DESC LIMIT 1";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            if (resultSet.next()) {
                String lastId = resultSet.getString("employee_id");
                // Extract the number part from EMP_XXX format
                String numberPart = lastId.substring(4); // Remove "EMP_"
                int nextNumber = Integer.parseInt(numberPart) + 1;
                return String.format("EMP_%03d", nextNumber);
            } else {
                // First employee
                return "EMP_001";
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating employee ID: " + e.getMessage(), e);
        }
    }

    // Helper method to close resources
    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}