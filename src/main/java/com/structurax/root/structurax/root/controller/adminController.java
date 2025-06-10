package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/admin")
public class adminController {

    @Autowired
    AdminService adminService;

    @PostMapping()
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO){
        adminService.createEmployee(employeeDTO);
        return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(){
        List<EmployeeDTO> employeeDTOS = adminService.getAllEmployees();
        return new ResponseEntity<>(employeeDTOS,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Integer id){
        EmployeeDTO employee = adminService.getEmployeeById(id);
        return new ResponseEntity<>(employee,HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<EmployeeDTO> updateEmployee(@RequestBody EmployeeDTO employeeDTO){
        adminService.updateEmployee(employeeDTO);
        return new ResponseEntity<>(employeeDTO,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EmployeeDTO> deleteEmployeeById(@PathVariable Integer id){
        EmployeeDTO employee = adminService.deleteEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }


}
