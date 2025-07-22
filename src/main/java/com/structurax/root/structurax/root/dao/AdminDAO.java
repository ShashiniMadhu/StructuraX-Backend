package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;

import java.util.List;

public interface AdminDAO {
    /**
     * create an employee by admin
     * @param employeeDTO
     * @return EmployeeDTO
     */
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    /**
     * get the all employees
     * @return EmployeeDTO
     */
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
