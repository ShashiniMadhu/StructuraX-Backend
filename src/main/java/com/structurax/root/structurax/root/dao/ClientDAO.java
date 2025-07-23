package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ClientOneDTO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;

import java.util.Optional;

public interface ClientDAO {
    Optional<ClientOneDTO> findByEmail(String email);
}
