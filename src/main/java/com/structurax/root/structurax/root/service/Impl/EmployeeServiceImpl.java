package com.structurax.root.structurax.root.service.Impl;


import com.structurax.root.structurax.root.dao.EmployeeDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.dto.EmployeeLoginDTO;
import com.structurax.root.structurax.root.dto.EmployeeResponseDTO;
import com.structurax.root.structurax.root.service.EmployeeService;
import com.structurax.root.structurax.root.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmployeeDAO employeeDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public EmployeeResponseDTO login(EmployeeLoginDTO loginDTO) {
        EmployeeDTO employee = employeeDAO.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), employee.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(employee.getEmail(), employee.getType());

        return new EmployeeResponseDTO(
                employee.getEmployeeId(),
                employee.getName(),
                employee.getEmail(),
                employee.getType(),
                token
        );
    }
}
