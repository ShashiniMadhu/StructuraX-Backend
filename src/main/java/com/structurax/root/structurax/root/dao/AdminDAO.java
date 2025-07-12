package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.EmployeeDTO;

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

    /**
     * update employee(employee_id add to the RestAPI)
     * @param employeeDTO
     * @return EmployeeDTO
     */
    EmployeeDTO updateEmployee(EmployeeDTO employeeDTO);

    /**
     * delete an employee by employee_id
     * @param id
     * @return
     */
    EmployeeDTO deleteEmployeeById(String id);
}
