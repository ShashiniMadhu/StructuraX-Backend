package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.EmployeeLoginDTO;
import com.structurax.root.structurax.root.dto.EmployeeResponseDTO;
import com.structurax.root.structurax.root.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public ResponseEntity<EmployeeResponseDTO> login(@RequestBody EmployeeLoginDTO loginDTO) {
        EmployeeResponseDTO response = employeeService.login(loginDTO);
        return ResponseEntity.ok(response);
    }
}
