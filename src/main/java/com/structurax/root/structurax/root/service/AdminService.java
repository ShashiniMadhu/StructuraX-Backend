package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

@Service
public interface AdminService {
    /**
     * To create an employee
     * @param userDTO
     * @return created employee
     */


    UserDTO createEmployee(UserDTO userDTO);

    List<NewEmployeeDTO> getAllEmployees();

    public void deactivateEmployee(String empId);

    UserDTO getEmployeeById(String id);

    UserDTO addSupplier(UserDTO userDTO) ;


}
