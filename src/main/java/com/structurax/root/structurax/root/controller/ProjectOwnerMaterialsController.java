package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.ProjectOwnerMaterialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-owner/materials")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectOwnerMaterialsController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerMaterialsController.class);
    private final ProjectOwnerMaterialsService materialsService;

    public ProjectOwnerMaterialsController(ProjectOwnerMaterialsService materialsService) {
        this.materialsService = materialsService;
    }



    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> getMaterialsByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching materials for project_id={}", projectId);
            List<ProjectMaterialDTO> materials = materialsService.getMaterialsByProjectId(projectId);
            response.put("success", true);
            response.put("materials", materials);
            response.put("totalCount", materials.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching materials: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching materials: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{projectId}/summary")
    public ResponseEntity<Map<String, Object>> getMaterialSummary(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching material summary for project_id={}", projectId);
            MaterialSummaryDTO summary = materialsService.getMaterialSummary(projectId);
            response.put("success", true);
            response.put("summary", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching material summary: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching material summary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== SITE VISIT ENDPOINTS ==========

    @PostMapping("/site-visits")
    public ResponseEntity<Map<String, Object>> createSiteVisit(@RequestBody SiteVisitDTO siteVisitDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Creating site visit for project_id={}", siteVisitDTO.getProjectId());
            SiteVisitDTO created = materialsService.createSiteVisitRequest(siteVisitDTO);
            response.put("success", true);
            response.put("siteVisit", created);
            response.put("message", "Site visit request submitted successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating site visit: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating site visit: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/site-visits/{projectId}")
    public ResponseEntity<Map<String, Object>> getSiteVisitsByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching site visits for project_id={}", projectId);
            List<SiteVisitDTO> siteVisits = materialsService.getSiteVisits(projectId);
            response.put("success", true);
            response.put("siteVisits", siteVisits);
            response.put("totalCount", siteVisits.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching site visits: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching site visits: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/site-visit/{visitId}")
    public ResponseEntity<Map<String, Object>> getSiteVisitById(@PathVariable Integer visitId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching site visit by visit_id={}", visitId);
            SiteVisitDTO siteVisit = materialsService.getSiteVisitById(visitId);
            if (siteVisit == null) {
                response.put("success", false);
                response.put("message", "Site visit not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("success", true);
            response.put("siteVisit", siteVisit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching site visit: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching site visit: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/site-visit/{visitId}/status")
    public ResponseEntity<Map<String, Object>> updateSiteVisitStatus(
            @PathVariable Integer visitId,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating site visit status for visit_id={} to {}", visitId, status);
            materialsService.updateSiteVisitStatus(visitId, status);
            response.put("success", true);
            response.put("message", "Site visit status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating site visit status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating site visit status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== PAYMENT ENDPOINTS ==========

    /**
     * Get payment summary for a project
     * GET /api/project-owner/materials/payments/{projectId}/summary
     */
    @GetMapping("/payments/{projectId}/summary")
    public ResponseEntity<Map<String, Object>> getPaymentSummary(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payment summary for project_id={}", projectId);
            PaymentSummaryDTO summary = materialsService.getPaymentSummary(projectId);
            response.put("success", true);
            response.put("summary", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payment summary: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payment summary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get payment history for a project
     * GET /api/project-owner/materials/payments/{projectId}/history
     */
    @GetMapping("/payments/{projectId}/history")
    public ResponseEntity<Map<String, Object>> getPaymentHistory(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payment history for project_id={}", projectId);
            List<InstallmentDTO> paymentHistory = materialsService.getPaymentHistory(projectId);
            response.put("success", true);
            response.put("paymentHistory", paymentHistory);
            response.put("totalCount", paymentHistory.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payment history: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payment history: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get upcoming payments for a project
     * GET /api/project-owner/materials/payments/{projectId}/upcoming
     */
    @GetMapping("/payments/{projectId}/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingPayments(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching upcoming payments for project_id={}", projectId);
            List<InstallmentDTO> upcomingPayments = materialsService.getUpcomingPayments(projectId);
            response.put("success", true);
            response.put("upcomingPayments", upcomingPayments);
            response.put("totalCount", upcomingPayments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching upcoming payments: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching upcoming payments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Upload payment receipt
     * POST /api/project-owner/materials/payments/receipt
     */
    @PostMapping("/payments/receipt")
    public ResponseEntity<Map<String, Object>> uploadPaymentReceipt(
            @RequestParam("installmentId") Integer installmentId,
            @RequestParam("projectId") String projectId,
            @RequestParam("phase") String phase,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("paymentDate") String paymentDate,
            @RequestParam("description") String description,
            @RequestParam("receiptFile") MultipartFile receiptFile) {

        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Uploading payment receipt for project_id={}", projectId);

            // Validate file
            if (receiptFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "Receipt file is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Create upload directory if it doesn't exist
            String uploadDir = "uploads/payment-receipts/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save file with timestamp prefix
            String originalFilename = receiptFile.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            String filePath = uploadDir + filename;

            Path path = Paths.get(filePath);
            Files.write(path, receiptFile.getBytes());

            // Create DTO
            PaymentReceiptDTO receiptDTO = new PaymentReceiptDTO();
            receiptDTO.setInstallmentId(installmentId);
            receiptDTO.setProjectId(projectId);
            receiptDTO.setPhase(phase);
            receiptDTO.setAmount(amount);
            receiptDTO.setPaymentDate(LocalDate.parse(paymentDate));
            receiptDTO.setReceiptFilePath(filePath);
            receiptDTO.setDescription(description);
            receiptDTO.setStatus("Pending");
            receiptDTO.setUploadedDate(LocalDate.now());

            PaymentReceiptDTO created = materialsService.uploadPaymentReceipt(receiptDTO);

            response.put("success", true);
            response.put("receipt", created);
            response.put("message", "Payment receipt uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IOException e) {
            logger.error("Error saving receipt file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error saving receipt file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Error uploading payment receipt: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error uploading payment receipt: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get payment receipts for a project
     * GET /api/project-owner/materials/payments/{projectId}/receipts
     */
    @GetMapping("/payments/{projectId}/receipts")
    public ResponseEntity<Map<String, Object>> getPaymentReceipts(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payment receipts for project_id={}", projectId);
            List<PaymentReceiptDTO> receipts = materialsService.getPaymentReceipts(projectId);
            response.put("success", true);
            response.put("receipts", receipts);
            response.put("totalCount", receipts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payment receipts: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payment receipts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}