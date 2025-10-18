package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.PaymentConfirmationDTO;
import com.structurax.root.structurax.root.dto.PaymentDTO;
import com.structurax.root.structurax.root.service.ProjectOwnerPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-owner/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectOwnerPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerPaymentController.class);
    private final ProjectOwnerPaymentService paymentService;

    public ProjectOwnerPaymentController(ProjectOwnerPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ========== PAYMENT ENDPOINTS ==========

    /**
     * Get all payments for a specific project
     * GET /api/project-owner/payments/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getPaymentsByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payments for project_id={}", projectId);
            List<PaymentDTO> payments = paymentService.getPaymentsByProjectId(projectId);
            response.put("success", true);
            response.put("payments", payments);
            response.put("totalCount", payments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payments: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get a single payment by payment ID
     * GET /api/project-owner/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable Integer paymentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payment by payment_id={}", paymentId);
            PaymentDTO payment = paymentService.getPaymentById(paymentId);
            response.put("success", true);
            response.put("payment", payment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error fetching payment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Error fetching payment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all payments by invoice ID
     * GET /api/project-owner/payments/invoice/{invoiceId}
     */
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<Map<String, Object>> getPaymentsByInvoice(@PathVariable Integer invoiceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payments for invoice_id={}", invoiceId);
            List<PaymentDTO> payments = paymentService.getPaymentsByInvoiceId(invoiceId);
            response.put("success", true);
            response.put("payments", payments);
            response.put("totalCount", payments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payments: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all pending payments for a project
     * GET /api/project-owner/payments/project/{projectId}/pending
     */
    @GetMapping("/project/{projectId}/pending")
    public ResponseEntity<Map<String, Object>> getPendingPayments(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching pending payments for project_id={}", projectId);
            List<PaymentDTO> payments = paymentService.getPendingPayments(projectId);
            response.put("success", true);
            response.put("payments", payments);
            response.put("totalCount", payments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching pending payments: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching pending payments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all paid payments for a project
     * GET /api/project-owner/payments/project/{projectId}/paid
     */
    @GetMapping("/project/{projectId}/paid")
    public ResponseEntity<Map<String, Object>> getPaidPayments(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching paid payments for project_id={}", projectId);
            List<PaymentDTO> payments = paymentService.getPaidPayments(projectId);
            response.put("success", true);
            response.put("payments", payments);
            response.put("totalCount", payments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching paid payments: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching paid payments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new payment
     * POST /api/project-owner/payments
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody PaymentDTO paymentDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Creating payment for invoice_id={}", paymentDTO.getInvoiceId());
            PaymentDTO created = paymentService.createPayment(paymentDTO);
            response.put("success", true);
            response.put("payment", created);
            response.put("message", "Payment created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Error creating payment: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update payment status
     * PUT /api/project-owner/payments/{paymentId}/status?status=Paid
     */
    @PutMapping("/{paymentId}/status")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable Integer paymentId,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating payment status for payment_id={} to {}", paymentId, status);
            paymentService.updatePaymentStatus(paymentId, status);
            response.put("success", true);
            response.put("message", "Payment status updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Error updating payment status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating payment status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== PAYMENT CONFIRMATION ENDPOINTS ==========

    /**
     * Get all payment confirmations for a specific project
     * GET /api/project-owner/payments/confirmations/project/{projectId}
     */
    @GetMapping("/confirmations/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getPaymentConfirmationsByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payment confirmations for project_id={}", projectId);
            List<PaymentConfirmationDTO> confirmations = paymentService.getPaymentConfirmationsByProjectId(projectId);
            response.put("success", true);
            response.put("confirmations", confirmations);
            response.put("totalCount", confirmations.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payment confirmations: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payment confirmations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get a single payment confirmation by confirmation ID
     * GET /api/project-owner/payments/confirmations/{confirmationId}
     */
    @GetMapping("/confirmations/{confirmationId}")
    public ResponseEntity<Map<String, Object>> getPaymentConfirmationById(@PathVariable Integer confirmationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payment confirmation by confirmation_id={}", confirmationId);
            PaymentConfirmationDTO confirmation = paymentService.getPaymentConfirmationById(confirmationId);
            response.put("success", true);
            response.put("confirmation", confirmation);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error fetching payment confirmation: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Error fetching payment confirmation: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payment confirmation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all payment confirmations by payment ID
     * GET /api/project-owner/payments/{paymentId}/confirmations
     */
    @GetMapping("/{paymentId}/confirmations")
    public ResponseEntity<Map<String, Object>> getPaymentConfirmationsByPayment(@PathVariable Integer paymentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching payment confirmations for payment_id={}", paymentId);
            List<PaymentConfirmationDTO> confirmations = paymentService.getPaymentConfirmationsByPaymentId(paymentId);
            response.put("success", true);
            response.put("confirmations", confirmations);
            response.put("totalCount", confirmations.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching payment confirmations: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching payment confirmations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all pending payment confirmations for a project
     * GET /api/project-owner/payments/confirmations/project/{projectId}/pending
     */
    @GetMapping("/confirmations/project/{projectId}/pending")
    public ResponseEntity<Map<String, Object>> getPendingConfirmations(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching pending payment confirmations for project_id={}", projectId);
            List<PaymentConfirmationDTO> confirmations = paymentService.getPendingConfirmations(projectId);
            response.put("success", true);
            response.put("confirmations", confirmations);
            response.put("totalCount", confirmations.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching pending payment confirmations: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching pending payment confirmations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new payment confirmation
     * POST /api/project-owner/payments/confirmations
     */
    @PostMapping("/confirmations")
    public ResponseEntity<Map<String, Object>> createPaymentConfirmation(@RequestBody PaymentConfirmationDTO confirmationDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Creating payment confirmation for payment_id={}", confirmationDTO.getPaymentId());
            PaymentConfirmationDTO created = paymentService.createPaymentConfirmation(confirmationDTO);
            response.put("success", true);
            response.put("confirmation", created);
            response.put("message", "Payment confirmation created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Error creating payment confirmation: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating payment confirmation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update payment confirmation status
     * PUT /api/project-owner/payments/confirmations/{confirmationId}/status?status=Approved
     */
    @PutMapping("/confirmations/{confirmationId}/status")
    public ResponseEntity<Map<String, Object>> updatePaymentConfirmationStatus(
            @PathVariable Integer confirmationId,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating payment confirmation status for confirmation_id={} to {}", confirmationId, status);
            paymentService.updatePaymentConfirmationStatus(confirmationId, status);
            response.put("success", true);
            response.put("message", "Payment confirmation status updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Error updating payment confirmation status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating payment confirmation status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
