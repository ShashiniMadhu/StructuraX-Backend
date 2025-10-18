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
    public UserDTO createEmployee(UserDTO userDTO){
        logger.info("Creating employee: {}", userDTO);
        UserDTO createdEmployee = adminDAO.createEmployee(userDTO);
        logger.info("Employee created successfully: {}", createdEmployee.getUserId());
        return createdEmployee;
    }

    @Override
    public List<NewEmployeeDTO> getAllEmployees() {
        logger.info("Fetching all employees");
        List<NewEmployeeDTO> employees = adminDAO.getAllEmployees();
        logger.info("Fetched {} employees", employees.size());
        return employees;
    }

    @Override
    public void deactivateEmployee(String empId) {
        logger.info("Deactivating employee: {}", empId);
        adminDAO.deactivateEmployee(empId);
        logger.info("Employee deactivated successfully: {}", empId);
    }


    @Override
    public UserDTO getEmployeeById(String id) {
        logger.info("Fetching employee by ID: {}", id);
        UserDTO employee = adminDAO.getEmployeeById(id);
        if (employee == null) {
            logger.warn("No employee found with ID: {}", id);
        } else {
            logger.info("Employee found: {}", employee.getUserId());
        }
        return employee;
    }

    @Override
    public UserDTO addSupplier(UserDTO userDTO) {
        logger.info("Creating supplier: {}", userDTO.getName());
        UserDTO createdSupplier = adminDAO.addSupplier(userDTO);
        logger.info("Supplier created successfully with user_id: {}", createdSupplier.getUserId());
        return createdSupplier;
    }
}
