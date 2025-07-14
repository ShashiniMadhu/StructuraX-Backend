package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.EmployeeLoginDTO;
import com.structurax.root.structurax.root.dto.EmployeeResponseDTO;

public interface EmployeeService {
    EmployeeResponseDTO login(EmployeeLoginDTO loginDTO);
}
