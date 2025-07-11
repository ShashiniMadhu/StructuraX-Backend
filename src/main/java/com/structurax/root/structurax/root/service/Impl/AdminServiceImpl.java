package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminDAO adminDAO;

    @Override
    @CacheEvict(value = {"employeeCache", "allEmplyeesCache"}, allEntries = true)
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO){
        logger.info("Creating employee: {}", employeeDTO);
        EmployeeDTO createdEmployee = adminDAO.createEmployee(employeeDTO);
        logger.info("Employee created successfully: {}", createdEmployee.getEmployeeId());
        return createdEmployee;
    }

    @Override
    @Cacheable(value = "allEmployeesCache", key = "'allEmployees'")
    public List<EmployeeDTO> getAllEmployees() {
        logger.info("Fetching all employees");
        List<EmployeeDTO> employees = adminDAO.getAllEmployees();
        logger.info("Fetched {} employees", employees.size());
        return employees;
    }

    @Override
    @Cacheable(value = "employeeCache", key = "#id")
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
    @CacheEvict(value = "allEmployeesCache", allEntries = true)
    @CachePut(value = "employeeCache", key = "#employeeDTO.employeeId")
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        logger.info("Updating employee with ID: {}", employeeDTO.getEmployeeId());
        EmployeeDTO updatedEmployee = adminDAO.updateEmployee(employeeDTO);
        logger.info("Employee updated successfully: {}", updatedEmployee.getEmployeeId());
        return updatedEmployee;
    }

    @Override
    @CacheEvict(value = {"employeeCache", "allEmployeesCache"}, allEntries = true)
    public EmployeeDTO deleteEmployeeById(String id) {
        logger.info("Deleting employee with ID: {}", id);
        EmployeeDTO deletedEmployee = adminDAO.deleteEmployeeById(id);
        logger.info("Employee deleted successfully: {}", id);
        return deletedEmployee;
    }
}
