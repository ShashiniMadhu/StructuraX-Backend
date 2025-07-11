package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping(value = "/admin")
public class AdminController { // Fixed class name capitalization

    @Autowired
    private AdminService adminService;

    @PostMapping()
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeDTO employeeDTO){
        try {
            EmployeeDTO createdEmployee = adminService.createEmployee(employeeDTO);
            return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllEmployees(){
        try {
            List<EmployeeDTO> employeeDTOS = adminService.getAllEmployees();
            return new ResponseEntity<>(employeeDTOS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Integer id){
        try {
            EmployeeDTO employee = adminService.getEmployeeById(id);
            if (employee == null) {
                return new ResponseEntity<>("Employee not found with id: " + id, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Integer id, @RequestBody EmployeeDTO employeeDTO){
        try {
            employeeDTO.setEmpId(id); // Set the ID from path variable
            EmployeeDTO updatedEmployee = adminService.updateEmployee(employeeDTO);
            return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployeeById(@PathVariable Integer id){
        try {
            EmployeeDTO employee = adminService.deleteEmployeeById(id);
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}