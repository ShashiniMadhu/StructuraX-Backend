package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

@Service
public interface AdminService {
    /**
     * To create an employee
     * @param employeeDTO
     * @return created employee
     */
    AdminResponseDTO login(AdminLoginDTO adminLoginDTO);

    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    List<EmployeeDTO> getAllEmployees();

    void deactivateEmployee(String empId);

    EmployeeDTO getEmployeeById(String id);

    SupplierDTO addSupplier(SupplierDTO supplierDTO);


}
