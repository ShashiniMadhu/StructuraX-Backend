package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDAO adminDAO;

    @Override
    @CacheEvict(value = {"employeeCache", "allEmplyeesCache"}, allEntries = true)
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO){
        EmployeeDTO employeeDTO1=adminDAO.createEmployee(employeeDTO);
        return employeeDTO1;
    }

    @Override
    @Cacheable(value = "allEmployeesCache", key = "'allEmployees'")
    public List<EmployeeDTO> getAllEmployees() {
        return adminDAO.getAllEmployees();
    }

    @Override
    @Cacheable(value = "employeeCache", key = "#id")
    public EmployeeDTO getEmployeeById(String id) {
        return adminDAO.getEmployeeById(id);
    }

    @Override
    @CacheEvict(value = "allEmployeesCache", allEntries = true)
    @CachePut(value = "employeeCache", key = "#employeeDTO.employeeId")
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        return adminDAO.updateEmployee(employeeDTO);
    }

    @Override
    @CacheEvict(value = {"employeeCache", "allEmployeesCache"}, allEntries = true)
    public EmployeeDTO deleteEmployeeById(String id) {
        return adminDAO.deleteEmployeeById(id);
    }
}
