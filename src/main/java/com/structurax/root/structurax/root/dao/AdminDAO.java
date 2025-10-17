package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;
import java.util.Optional;

public interface AdminDAO {
    /**
     * create an employee by admin
     * @param userDTO
     * @return EmployeeDTO
     */
    UserDTO createEmployee(UserDTO userDTO);

    /**
     * get the all employees
     * @return EmployeeDTO
     */
    Optional<AdminDTO> findByEmail(String email);

    List<EmployeeDTO> getAllEmployees();

    void removeEmployeePassword(String empId);

    /**
     * get the employee by employee_id
     * @param id
     * @return EmployeeDTO
     */
    EmployeeDTO getEmployeeById(String id);

    SupplierDTO addSupplier(SupplierDTO supplierDTO);
}
