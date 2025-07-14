package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.EmployeeDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<EmployeeDTO> findByEmail(String email) {
        String sql = "SELECT * FROM employee WHERE email = ?";
        try {
            EmployeeDTO employee = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(EmployeeDTO.class), email);
            return Optional.ofNullable(employee);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}

