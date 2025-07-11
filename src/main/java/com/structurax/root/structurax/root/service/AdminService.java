package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.EmployeeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {
    /**
     * To create an employee
     * @param employeeDTO
     * @return created employee
     */
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(String id);

    EmployeeDTO updateEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO deleteEmployeeById(String id);
}
