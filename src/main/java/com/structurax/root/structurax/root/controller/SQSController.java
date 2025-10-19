package com.structurax.root.structurax.root.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.BOQWithProjectDTO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.dto.UserDTO;
import com.structurax.root.structurax.root.service.AdminService;
import com.structurax.root.structurax.root.service.MailService;
import com.structurax.root.structurax.root.service.QuotationService;
import com.structurax.root.structurax.root.service.SQSService;
import com.structurax.root.structurax.root.service.SupplierService;

import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(value = "/sqs")
public class SQSController {

    @Autowired
    private SQSService sqsService;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AdminService adminService;

    @PutMapping(value = "/assign_qs/{pid}/{eid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assign_qs(@PathVariable @Pattern(regexp = "^EMP_\\d{3}$") String eid, @PathVariable @Pattern(regexp = "^PRJ_\\d{3}$") String pid) {
        try {
            Project1DTO project = sqsService.getProjectById(pid);
            if (project == null) {
                return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
            }

            boolean updated = sqsService.assignQsToProject(pid, eid);
            if (!updated) {
                return new ResponseEntity<>("Failed to assign QS to project", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok("QS assigned to project successfully.");
        } catch (Exception e) {
            return new ResponseEntity<>("Error assigning QS: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Endpoint to get projects where qs_id is null or empty, returning project name, category, and client_id.
     */
    @RequestMapping(value = "/projects_without_qs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProjectsWithoutQs() {
        try {
            java.util.List<com.structurax.root.structurax.root.dao.Impl.SQSDAOImpl.ProjectInfo> projects = sqsService.getProjectsWithoutQs();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching projects: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Endpoint to get all QS Officers from the employee table.
     */
    @RequestMapping(value = "/get_qs_officers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQSOfficers() {
        try {
            java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> officers = sqsService.getQSOfficers();
            return ResponseEntity.ok(officers);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching QS Officers: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to get all requests in the system.
     */
    @GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllRequests() {
        try {
            List<com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO> requests = sqsService.getAllRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching requests: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // BOQ Management Endpoints for SQS

    /**
     * Get all BOQs in the system with their items
     */
    @GetMapping(value = "/boqs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllBOQs() {
        try {
            List<BOQWithProjectDTO> boqs = sqsService.getAllBOQsWithProjectInfo();
            return ResponseEntity.ok(boqs);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching BOQs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a specific BOQ by ID with its items
     */
    @GetMapping(value = "/boqs/{boqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBOQById(@PathVariable String boqId) {
        try {
            BOQWithItemsDTO boq = sqsService.getBOQWithItemsById(boqId);
            if (boq != null) {
                return ResponseEntity.ok(boq);
            } else {
                return new ResponseEntity<>("BOQ not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching BOQ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update BOQ with items (SQS can edit any BOQ)
     */
    @PutMapping(value = "/boqs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBOQWithItems(@RequestBody BOQWithItemsDTO boqWithItems) {
        try {
            boolean updated = sqsService.updateBOQWithItems(boqWithItems);
            if (updated) {
                return ResponseEntity.ok("BOQ updated successfully");
            } else {
                return new ResponseEntity<>("Failed to update BOQ", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating BOQ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update BOQ status (approve/reject)
     */
    @PutMapping(value = "/boqs/{boqId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBOQStatus(@PathVariable String boqId, @RequestParam String status) {
        try {
            BOQDTO.Status boqStatus;
            try {
                boqStatus = BOQDTO.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid status. Valid values are: DRAFT, APPROVED, FINAL", HttpStatus.BAD_REQUEST);
            }
            
            boolean updated = sqsService.updateBOQStatus(boqId, boqStatus);
            if (updated) {
                return ResponseEntity.ok("BOQ status updated successfully to " + status.toUpperCase());
            } else {
                return new ResponseEntity<>("Failed to update BOQ status", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating BOQ status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ============ QUOTATION MANAGEMENT ENDPOINTS ============

    /**
     * Create a new quotation with items and automatic email sending to suppliers
     */
    @org.springframework.web.bind.annotation.PostMapping(value = "/quotations/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createQuotation(@RequestBody java.util.Map<String, Object> request) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            // Extract quotation data
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> quotationData = (java.util.Map<String, Object>) request.get("quotation");

            if (quotationData == null) {
                response.put("success", false);
                response.put("message", "Quotation data is missing");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            QuotationDTO quotation = new QuotationDTO();
            quotation.setProjectId((String) quotationData.get("projectId"));
            quotation.setQsId((String) quotationData.get("qsId"));

            // Parse date
            try {
                String dateStr = (String) quotationData.get("date");
                quotation.setDate(java.sql.Date.valueOf(java.time.LocalDate.parse(dateStr)));
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Invalid date format. Use YYYY-MM-DD format.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Parse deadline
            try {
                String deadlineStr = (String) quotationData.get("deadline");
                quotation.setDeadline(java.sql.Date.valueOf(java.time.LocalDate.parse(deadlineStr)));
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Invalid deadline format. Use YYYY-MM-DD format.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            quotation.setStatus((String) quotationData.get("status"));
            quotation.setDescription((String) quotationData.get("description"));

            // Extract items data
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> itemsData = (java.util.List<java.util.Map<String, Object>>) request.get("items");

            java.util.List<QuotationItemDTO> items = null;

            if (itemsData != null && !itemsData.isEmpty()) {
                items = new java.util.ArrayList<>();
                for (java.util.Map<String, Object> itemData : itemsData) {
                    QuotationItemDTO item = new QuotationItemDTO();
                    item.setName((String) itemData.get("name"));
                    item.setDescription((String) itemData.get("description"));

                    // Parse amount
                    try {
                        Object amountObj = itemData.get("amount");
                        item.setAmount(new java.math.BigDecimal(amountObj.toString()));
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("message", "Invalid amount format for item: " + itemData.get("name"));
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    // Parse quantity
                    try {
                        Object quantityObj = itemData.get("quantity");
                        item.setQuantity(Integer.valueOf(quantityObj.toString()));
                    } catch (Exception e) {
                        response.put("success", false);
                        response.put("message", "Invalid quantity format for item: " + itemData.get("name"));
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    items.add(item);
                }
            }

            // Extract suppliers data
            java.util.List<Integer> supplierIds = null;
            if (request.containsKey("supplierIds")) {
                @SuppressWarnings("unchecked")
                java.util.List<Object> supplierData = (java.util.List<Object>) request.get("supplierIds");

                if (supplierData != null && !supplierData.isEmpty()) {
                    supplierIds = new java.util.ArrayList<>();
                    for (Object supplierId : supplierData) {
                        try {
                            supplierIds.add(Integer.valueOf(supplierId.toString()));
                        } catch (Exception e) {
                            response.put("success", false);
                            response.put("message", "Invalid supplier ID format");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }
                    }
                }
            }

            // Create quotation
            Integer qId;
            if (supplierIds != null && !supplierIds.isEmpty()) {
                qId = quotationService.createQuotation(quotation, items, supplierIds);
            } else {
                qId = quotationService.createQuotation(quotation, items);
            }

            if (qId != null) {
                response.put("success", true);
                response.put("message", "Quotation created successfully");
                response.put("qId", qId);
                
                // Send emails to suppliers if status is 'sent' or 'pending' (auto-send for both)
                if (supplierIds != null && !supplierIds.isEmpty()) {
                    int emailCount = 0;
                    java.util.List<java.util.Map<String, Object>> emailResults = new java.util.ArrayList<>();
                    
                    try {
                        Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
                        String projectName = project != null ? project.getName() : "Project #" + quotation.getProjectId();
                        
                        UserDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
                        String qsName = qsEmployee != null ? qsEmployee.getName() : "QS";
                        String qsEmail = qsEmployee != null ? qsEmployee.getEmail() : "noreply@structurax.com";
                        
                        for (Integer supplierId : supplierIds) {
                            try {
                                SupplierDTO supplier = supplierService.getSupplierById(supplierId);
                                
                                if (supplier != null && supplier.getEmail() != null && !supplier.getEmail().isEmpty()) {
                                    mailService.sendQuotationRequest(
                                        supplier.getEmail(),
                                        supplier.getSupplier_name(),
                                        qId,
                                        projectName,
                                        qsName,
                                        qsEmail,
                                        quotation.getDeadline() != null ? quotation.getDeadline().toString() : "Not specified"
                                    );
                                    
                                    emailCount++;
                                    java.util.Map<String, Object> emailResult = new java.util.HashMap<>();
                                    emailResult.put("supplierId", supplierId);
                                    emailResult.put("supplierName", supplier.getSupplier_name());
                                    emailResult.put("email", supplier.getEmail());
                                    emailResult.put("status", "sent");
                                    emailResults.add(emailResult);
                                } else {
                                    java.util.Map<String, Object> emailResult = new java.util.HashMap<>();
                                    emailResult.put("supplierId", supplierId);
                                    emailResult.put("status", "failed");
                                    emailResult.put("reason", supplier == null ? "Supplier not found" : "Email not found");
                                    emailResults.add(emailResult);
                                }
                            } catch (Exception e) {
                                java.util.Map<String, Object> emailResult = new java.util.HashMap<>();
                                emailResult.put("supplierId", supplierId);
                                emailResult.put("status", "failed");
                                emailResult.put("reason", e.getMessage());
                                emailResults.add(emailResult);
                            }
                        }
                        
                        // Update status to 'sent' after emails are sent
                        if (emailCount > 0 && "pending".equalsIgnoreCase(quotation.getStatus())) {
                            quotationService.updateQuotationStatus(qId, "sent");
                        }
                        
                        response.put("emailsSent", emailCount);
                        response.put("totalSuppliers", supplierIds.size());
                        response.put("emailResults", emailResults);
                        
                        if (emailCount > 0) {
                            response.put("message", "Quotation created and " + emailCount + " email(s) sent successfully");
                        } else {
                            response.put("warning", "Quotation created but no emails were sent");
                        }
                    } catch (Exception e) {
                        response.put("warning", "Quotation created but email sending failed: " + e.getMessage());
                    }
                }
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to create quotation");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
