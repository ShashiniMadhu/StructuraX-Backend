package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ClientDAO;
import com.structurax.root.structurax.root.dto.ClientOneDTO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ClientDAOImpl implements ClientDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<ClientOneDTO> findByEmail(String email) {
        String sql = "SELECT * FROM client WHERE email = ?";
        try {
            ClientOneDTO client = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ClientOneDTO.class), email);
            return Optional.ofNullable(client);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
