package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.Constants.Constants;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Validated
@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping(value = "/add_employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEmployee(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone_number") String phoneNumber,
            @RequestParam("address") String address,
            @RequestParam("type") String type,
            @RequestParam("joined_date") String joinedDate,
            @RequestParam("password") String password,
            @RequestParam("availability") Boolean availability,
            @RequestParam(value = "profile_image", required = false) MultipartFile profileImage
    ) {
        try {
            // Save image
            String imageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) uploadPath.mkdirs();

                File dest = new File(uploadDir + fileName);
                profileImage.transferTo(dest);

                imageUrl = "/uploads/" + fileName;

            }

            // Convert to DTO
            EmployeeDTO employeeDTO = new EmployeeDTO();
            employeeDTO.setName(name);
            employeeDTO.setEmail(email);
            employeeDTO.setPhoneNumber(phoneNumber);
            employeeDTO.setAddress(address);
            employeeDTO.setType(type);
            employeeDTO.setJoinedDate(LocalDate.parse(joinedDate));
            employeeDTO.setPassword(password);
            employeeDTO.setAvailability(availability);
            employeeDTO.setProfileImageUrl(imageUrl);

            return ResponseEntity.ok(adminService.createEmployee(employeeDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PostMapping(consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
//    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
//        try {
//            final EmployeeDTO createdEmployee = adminService.createEmployee(employeeDTO);
//            return ResponseEntity.ok(createdEmployee);
//        } catch (Exception e) {
//            return new ResponseEntity<>("Error creating employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

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