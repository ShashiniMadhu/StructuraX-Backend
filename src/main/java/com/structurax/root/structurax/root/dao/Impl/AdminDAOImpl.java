package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;//for password hashing

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

        try {
            final String sql = "INSERT INTO employee (full_name, email, contact_number, address, employee_type, join_date, salary, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, employeeDTO.getFullName());
            preparedStatement.setString(2, employeeDTO.getEmail());
            preparedStatement.setString(3, employeeDTO.getContactNumber());
            preparedStatement.setString(4, employeeDTO.getAddress());
            preparedStatement.setString(5, employeeDTO.getEmployeeType());
            preparedStatement.setDate(6, java.sql.Date.valueOf(employeeDTO.getJoinDate()));
            preparedStatement.setBigDecimal(7, employeeDTO.getSalary());
            preparedStatement.setString(8, hashedPassword); // Save hashed password

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting employee: " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
                        resultSet.getInt("emp_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        resultSet.getString("contact_number"),
                        resultSet.getString("address"),
                        resultSet.getString("employee_type"),
                        resultSet.getDate("join_date").toLocalDate(),
                        resultSet.getBigDecimal("salary"),
                        resultSet.getString("password") // Optional — consider omitting for API responses
                );
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return employeeList;
    }

    @Override
    public EmployeeDTO getEmployeeById(Integer empId) {
        final String sql = "SELECT * FROM employee WHERE emp_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, empId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new EmployeeDTO(
                        resultSet.getInt("emp_id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        resultSet.getString("contact_number"),
                        resultSet.getString("address"),
                        resultSet.getString("employee_type"),
                        resultSet.getDate("join_date").toLocalDate(),
                        resultSet.getBigDecimal("salary"),
                        resultSet.getString("password") // Optional: remove if sensitive
                );
            } else {
                return null; // or throw new RuntimeException("Employee not found")
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching employee by ID: " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(employeeDTO.getPassword());

        final String sql = "UPDATE employee SET full_name = ?, email = ?, contact_number = ?, address = ?, " +
                "employee_type = ?, join_date = ?, salary = ?, password = ? WHERE emp_id = ?";

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, employeeDTO.getFullName());
            preparedStatement.setString(2, employeeDTO.getEmail());
            preparedStatement.setString(3, employeeDTO.getContactNumber());
            preparedStatement.setString(4, employeeDTO.getAddress());
            preparedStatement.setString(5, employeeDTO.getEmployeeType());
            preparedStatement.setDate(6, java.sql.Date.valueOf(employeeDTO.getJoinDate()));
            preparedStatement.setBigDecimal(7, employeeDTO.getSalary());
            preparedStatement.setString(8, hashedPassword); // hashed password
            preparedStatement.setInt(9, employeeDTO.getEmpId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Employee update failed. Employee ID may not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee: " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return employeeDTO;
    }

    @Override
    public EmployeeDTO deleteEmployeeById(Integer empId) {
        EmployeeDTO employee = getEmployeeById(empId); // Reuse this method if you already have it

        if (employee == null) {
            throw new RuntimeException("No employee found with id: " + empId);
        }

        final String sql = "DELETE FROM employee WHERE emp_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, empId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee: " + e.getMessage(), e);
        }

        return employee;
    }

}
