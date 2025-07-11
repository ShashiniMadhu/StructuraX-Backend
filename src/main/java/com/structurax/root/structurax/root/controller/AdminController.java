package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.Constants.Constants;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping(consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        try {
            final EmployeeDTO createdEmployee = adminService.createEmployee(employeeDTO);
            return ResponseEntity.ok(createdEmployee);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getAllEmployees() {
        try {
            final List employeeDTOS = adminService.getAllEmployees();
            return ResponseEntity.ok(employeeDTOS);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getEmployeeById(@PathVariable @Pattern(regexp = "^EMP_\\d{3}$", message = "Employee ID must follow format EMP_XXX") String id) {
        try {
            final EmployeeDTO employee = adminService.getEmployeeById(id);
            if (employee == null) {
                return new ResponseEntity<>("Employee not found with id: " + id, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> updateEmployee(@PathVariable @Pattern(regexp = "^EMP_\\d{3}$", message = "Employee ID must follow format EMP_XXX") String id, @RequestBody @Valid EmployeeDTO employeeDTO) {
        try {
            employeeDTO.setEmployeeId(id); // Set the ID from path variable
            final EmployeeDTO updatedEmployee = adminService.updateEmployee(employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> deleteEmployeeById(@PathVariable @Pattern(regexp = "^EMP_\\d{3}$", message = "Employee ID must follow format EMP_XXX") String id) {
        try {
            final EmployeeDTO employee = adminService.deleteEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}