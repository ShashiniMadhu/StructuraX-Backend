package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.repository.AdminRepository;
import com.structurax.root.structurax.root.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminRepository adminRepository;

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        EmployeeDTO employeeDTO1=adminRepository.createEmployee(employeeDTO);
        return employeeDTO1;
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return adminRepository.getAllEmployees();
    }

    @Override
    public EmployeeDTO getEmployeeById(Integer id) {
        return adminRepository.getEmployeeById(id);
    }

    @Override
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        return adminRepository.updateEmployee(employeeDTO);
    }

    @Override
    public EmployeeDTO deleteEmployeeById(Integer id) {
        return adminRepository.deleteEmployeeById(id);
    }
}
