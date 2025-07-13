package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminDAO adminDAO;

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
    public DesignFullDTO getDesignById(String id) {
        DesignFullDTO design = adminDAO.getDesignById(id);
        logger.info("Design fetched: {}", design.getDesignId());
        return design;
    }

    @Override
    public List<DesignFullDTO> getAllDesigns() {
        logger.info("Fetching all designs");
        List<DesignFullDTO> designs = adminDAO.getAllDesigns();
        logger.info("Fetched {} designs", designs.size());
        return designs;
    }

    @Override
    public DesignDTO deleteDesign(String id) {
        DesignDTO design = adminDAO.deleteDesign(id);
        logger.info("Design deleted: {}", design.getDesignId());
        return design;
    }

    @Override
    public DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign) {
        DesignFullDTO design = adminDAO.updateDesign(id, updatedDesign);
        logger.info("Design updated successfully with ID: " + id);
        return design;
    }

}
