package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.EmployeeDTO;

import java.util.Optional;

public interface EmployeeDAO {
    Optional<EmployeeDTO> findByEmail(String email);
}
