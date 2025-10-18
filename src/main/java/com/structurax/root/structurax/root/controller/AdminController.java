package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.Constants.Constants;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.AdminService;
import com.structurax.root.structurax.root.service.MailService;
import com.structurax.root.structurax.root.util.OtpUtil;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private MailService mailService;



    @PostMapping(value = "/add_employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEmployee(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone_number") String phoneNumber,
            @RequestParam("address") String address,
            @RequestParam("type") String type,
            @RequestParam("joined_date") String joinedDate,
            @RequestParam(value = "availability", required = false) String availability,
            @RequestParam(value = "profile_image", required = false) MultipartFile profileImage
    ) {
        try {
            // Set default availability if not provided
            if (availability == null || availability.trim().isEmpty() ||
                    availability.equals("true") || availability.equals("false")) {
                availability = "Available";
            }

            // Validate availability
            if (!isValidAvailability(availability)) {
                return new ResponseEntity<>("Invalid availability status. Must be one of: Assigned, Available, Deactive",
                        HttpStatus.BAD_REQUEST);
            }

            // Handle image upload
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

            // Generate OTP
            String otp = OtpUtil.generateOtp();

            // Create DTO and set fields (no hashing here)
            UserDTO employeeDTO = new UserDTO();
            employeeDTO.setName(name);
            employeeDTO.setEmail(email);
            employeeDTO.setPhoneNumber(phoneNumber);
            employeeDTO.setAddress(address);
            employeeDTO.setType(type);
            employeeDTO.setJoinedDate(LocalDate.parse(joinedDate));
            employeeDTO.setPassword(otp); // Store plain OTP, hash it later in DAO
            employeeDTO.setProfileImageUrl(imageUrl);

            // Save employee
            UserDTO savedEmployee = adminService.createEmployee(employeeDTO);

            // Send OTP email
            mailService.sendEmployeeOtp(
                    savedEmployee.getEmail(),
                    savedEmployee.getName(),
                    otp
            );

            return ResponseEntity.ok(savedEmployee);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // Helper method to validate availability
    private boolean isValidAvailability(String availability) {
        return availability != null &&
                (availability.equals("Assigned") ||
                        availability.equals("Available") ||
                        availability.equals("Deactive"));
    }

    @GetMapping(value = "get_employees" , produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getAllEmployees() {
        try {
            final List employeeDTOS = adminService.getAllEmployees();
            return ResponseEntity.ok(employeeDTOS);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching employees: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/deactivate/{id}")
    public ResponseEntity<?> deactivateEmployee(@PathVariable @Pattern(regexp = "^EMP_\\d{3}$") String id) {
        try {
            // Get employee using existing method
            UserDTO employee = adminService.getEmployeeById(id);
            if (employee == null) {
                return new ResponseEntity<>("Employee not found with id: " + id, HttpStatus.NOT_FOUND);
            }

            // Check if employee is already deactivated
            if ("Deactive".equals(employee.getAvailability())) {
                return new ResponseEntity<>("Employee is already deactivated", HttpStatus.BAD_REQUEST);
            }

            // Deactivate the employee
            adminService.deactivateEmployee(id);

            // Send removal notification email
            mailService.sendRemovalNotification(employee.getEmail(), employee.getName());

            return ResponseEntity.ok("Employee deactivated successfully and notification email sent.");
        } catch (Exception e) {
            return new ResponseEntity<>("Error deactivating employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getEmployeeById(@PathVariable @Pattern(regexp = "^EMP_\\d{3}$", message = "Employee ID must follow format EMP_XXX") String id) {
        try {
            final UserDTO employee = adminService.getEmployeeById(id);
            if (employee == null) {
                return new ResponseEntity<>("Employee not found with id: " + id, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching employee: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/add_supplier")
    public ResponseEntity<?> addSupplier(@RequestBody UserDTO supplierDTO) {
        try {
            // Generate OTP for the supplier
            String otp = OtpUtil.generateOtp();

            // Set type as Supplier
            supplierDTO.setType("Supplier");
            supplierDTO.setPassword(otp);

            // Save supplier as user
            UserDTO savedSupplier = adminService.addSupplier(supplierDTO);

            // Send OTP email to supplier
            mailService.sendSupplierOtp(
                    savedSupplier.getEmail(),
                    savedSupplier.getName(),
                    otp
            );

            log.info("Supplier created successfully: {} and OTP email sent", savedSupplier.getName());

            return ResponseEntity.ok(savedSupplier);
        } catch (Exception e) {
            log.error("Error adding supplier: {}", e.getMessage());
            return new ResponseEntity<>("Error adding supplier: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/get_all_clients", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getAllClients() {
        try {
            List<FullClientDTO> clients = adminService.getAllClients();
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching clients: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/get_all_projects", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getAllProjects() {
        try {
            List<Project1DTO> projects = adminService.getAllProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching projects: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}