package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.UserDAO;
import com.structurax.root.structurax.root.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Optional<UserDTO> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email=?";
        try{
            UserDTO user = jdbcTemplate.queryForObject(sql, new
                    BeanPropertyRowMapper<>(UserDTO.class), email);
            return Optional.ofNullable(user);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public void save(UserDTO user) {
        String sql = "UPDATE users SET password = ?, reset_token = ?, token_expiry = ? WHERE email =?";
        jdbcTemplate.update(sql,
                user.getPassword(),
                user.getResetToken(),
                user.getTokenExpiry(),
                user.getEmail());
    }

    @Override
    public Optional<UserDTO> findByResetToken(String token) {
        String sql = "SELECT * FROM users WHERE reset_token = ?";
        try{
            UserDTO user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserDTO.class),token);
            return Optional.ofNullable(user);
        }catch(EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

   /* @Override
    public void save(UserDTO user) {
        String sql = "UPADTE user SET password = ?, reset_token = ?"

    }
*/

    @Override
    public String findEmployeeIdByUserId(Integer userId) {
        String sql = "SELECT e.employee_id FROM employee e " +
                "INNER JOIN users u ON e.user_id=u.user_id WHERE u.user_id=?";
        try{
            return jdbcTemplate.queryForObject(sql,String.class, userId);
        }catch(EmptyResultDataAccessException e){
            return "";
        }

    }

    @Override
    public String findClientIdByUserId(Integer userId) {
        String sql = "SELECT c.client_id FROM client c " +
                "INNER JOIN users u ON c.user_id=u.user_id WHERE u.user_id=?";
        try{
            return jdbcTemplate.queryForObject(sql,String.class, userId);
        }catch(EmptyResultDataAccessException e){
            return "";
        }
    }

    @Override
    public String findSupplierIdByUserId(Integer userId) {
        String sql = "SELECT s.supplier_id FROM supplier s " +
                "INNER JOIN users u ON s.user_id=u.user_id WHERE u.user_id=?";
        try{
            return jdbcTemplate.queryForObject(sql,String.class, userId);
        }catch(EmptyResultDataAccessException e){
            return "";
        }
    }

    @Override
    public String findAdminIdByUserId(Integer userId) {
        String sql = "SELECT a.admin_id FROM admin a " +
                "INNER JOIN users u ON a.user_id=u.user_id WHERE u.user_id=?";
        try{
            return jdbcTemplate.queryForObject(sql,String.class, userId);
        }catch(EmptyResultDataAccessException e){
            return "";
        }
    }

    @Override
    public Optional<UserDTO> getUserProfileByAnyId(String id) {
        String sql = null;

        // Pattern detection
        if (id.startsWith("EMP") || id.startsWith("DSN")) {
            // Employee ID pattern
            sql = "SELECT u.* FROM users u " +
                    "INNER JOIN employee e ON u.user_id = e.user_id " +
                    "WHERE e.employee_id = ?";
        } else if (id.startsWith("CLI")) {
            // Client ID pattern
            sql = "SELECT u.* FROM users u " +
                    "INNER JOIN client c ON u.user_id = c.user_id " +
                    "WHERE c.client_id = ?";
        } else if (id.matches("\\d+")) {
            // Pure integer pattern (Supplier ID)
            sql = "SELECT u.* FROM users u " +
                    "INNER JOIN supplier s ON u.user_id = s.user_id " +
                    "WHERE s.supplier_id = ?";
        } else {
            // Unknown pattern
            return Optional.empty();
        }

        try {
            UserDTO user = jdbcTemplate.queryForObject(sql,
                    new BeanPropertyRowMapper<>(UserDTO.class), id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserDTO> getAllUsers() {
        String sql = """
            SELECT 
                u.user_id,
                u.name,
                u.email,
                u.phone_number,
                u.address,
                u.type,
                u.joined_date,
                u.profile_image_url,
                CASE 
                    WHEN u.type = 'Admin' THEN 'N/A'
                    WHEN e.availability IS NOT NULL THEN e.availability
                    ELSE 'N/A'
                END AS availability
            FROM users u
            LEFT JOIN employee e ON u.user_id = e.user_id
            WHERE u.type != 'Admin'
            ORDER BY u.user_id ASC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            LocalDate joinedDate = null;
            Date joinedDateSql = rs.getDate("joined_date");
            if (joinedDateSql != null) {
                joinedDate = joinedDateSql.toLocalDate();
            }

            return new UserDTO(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("type"),
                    joinedDate,
                    rs.getString("availability"),
                    rs.getString("profile_image_url")
            );
        });
    }


}
