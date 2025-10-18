package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class AdminDAOImpl implements AdminDAO {
    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<AdminDTO> findByEmail(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try {
            AdminDTO admin = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AdminDTO.class), email);
            return Optional.ofNullable(admin);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserDTO createEmployee(UserDTO userDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        // BCrypt encoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        /* Generate employee ID if not provided
        if (userDTO.getUserId() == null || userDTO.getUserId().trim().isEmpty()) {
            userDTO.setUserId(generateEmployeeId());
        }*/

        try {
            final String sql = "INSERT INTO users (user_id, name, email, phone_number, address, type, joined_date, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, userDTO.getUserId());
            preparedStatement.setString(2, userDTO.getName());
            preparedStatement.setString(3, userDTO.getEmail());
            preparedStatement.setString(4, userDTO.getPhoneNumber());
            preparedStatement.setString(5, userDTO.getAddress());
            preparedStatement.setString(6, userDTO.getType());
            preparedStatement.setDate(7, java.sql.Date.valueOf(userDTO.getJoinedDate()));
            preparedStatement.setString(8, hashedPassword);
      //      preparedStatement.setString(10, userDTO.getProfileImageUrl());


            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to create employee");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting employee: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }
        return userDTO;
    }

    @Override
    public List<NewEmployeeDTO> getAllEmployees() {
        final List<NewEmployeeDTO> employeeList = new ArrayList<>();
        final String sql = """
        SELECT 
            e.employee_id,
            u.user_id,
            u.name,
            u.email,
            u.phone_number,
            u.address,
            u.type,
            u.joined_date,
            u.profile_image_url,
            e.availability
        FROM employee e
        INNER JOIN users u ON e.user_id = u.user_id
        WHERE u.type IN ('Designer', 'Director', 'Senior_QS_Officer', 'QS_Officer', 'Project_Manager', 'Site_Supervisor', 'Legal_Officer', 'Financial_Officer')
        ORDER BY e.employee_id ASC
    """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                Date joinedDate = resultSet.getDate("joined_date");
                LocalDate localJoinedDate = (joinedDate != null) ? ((java.sql.Date) joinedDate).toLocalDate() : null;

                NewEmployeeDTO employee = new NewEmployeeDTO(
                        resultSet.getString("employee_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address"),
                        resultSet.getString("type"),
                        localJoinedDate,
                        resultSet.getString("availability"),
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
    public void deactivateEmployee(String empId) {
        String randomPassword = generateRandomPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedRandomPassword = passwordEncoder.encode(randomPassword);

        final String sql = """
        UPDATE users u 
        INNER JOIN employee e ON u.user_id = e.user_id
        SET u.password = ?, e.availability = 'Deactive'
        WHERE e.employee_id = ?
    """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, hashedRandomPassword);
            preparedStatement.setString(2, empId);

            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Employee deactivation failed for employee: " + empId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating employee: " + e.getMessage(), e);
        }
    }

    private String generateRandomPassword() {
        return "DEACTIVATED_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public UserDTO getEmployeeById(String empId) {
        final String sql = """
        SELECT 
            e.employee_id,
            u.user_id,
            u.name,
            u.email,
            u.phone_number,
            u.address,
            u.type,
            u.joined_date,
            u.profile_image_url,
            e.availability
        FROM employee e
        INNER JOIN users u ON e.user_id = u.user_id
        WHERE e.employee_id = ? 
        AND u.type IN ('Designer','Director','Senior_QS_Officer','QS_Officer','Project_Manager','Site_Supervisor','Legal_Officer','Financial_Officer')
    """;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, empId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new UserDTO(
                        resultSet.getInt("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("address"),
                        resultSet.getString("type"),
                        resultSet.getDate("joined_date").toLocalDate(),
                        resultSet.getString("availability"),
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

    @Override
    public UserDTO addSupplier(UserDTO userDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        try {
            final String sql = "INSERT INTO users (name, email, phone_number, address, type, joined_date, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, userDTO.getName());
            preparedStatement.setString(2, userDTO.getEmail());
            preparedStatement.setString(3, userDTO.getPhoneNumber());
            preparedStatement.setString(4, userDTO.getAddress());
            preparedStatement.setString(5, userDTO.getType()); // "Supplier"
            preparedStatement.setDate(6, java.sql.Date.valueOf(userDTO.getJoinedDate()));
            preparedStatement.setString(7, hashedPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to add supplier");
            }

            // Get generated user_id
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                userDTO.setUserId(generatedKeys.getInt(1));
            }

            return userDTO;

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting supplier: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }
    }

}