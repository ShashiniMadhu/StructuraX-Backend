package com.structurax.root.structurax.root.dao.Impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.AdminDTO;
import com.structurax.root.structurax.root.dto.FullClientDTO;
import com.structurax.root.structurax.root.dto.NewEmployeeDTO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.UserDTO;

@Repository
public class AdminDAOImpl implements AdminDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // This is no longer needed when using JdbcTemplate consistently.
    // @Autowired
    // private DatabaseConnection databaseConnection;

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
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        final String sql = "INSERT INTO users (user_id, name, email, phone_number, address, type, joined_date, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        int rowsAffected = jdbcTemplate.update(sql,
                userDTO.getUserId(),
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getPhoneNumber(),
                userDTO.getAddress(),
                userDTO.getType(),
                java.sql.Date.valueOf(userDTO.getJoinedDate()),
                hashedPassword
        );

        if (rowsAffected == 0) {
            throw new RuntimeException("Failed to create employee");
        }
        return userDTO;
    }

    @Override
    public List<NewEmployeeDTO> getAllEmployees() {
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
        
        // Use JdbcTemplate's query method with a RowMapper for clean data mapping.
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Date joinedDate = rs.getDate("joined_date");
            LocalDate localJoinedDate = (joinedDate != null) ? ((java.sql.Date) joinedDate).toLocalDate() : null;

            return new NewEmployeeDTO(
                    rs.getString("employee_id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("type"),
                    localJoinedDate,
                    rs.getString("availability"),
                    rs.getString("profile_image_url")
            );
        });
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
        
        int rowsAffected = jdbcTemplate.update(sql, hashedRandomPassword, empId);
        if (rowsAffected == 0) {
            throw new RuntimeException("Employee deactivation failed for employee: " + empId);
        }
    }

    // Helper method to generate a random password for deactivation
    private String generateRandomPassword() {
        return "DEACTIVATED_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * CONFLICT RESOLVED:
     * This version combines the logic from both branches.
     * 1. It uses the more specific query from the 'Dev' branch to ensure only valid employee types are returned.
     * 2. It fulfills the core requirement of getting an employee by their ID from the 'Malith-Project' branch.
     * 3. It has been refactored to use Spring's JdbcTemplate for better code quality and consistency.
     */
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

        try {
            // BeanPropertyRowMapper automatically maps columns (like 'phone_number') to fields (like 'phoneNumber').
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserDTO.class), empId);
        } catch (EmptyResultDataAccessException e) {
            return null; // No employee found
        }
    }

    // Helper method to generate employee ID (Now using JdbcTemplate)
    private String generateEmployeeId() {
        String sql = "SELECT employee_id FROM employee ORDER BY employee_id DESC LIMIT 1";
        try {
            String lastId = jdbcTemplate.queryForObject(sql, String.class);
            if (lastId != null) {
                String numberPart = lastId.substring(4); // Remove "EMP_"
                int nextNumber = Integer.parseInt(numberPart) + 1;
                return String.format("EMP_%03d", nextNumber);
            }
        } catch (EmptyResultDataAccessException e) {
            return "EMP_001"; // First employee
        }
        return "EMP_001";
    }

    @Override
    public UserDTO addSupplier(UserDTO userDTO) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        final String sql = "INSERT INTO users (name, email, phone_number, address, type, joined_date, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userDTO.getName());
            ps.setString(2, userDTO.getEmail());
            ps.setString(3, userDTO.getPhoneNumber());
            ps.setString(4, userDTO.getAddress());
            ps.setString(5, userDTO.getType()); // "Supplier"
            ps.setDate(6, java.sql.Date.valueOf(userDTO.getJoinedDate()));
            ps.setString(7, hashedPassword);
            return ps;
        }, keyHolder);

        // Set the generated user_id back to the DTO
        if (keyHolder.getKey() != null) {
            userDTO.setUserId(keyHolder.getKey().intValue());
        } else {
            throw new RuntimeException("Failed to add supplier, no ID obtained.");
        }
        
        return userDTO;
    }

    @Override
    public List<FullClientDTO> getAllClients() {
        final String sql = """
            SELECT 
                c.client_id,
                u.user_id,
                u.name,
                u.email,
                u.phone_number,
                u.address,
                u.profile_image_url,
                c.type AS client_type,
                p.project_id,
                p.name as project_name,
                p.status,
                p.budget,
                p.location,
                p.start_date,
                p.due_date
            FROM client c
            INNER JOIN users u ON c.user_id = u.user_id
            LEFT JOIN project p ON c.client_id = p.client_id
            WHERE u.type = 'Client'
            ORDER BY c.client_id, p.project_id
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            FullClientDTO client = new FullClientDTO();
            client.setClientId(rs.getString("client_id"));
            client.setUserId(rs.getInt("user_id"));
            client.setName(rs.getString("name"));
            client.setEmail(rs.getString("email"));
            client.setPhoneNumber(rs.getString("phone_number"));
            client.setAddress(rs.getString("address"));
            client.setProfileImageUrl(rs.getString("profile_image_url"));
            client.setClientType(rs.getString("client_type"));
            client.setProjectId(rs.getString("project_id"));
            client.setProjectName(rs.getString("project_name"));
            client.setStatus(rs.getString("status"));
            client.setBudget(rs.getDouble("budget"));
            client.setLocation(rs.getString("location"));

            Date startDate = rs.getDate("start_date");
            if (startDate != null) {
                client.setStartDate(((java.sql.Date) startDate).toLocalDate());
            }
            Date dueDate = rs.getDate("due_date");
            if (dueDate != null) {
                client.setDueDate(((java.sql.Date) dueDate).toLocalDate());
            }
            return client;
        });
    }

    @Override
    public List<Project1DTO> getAllProjects() {
        final String sql = """
            SELECT 
                p.project_id,
                p.name,
                p.description,
                p.location,
                p.status,
                p.start_date,
                p.due_date,
                p.estimated_value,
                p.budget,
                p.category,
                p.client_id,
                p.qs_id,
                p.pm_id,
                p.ss_id
            FROM project p
            ORDER BY p.project_id ASC
        """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Project1DTO project = new Project1DTO();
            project.setProjectId(rs.getString("project_id"));
            project.setName(rs.getString("name"));
            project.setDescription(rs.getString("description"));
            project.setLocation(rs.getString("location"));
            project.setStatus(rs.getString("status"));
            project.setCategory(rs.getString("category"));
            project.setEstimatedValue(rs.getBigDecimal("estimated_value"));
            project.setBudget(rs.getBigDecimal("budget"));

            Date startDate = rs.getDate("start_date");
            if (startDate != null) {
                project.setStartDate(((java.sql.Date) startDate).toLocalDate());
            }
            Date dueDate = rs.getDate("due_date");
            if (dueDate != null) {
                project.setDueDate(((java.sql.Date) dueDate).toLocalDate());
            }

            project.setOwnerId(rs.getString("client_id"));
            project.setQsId(rs.getString("qs_id"));
            project.setSpId(rs.getString("pm_id"));
            project.setPlanId(rs.getString("ss_id"));
            return project;
        });
    }

    @Override
    public UserDTO getAdminDetails() {
        try {
            String sql = "SELECT user_id, name, email, phone_number, address, type, joined_date, profile_image_url " +
                    "FROM `users` WHERE type = 'Admin' LIMIT 1";

            List<UserDTO> results = jdbcTemplate.query(sql, (rs, rowNum) ->
                    new UserDTO(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("address"),
                            rs.getString("type"),
                            rs.getDate("joined_date").toLocalDate(),
                            null, // availability - this column doesn't exist in your users table
                            rs.getString("profile_image_url")
                    )
            );

            return results.isEmpty() ? null : results.get(0);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch admin details: " + e.getMessage(), e);
        }
    }
}