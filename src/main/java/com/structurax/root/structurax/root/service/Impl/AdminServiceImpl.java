package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dao.ClientDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.AdminService;
import com.structurax.root.structurax.root.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminResponseDTO login(AdminLoginDTO loginDTO) {
        AdminDTO admin = adminDAO.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateTokenForAdmin(admin.getEmail(), admin.getRole(), admin.getAdminId());

        return new AdminResponseDTO(
                admin.getAdminId(),

                admin.getEmail(),
                admin.getRole(),
                token
        );
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO){
        logger.info("Creating employee: {}", employeeDTO);
        EmployeeDTO createdEmployee = adminDAO.createEmployee(employeeDTO);
        logger.info("Employee created successfully: {}", createdEmployee.getEmployeeId());
        return createdEmployee;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        logger.info("Fetching all employees");
        List<EmployeeDTO> employees = adminDAO.getAllEmployees();
        logger.info("Fetched {} employees", employees.size());
        return employees;
    }

    @Override
    public void deactivateEmployee(String empId) {
        // This method now sets a random password and marks availability as false
        // instead of setting password to NULL
        adminDAO.removeEmployeePassword(empId);
        logger.info("deactivated employee successfully");
    }


    @Override
    public EmployeeDTO getEmployeeById(String id) {
        logger.info("Fetching employee by ID: {}", id);
        EmployeeDTO employee = adminDAO.getEmployeeById(id);
        if (employee == null) {
            logger.warn("No employee found with ID: {}", id);
        } else {
            logger.info("Employee found: {}", employee.getEmployeeId());
        }
        return employee;
    }

    @Override
    public SupplierDTO addSupplier(SupplierDTO supplierDTO) {
        logger.info("Creating supplier: {}", supplierDTO);
        SupplierDTO createdSupplier = adminDAO.addSupplier(supplierDTO);
        logger.info("Supplier created successfully: {}", createdSupplier.getSupplier_id());
        return createdSupplier;    }
}
