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

import java.util.ArrayList;
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

            // Map qId to id for frontend compatibility and include requested items
            List<Map<String, Object>> response = quotations.stream().map(q -> {
                Map<String, Object> quotationMap = new HashMap<>();
                quotationMap.put("id", q.getQId()); // Map qId to id
                quotationMap.put("qId", q.getQId());
                quotationMap.put("projectName", q.getProjectName());

                // Fetch requested items for this quotation
                List<QuotationItemDTO> items = quotationDetailsService.getQuotationItemsByQuotationId(q.getQId());
                quotationMap.put("requestedItems", items != null ? items : new ArrayList<>());
                quotationMap.put("itemsCount", items != null ? items.size() : 0);

                quotationMap.put("date", q.getDate());
                quotationMap.put("status", q.getStatus());
                quotationMap.put("deadline", q.getDeadline());
                quotationMap.put("description", q.getDescription());
                quotationMap.put("projectId", q.getProjectId());
                quotationMap.put("createdDate", q.getCreatedAt());
                quotationMap.put("updatedDate", q.getUpdatedAt());
                return quotationMap;
            }).collect(Collectors.toList());

            logger.info("Successfully retrieved {} quotations with items for supplier {}", response.size(), supplierId);
            return ResponseEntity.ok(response);
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
            // Enhanced validation with detailed logging
            logger.info("Received request for quotation items with quotationId: {}", quotationId);

            if (quotationId == null) {
                logger.warn("Quotation ID is null");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Quotation ID cannot be null", "receivedValue", "null"));
            }

            if (quotationId <= 0) {
                logger.warn("Invalid quotation ID provided: {}", quotationId);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Valid quotation ID is required (must be > 0)", "receivedValue", quotationId));
            }

            logger.info("Getting enhanced items for quotation ID: {}", quotationId);

            // First check if quotation exists
            QuotationDTO quotation = quotationService.getQuotationById(quotationId);
            if (quotation == null) {
                logger.warn("Quotation not found for ID: {}", quotationId);
                return ResponseEntity.notFound()
                        .build();
            }

            // Use enhanced method to get complete item details from database
            List<QuotationItemDTO> items = quotationDetailsService.getEnhancedQuotationItemsByQuotationId(quotationId);

            if (items == null) {
                logger.warn("Items list is null for quotation ID: {}", quotationId);
                items = new ArrayList<>();
            }

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

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("quotationId", quotationId);
            response.put("quotation", quotation);
            response.put("itemsCount", enhancedItems.size());
            response.put("items", enhancedItems);
            response.put("timestamp", new Date());

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            logger.error("Invalid quotation ID format: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid quotation ID format - must be a number", "receivedValue", quotationId));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid quotation ID argument: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid quotation ID: " + e.getMessage(), "receivedValue", quotationId));
        } catch (Exception e) {
            logger.error("Error fetching quotation items for {}: {}", quotationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotation items: " + e.getMessage(), "quotationId", quotationId));
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
            // Log the entire received DTO
            logger.info("Received QuotationResponseDTO: {}", responseDTO);
            logger.info("qId: {}, supplierId: {}, totalAmount: {}, deliveryTime: {}, status: {}",
                    responseDTO.getQId(), responseDTO.getSupplierId(), responseDTO.getTotalAmount(),
                    responseDTO.getDeliveryTime(), responseDTO.getStatus());

            // Enhanced validation with detailed error messages
            if (responseDTO.getQId() == null || responseDTO.getQId() <= 0) {
                String errorMsg = String.format("Valid quotation ID is required. Received qId: %s",
                        responseDTO.getQId());
                logger.error(errorMsg);
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", errorMsg,
                                "receivedDTO", responseDTO.toString(),
                                "qId", responseDTO.getQId() != null ? responseDTO.getQId() : "null"
                        ));
            }

            if (responseDTO.getSupplierId() == null || responseDTO.getSupplierId() <= 0) {
                String errorMsg = String.format("Valid supplier ID is required. Received supplierId: %s",
                        responseDTO.getSupplierId());
                logger.error(errorMsg);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", errorMsg));
            }

            if (responseDTO.getTotalAmount() == null ||
                    responseDTO.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                String errorMsg = String.format("Valid total amount is required. Received totalAmount: %s",
                        responseDTO.getTotalAmount());
                logger.error(errorMsg);
                return ResponseEntity.badRequest()
                        .body(Map.of("error", errorMsg));
            }

            // Normalize status to match database ENUM values (lowercase: pending, accepted, rejected)
            String status = responseDTO.getStatus();
            if (status == null || status.trim().isEmpty()) {
                status = "pending"; // Default status
            } else {
                // Normalize to lowercase and map to valid ENUM values
                status = status.trim().toLowerCase();
                switch (status) {
                    case "submitted":
                    case "submit":
                    case "new":
                        status = "pending";
                        break;
                    case "accepted":
                    case "approved":
                    case "accept":
                        status = "accepted";
                        break;
                    case "rejected":
                    case "declined":
                    case "reject":
                        status = "rejected";
                        break;
                    case "pending":
                        // Already valid
                        break;
                    default:
                        logger.warn("Unknown status '{}', defaulting to 'pending'", status);
                        status = "pending";
                }
            }
            responseDTO.setStatus(status);
            logger.info("Normalized status to: '{}'", status);

            if (responseDTO.getRespondDate() == null) {
                responseDTO.setRespondDate(new java.sql.Date(System.currentTimeMillis()));
            }

            // Calculate delivery date based on delivery time (days)
            if (responseDTO.getDeliveryDate() == null && responseDTO.getDeliveryTime() != null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(new java.util.Date());
                calendar.add(java.util.Calendar.DAY_OF_MONTH, responseDTO.getDeliveryTime());
                responseDTO.setDeliveryDate(new java.sql.Date(calendar.getTimeInMillis()));
                logger.info("Calculated delivery date: {} (current date + {} days)",
                        responseDTO.getDeliveryDate(), responseDTO.getDeliveryTime());
            } else if (responseDTO.getDeliveryDate() == null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(new java.util.Date());
                calendar.add(java.util.Calendar.DAY_OF_MONTH, 30);
                responseDTO.setDeliveryDate(new java.sql.Date(calendar.getTimeInMillis()));
                logger.warn("No delivery date or time provided, defaulting to 30 days from now");
            }

            // Set response date
            if (responseDTO.getResponseDate() == null) {
                responseDTO.setResponseDate(new java.sql.Date(System.currentTimeMillis()));
            }

            // Set validUntil date
            if (responseDTO.getValidUntil() == null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(responseDTO.getResponseDate());
                calendar.add(java.util.Calendar.DAY_OF_MONTH, 30);
                responseDTO.setValidUntil(new java.sql.Date(calendar.getTimeInMillis()));
                logger.info("Set validUntil date to: {}", responseDTO.getValidUntil());
            }

            logger.info("Calling service to save response - status: '{}', delivery date: {}",
                    responseDTO.getStatus(), responseDTO.getDeliveryDate());
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
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.error("Database constraint violation: {}", e.getMessage(), e);
            String errorMsg = "Database validation failed. ";
            if (e.getMessage().contains("status")) {
                errorMsg += "Status must be one of: pending, accepted, rejected";
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", errorMsg,
                            "details", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.error("Error creating quotation response: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to create quotation response: " + e.getMessage(),
                            "exceptionType", e.getClass().getSimpleName()
                    ));
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

            // Get quotation items
            List<QuotationItemDTO> items = quotationDetailsService.getQuotationItemsByQuotationId(quotationId);

            // Build complete response with quotation and items
            Map<String, Object> response = new HashMap<>();
            response.put("canRespond", true);
            response.put("quotation", quotation);
            response.put("items", items);
            response.put("itemsCount", items.size());

            logger.info("Supplier {} can respond to quotation {} with {} items", supplierId, quotationId, items.size());
            return ResponseEntity.ok(response);

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
            List<Map<String, Object>> detailedResponses = new ArrayList<>();

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

    /**
     * Debug endpoint to help identify quotation ID issues from frontend
     */
    @GetMapping("/debug/items/{quotationId}")
    public ResponseEntity<?> debugQuotationItems(@PathVariable String quotationId) {
        try {
            logger.info("Debug endpoint called with quotationId parameter: '{}'", quotationId);

            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("receivedQuotationId", quotationId);
            debugInfo.put("quotationIdType", quotationId != null ? quotationId.getClass().getSimpleName() : "null");
            debugInfo.put("quotationIdLength", quotationId != null ? quotationId.length() : 0);
            debugInfo.put("isNumeric", quotationId != null ? quotationId.matches("\\d+") : false);

            if (quotationId == null || quotationId.trim().isEmpty() || "undefined".equals(quotationId) || "null".equals(quotationId)) {
                debugInfo.put("issue", "Invalid quotation ID received");
                debugInfo.put("suggestion", "Check frontend code - quotation ID is not being passed correctly");
                return ResponseEntity.badRequest().body(debugInfo);
            }

            try {
                Integer numericQuotationId = Integer.parseInt(quotationId);
                debugInfo.put("parsedQuotationId", numericQuotationId);

                // Try to get the quotation
                QuotationDTO quotation = quotationService.getQuotationById(numericQuotationId);
                if (quotation == null) {
                    debugInfo.put("issue", "Quotation not found");
                    debugInfo.put("suggestion", "Quotation with ID " + numericQuotationId + " does not exist");
                    return ResponseEntity.notFound().build();
                }

                // Get items
                List<QuotationItemDTO> items = quotationDetailsService.getQuotationItemsByQuotationId(numericQuotationId);
                debugInfo.put("quotation", quotation);
                debugInfo.put("itemsCount", items.size());
                debugInfo.put("items", items);
                debugInfo.put("status", "success");

                return ResponseEntity.ok(debugInfo);

            } catch (NumberFormatException e) {
                debugInfo.put("issue", "Quotation ID is not a valid number");
                debugInfo.put("suggestion", "Ensure the frontend passes a numeric quotation ID");
                return ResponseEntity.badRequest().body(debugInfo);
            }

        } catch (Exception e) {
            logger.error("Debug endpoint error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Debug endpoint failed: " + e.getMessage()));
        }
    }

    /**
     * Get all quotations with their items for a supplier (useful for debugging and as fallback)
     */
    @GetMapping("/supplier/{supplierId}/with-items")
    public ResponseEntity<?> getQuotationsWithItemsBySupplierId(@PathVariable Integer supplierId) {
        try {
            logger.info("Getting all quotations with items for supplier ID: {}", supplierId);

            List<QuotationDTO> quotations = quotationService.getQuotationsBySupplierId(supplierId);
            List<Map<String, Object>> quotationsWithItems = new ArrayList<>();

            for (QuotationDTO quotation : quotations) {
                Map<String, Object> quotationData = new HashMap<>();
                quotationData.put("quotation", quotation);

                // Get items for each quotation
                List<QuotationItemDTO> items = quotationDetailsService.getQuotationItemsByQuotationId(quotation.getQId());
                quotationData.put("items", items);
                quotationData.put("itemsCount", items.size());

                quotationsWithItems.add(quotationData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("supplierId", supplierId);
            response.put("quotationsCount", quotationsWithItems.size());
            response.put("quotationsWithItems", quotationsWithItems);
            response.put("timestamp", new Date());

            logger.info("Successfully retrieved {} quotations with items for supplier {}", quotationsWithItems.size(), supplierId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching quotations with items for supplier {}: {}", supplierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch quotations with items: " + e.getMessage()));
        }
    }
}