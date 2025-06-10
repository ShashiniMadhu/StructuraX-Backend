package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.EmployeeDTO;

import java.util.List;

public interface AdminDAO {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(Integer id);

    EmployeeDTO updateEmployee(EmployeeDTO employeeDTO);

    EmployeeDTO deleteEmployeeById(Integer id);
}
