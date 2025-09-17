package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationSupplierDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.service.SupplierQuotationService;
import com.structurax.root.structurax.root.service.QuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/supplier/quotations")
@CrossOrigin(origins = "*")
public class SupplierQuotationController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierQuotationController.class);

    @Autowired
    private SupplierQuotationService quotationService;

    @Autowired
    private QuotationService quotationDetailsService;

    @GetMapping("/requests")
    public ResponseEntity<?> getAllQuotationRequests() {
        try {
            logger.info("Getting all quotation requests");
            List<QuotationDTO> quotations = quotationService.getAllQuotationRequests();
            return ResponseEntity.ok(quotations);
        } catch (Exception e) {
            logger.error("Error fetching quotation requests: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation requests: " + e.getMessage()));
        }
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<?> getQuotationsBySupplierId(@PathVariable Integer supplierId) {
        try {
            logger.info("Getting quotations for supplier ID: {}", supplierId);
            List<QuotationDTO> quotations = quotationService.getQuotationsBySupplierId(supplierId);
            return ResponseEntity.ok(quotations);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching quotations for supplier {}: {}", supplierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotations: " + e.getMessage()));
        }
    }

    @GetMapping("/details/{quotationId}")
    public ResponseEntity<?> getQuotationById(@PathVariable Integer quotationId) {
        try {
            logger.info("Getting quotation details for ID: {}", quotationId);
            QuotationDTO quotation = quotationService.getQuotationById(quotationId);
            return ResponseEntity.ok(quotation);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error fetching quotation {}: {}", quotationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation: " + e.getMessage()));
        }
    }

    /**
     * Get detailed quotation information including items and suppliers
     */
    @GetMapping("/details/{quotationId}/complete")
    public ResponseEntity<?> getCompleteQuotationDetails(@PathVariable Integer quotationId) {
        try {
            logger.info("Getting complete quotation details for ID: {}", quotationId);

            // Get quotation basic info
            QuotationDTO quotation = quotationService.getQuotationById(quotationId);
            if (quotation == null) {
                return ResponseEntity.notFound().build();
            }

            // Get quotation items
            List<QuotationItemDTO> items = quotationDetailsService.getQuotationItemsByQuotationId(quotationId);

            // Get quotation suppliers
            List<QuotationSupplierDTO> suppliers = quotationDetailsService.getQuotationSuppliersByQuotationId(quotationId);

            // Build complete response
            Map<String, Object> response = new HashMap<>();
            response.put("quotation", quotation);
            response.put("items", items);
            response.put("suppliers", suppliers);

            logger.info("Successfully retrieved complete quotation details for ID: {}", quotationId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching complete quotation details for {}: {}", quotationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch complete quotation details: " + e.getMessage()));
        }
    }

    /**
     * Get quotation items for a specific quotation with enhanced details
     */
    @GetMapping("/{quotationId}/items")
    public ResponseEntity<?> getQuotationItems(@PathVariable Integer quotationId) {
        try {
            // Validate quotation ID
            if (quotationId == null || quotationId <= 0) {
                logger.warn("Invalid quotation ID provided: {}", quotationId);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid quotation ID is required"));
            }

            logger.info("Getting enhanced items for quotation ID: {}", quotationId);

            // Use enhanced method to get complete item details from database
            List<QuotationItemDTO> items = quotationDetailsService.getEnhancedQuotationItemsByQuotationId(quotationId);

            // Transform items to include formatted information for frontend
            List<Map<String, Object>> enhancedItems = items.stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemId", item.getItemId());
                itemMap.put("qId", item.getQId());
                itemMap.put("name", item.getName());
                itemMap.put("description", item.getDescription());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("amount", item.getAmount());
                itemMap.put("unit", item.getUnit());
                itemMap.put("category", item.getCategory());
                itemMap.put("specifications", item.getSpecifications());
                itemMap.put("brand", item.getBrand());
                itemMap.put("model", item.getModel());
                itemMap.put("unitPrice", item.getUnitPrice());
                itemMap.put("totalPrice", item.getTotalPrice());
                itemMap.put("itemCode", item.getItemCode());
                itemMap.put("priority", item.getPriority());
                itemMap.put("requiredDate", item.getRequiredDate());
                itemMap.put("notes", item.getNotes());
                itemMap.put("status", item.getStatus());
                itemMap.put("createdDate", item.getCreatedDate());
                itemMap.put("updatedDate", item.getUpdatedDate());

                // Add formatted strings for display
                itemMap.put("formattedPrice", item.getFormattedPrice());
                itemMap.put("itemSummary", item.getItemSummary());

                return itemMap;
            }).collect(Collectors.toList());

            logger.info("Successfully retrieved {} enhanced items for quotation ID: {}", enhancedItems.size(), quotationId);

            return ResponseEntity.ok(Map.of(
                "quotationId", quotationId,
                "itemsCount", enhancedItems.size(),
                "items", enhancedItems,
                "timestamp", new Date()
            ));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid quotation ID format: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid quotation ID format: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching quotation items for {}: {}", quotationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation items: " + e.getMessage()));
        }
    }

    /**
     * Get basic quotation items for backward compatibility
     */
    @GetMapping("/{quotationId}/items/basic")
    public ResponseEntity<?> getBasicQuotationItems(@PathVariable Integer quotationId) {
        try {
            // Validate quotation ID
            if (quotationId == null || quotationId <= 0) {
                logger.warn("Invalid quotation ID provided: {}", quotationId);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid quotation ID is required"));
            }

            logger.info("Getting basic items for quotation ID: {}", quotationId);
            List<QuotationItemDTO> items = quotationDetailsService.getQuotationItemsByQuotationId(quotationId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            logger.error("Error fetching basic quotation items for {}: {}", quotationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation items: " + e.getMessage()));
        }
    }

    /**
     * Get suppliers for a specific quotation
     */
    @GetMapping("/{quotationId}/suppliers")
    public ResponseEntity<?> getQuotationSuppliers(@PathVariable Integer quotationId) {
        try {
            logger.info("Getting suppliers for quotation ID: {}", quotationId);
            List<QuotationSupplierDTO> suppliers = quotationDetailsService.getQuotationSuppliersByQuotationId(quotationId);
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            logger.error("Error fetching quotation suppliers for {}: {}", quotationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation suppliers: " + e.getMessage()));
        }
    }

    @PostMapping("/respond")
    public ResponseEntity<?> respondToQuotation(@RequestBody QuotationResponseDTO responseDTO) {
        try {
            logger.info("Creating response for quotation ID: {} by supplier ID: {}",
                       responseDTO.getQId(), responseDTO.getSupplierId());

            // Validate the response data
            if (responseDTO.getQId() == null || responseDTO.getQId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid quotation ID is required"));
            }

            if (responseDTO.getSupplierId() == null || responseDTO.getSupplierId() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid supplier ID is required"));
            }

            if (responseDTO.getTotalAmount() == null || responseDTO.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid total amount is required"));
            }

            // Set default values if not provided
            if (responseDTO.getStatus() == null || responseDTO.getStatus().trim().isEmpty()) {
                responseDTO.setStatus("SUBMITTED");
            }

            if (responseDTO.getRespondDate() == null) {
                responseDTO.setRespondDate(new java.sql.Date(System.currentTimeMillis()));
            }

            QuotationResponseDTO response = quotationService.respondToQuotation(responseDTO);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Quotation response submitted successfully");
            result.put("response", response);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating quotation response: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create quotation response: " + e.getMessage()));
        }
    }

    /**
     * Check if supplier can respond to a quotation
     */
    @GetMapping("/can-respond/{quotationId}/{supplierId}")
    public ResponseEntity<?> canSupplierRespond(@PathVariable Integer quotationId, @PathVariable Integer supplierId) {
        try {
            logger.info("Checking if supplier {} can respond to quotation {}", supplierId, quotationId);

            // Check if quotation exists
            QuotationDTO quotation = quotationService.getQuotationById(quotationId);
            if (quotation == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("canRespond", false, "reason", "Quotation not found"));
            }

            // Check if supplier is assigned to this quotation
            List<QuotationSupplierDTO> suppliers = quotationDetailsService.getQuotationSuppliersByQuotationId(quotationId);
            boolean isAssigned = suppliers.stream()
                    .anyMatch(s -> s.getSupplierId().equals(supplierId));

            if (!isAssigned) {
                return ResponseEntity.ok(Map.of("canRespond", false, "reason", "Supplier not assigned to this quotation"));
            }

            // Check if supplier has already responded
            QuotationResponseDTO existingResponse = quotationService.getQuotationResponse(quotationId);
            if (existingResponse != null && existingResponse.getSupplierId().equals(supplierId)) {
                return ResponseEntity.ok(Map.of("canRespond", false, "reason", "Already responded to this quotation", "existingResponse", existingResponse));
            }

            // Check quotation status and deadline
            if ("CLOSED".equalsIgnoreCase(quotation.getStatus()) || "CANCELLED".equalsIgnoreCase(quotation.getStatus())) {
                return ResponseEntity.ok(Map.of("canRespond", false, "reason", "Quotation is " + quotation.getStatus()));
            }

            // Check if deadline has passed
            if (quotation.getDeadline() != null && quotation.getDeadline().before(new java.sql.Date(System.currentTimeMillis()))) {
                return ResponseEntity.ok(Map.of("canRespond", false, "reason", "Quotation deadline has passed"));
            }

            return ResponseEntity.ok(Map.of("canRespond", true, "quotation", quotation));

        } catch (Exception e) {
            logger.error("Error checking response eligibility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check response eligibility: " + e.getMessage()));
        }
    }

    @GetMapping("/response/{quotationId}")
    public ResponseEntity<?> getQuotationResponse(@PathVariable Integer quotationId) {
        try {
            logger.info("Getting response for quotation ID: {}", quotationId);
            QuotationResponseDTO response = quotationService.getQuotationResponse(quotationId);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching quotation response for quotation {}: {}", quotationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation response: " + e.getMessage()));
        }
    }

    @PutMapping("/response")
    public ResponseEntity<?> updateQuotationResponse(@RequestBody QuotationResponseDTO responseDTO) {
        try {
            logger.info("Updating quotation response with ID: {}", responseDTO.getResponseId());
            QuotationResponseDTO updatedResponse = quotationService.updateQuotationResponse(responseDTO);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Quotation response updated successfully");
            result.put("response", updatedResponse);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("Failed to update")) {
                return ResponseEntity.notFound().build();
            }
            logger.error("Error updating quotation response: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update quotation response: " + e.getMessage()));
        }
    }

    @GetMapping("/responses/supplier/{supplierId}")
    public ResponseEntity<?> getQuotationResponsesBySupplierId(@PathVariable Integer supplierId) {
        try {
            logger.info("Getting quotation responses for supplier ID: {}", supplierId);
            List<QuotationResponseDTO> responses = quotationService.getQuotationResponsesBySupplierId(supplierId);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching quotation responses for supplier {}: {}", supplierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation responses: " + e.getMessage()));
        }
    }

    /**
     * Get quotation responses with detailed quotation information
     */
    @GetMapping("/responses/supplier/{supplierId}/detailed")
    public ResponseEntity<?> getDetailedQuotationResponsesBySupplierId(@PathVariable Integer supplierId) {
        try {
            logger.info("Getting detailed quotation responses for supplier ID: {}", supplierId);

            List<QuotationResponseDTO> responses = quotationService.getQuotationResponsesBySupplierId(supplierId);
            List<Map<String, Object>> detailedResponses = new java.util.ArrayList<>();

            for (QuotationResponseDTO response : responses) {
                Map<String, Object> detailedResponse = new HashMap<>();
                detailedResponse.put("response", response);

                // Get quotation details
                QuotationDTO quotation = quotationService.getQuotationById(response.getQId());
                detailedResponse.put("quotation", quotation);

                // Get quotation items
                List<QuotationItemDTO> items = quotationDetailsService.getQuotationItemsByQuotationId(response.getQId());
                detailedResponse.put("items", items);

                detailedResponses.add(detailedResponse);
            }

            return ResponseEntity.ok(detailedResponses);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching detailed quotation responses for supplier {}: {}", supplierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch detailed quotation responses: " + e.getMessage()));
        }
    }

    /**
     * Get quotation status for suppliers dashboard
     */
    @GetMapping("/status/supplier/{supplierId}")
    public ResponseEntity<?> getQuotationStatusForSupplier(@PathVariable Integer supplierId) {
        try {
            logger.info("Getting quotation status summary for supplier ID: {}", supplierId);

            // Get all quotations for supplier
            List<QuotationDTO> quotations = quotationService.getQuotationsBySupplierId(supplierId);

            // Get all responses by supplier
            List<QuotationResponseDTO> responses = quotationService.getQuotationResponsesBySupplierId(supplierId);

            // Calculate statistics
            Map<String, Object> statusSummary = new HashMap<>();
            statusSummary.put("totalQuotations", quotations.size());
            statusSummary.put("totalResponses", responses.size());
            statusSummary.put("pendingResponses", quotations.size() - responses.size());

            // Group by status
            Map<String, Long> quotationsByStatus = quotations.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            q -> q.getStatus() != null ? q.getStatus() : "UNKNOWN",
                            java.util.stream.Collectors.counting()));

            Map<String, Long> responsesByStatus = responses.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            r -> r.getStatus() != null ? r.getStatus() : "UNKNOWN",
                            java.util.stream.Collectors.counting()));

            statusSummary.put("quotationsByStatus", quotationsByStatus);
            statusSummary.put("responsesByStatus", responsesByStatus);
            statusSummary.put("quotations", quotations);
            statusSummary.put("responses", responses);

            return ResponseEntity.ok(statusSummary);

        } catch (Exception e) {
            logger.error("Error fetching quotation status for supplier {}: {}", supplierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation status: " + e.getMessage()));
        }
    }
}
