package com.structurax.root.structurax.root.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;
import com.structurax.root.structurax.root.service.QuotationResponseService;
import com.structurax.root.structurax.root.service.QuotationService;

@RestController
@RequestMapping("/quotation")
@CrossOrigin(origins = "http://localhost:5173")
public class QuotationController {
    
    @Autowired
    private QuotationService quotationService;
    
    @Autowired
    private QuotationResponseService quotationResponseService;

    /**
     * Create a new quotation with items
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createQuotation(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract quotation data
            Map<String, Object> quotationData = (Map<String, Object>) request.get("quotation");
            QuotationDTO quotation = new QuotationDTO();
            quotation.setProjectId((String) quotationData.get("projectId"));
            quotation.setQsId((String) quotationData.get("qsId"));
            quotation.setDate(java.sql.Date.valueOf(java.time.LocalDate.parse((String) quotationData.get("date"))));
            quotation.setDeadline(java.sql.Date.valueOf(java.time.LocalDate.parse((String) quotationData.get("deadline"))));
            quotation.setStatus((String) quotationData.get("status"));
            quotation.setDescription((String) quotationData.get("description"));

            // Extract items data
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
            List<QuotationItemDTO> items = null;
            
            if (itemsData != null && !itemsData.isEmpty()) {
                items = new java.util.ArrayList<>();
                for (Map<String, Object> itemData : itemsData) {
                    QuotationItemDTO item = new QuotationItemDTO();
                    item.setName((String) itemData.get("name"));
                    item.setDescription((String) itemData.get("description"));
                    item.setAmount(new java.math.BigDecimal(itemData.get("amount").toString()));
                    item.setQuantity(Integer.valueOf(itemData.get("quantity").toString()));
                    items.add(item);
                }
            }

            // Extract suppliers data
            List<Integer> supplierIds = null;
            if (request.containsKey("supplierIds")) {
                List<Object> supplierData = (List<Object>) request.get("supplierIds");
                if (supplierData != null && !supplierData.isEmpty()) {
                    supplierIds = new java.util.ArrayList<>();
                    for (Object supplierId : supplierData) {
                        supplierIds.add(Integer.valueOf(supplierId.toString()));
                    }
                }
            }

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
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to create quotation");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all quotations
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllQuotations() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<QuotationDTO> quotations = quotationService.getAllQuotations();
            response.put("success", true);
            response.put("quotations", quotations);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get quotations by QS ID
     */
    @GetMapping("/qs/{qsId}")
    public ResponseEntity<Map<String, Object>> getQuotationsByQsId(@PathVariable String qsId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<QuotationDTO> quotations = quotationService.getQuotationsByQsId(qsId);
            response.put("success", true);
            response.put("quotations", quotations);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotations for QS: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get quotation with items and suppliers
     */
    @GetMapping("/{qId}/with-items")
    public ResponseEntity<Map<String, Object>> getQuotationWithItems(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuotationDTO quotation = quotationService.getQuotationById(qId);
            List<QuotationItemDTO> items = quotationService.getQuotationItemsByQuotationId(qId);
            List<com.structurax.root.structurax.root.dto.QuotationSupplierDTO> suppliers = 
                quotationService.getQuotationSuppliersByQuotationId(qId);
            
            if (quotation != null) {
                response.put("success", true);
                response.put("quotation", quotation);
                response.put("items", items);
                response.put("suppliers", suppliers);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Quotation not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotation with items and suppliers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get suppliers for a specific quotation
     */
    @GetMapping("/{qId}/suppliers")
    public ResponseEntity<Map<String, Object>> getQuotationSuppliers(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<com.structurax.root.structurax.root.dto.QuotationSupplierDTO> suppliers = 
                quotationService.getQuotationSuppliersByQuotationId(qId);
            response.put("success", true);
            response.put("suppliers", suppliers);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotation suppliers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get items for a specific quotation
     */
    @GetMapping("/{qId}/items")
    public ResponseEntity<Map<String, Object>> getQuotationItems(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<QuotationItemDTO> items = quotationService.getQuotationItemsByQuotationId(qId);
            response.put("success", true);
            response.put("items", items);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotation items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update quotation status
     */
    @PutMapping("/{qId}/status")
    public ResponseEntity<Map<String, Object>> updateQuotationStatus(
            @PathVariable Integer qId, 
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String status = request.get("status");
            boolean updated = quotationService.updateQuotationStatus(qId, status);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Quotation status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update quotation status");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating quotation status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update quotation with items
     */
    @PutMapping("/{qId}")
    public ResponseEntity<Map<String, Object>> updateQuotationWithItems(
            @PathVariable Integer qId,
            @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract quotation data
            Map<String, Object> quotationData = (Map<String, Object>) request.get("quotation");
            QuotationDTO quotation = new QuotationDTO();
            quotation.setQId(qId);
            quotation.setProjectId((String) quotationData.get("projectId"));
            quotation.setQsId((String) quotationData.get("qsId"));
            quotation.setDate(java.sql.Date.valueOf(java.time.LocalDate.parse((String) quotationData.get("date"))));
            quotation.setDeadline(java.sql.Date.valueOf(java.time.LocalDate.parse((String) quotationData.get("deadline"))));
            quotation.setStatus((String) quotationData.get("status"));
            quotation.setDescription((String) quotationData.get("description"));

            // Extract items data
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
            List<QuotationItemDTO> items = null;
            
            if (itemsData != null) {
                items = new java.util.ArrayList<>();
                for (Map<String, Object> itemData : itemsData) {
                    QuotationItemDTO item = new QuotationItemDTO();
                    item.setQId(qId);
                    item.setName((String) itemData.get("name"));
                    item.setDescription((String) itemData.get("description"));
                    item.setAmount(new java.math.BigDecimal(itemData.get("amount").toString()));
                    item.setQuantity(Integer.valueOf(itemData.get("quantity").toString()));
                    items.add(item);
                }
            }

            boolean updated = quotationService.updateQuotationWithItems(quotation, items);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Quotation updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update quotation");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete quotation
     */
    @DeleteMapping("/{qId}")
    public ResponseEntity<Map<String, Object>> deleteQuotation(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = quotationService.deleteQuotation(qId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Quotation deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to delete quotation");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Add supplier to existing quotation5
     */
    @PostMapping("/{qId}/suppliers")
    public ResponseEntity<Map<String, Object>> addQuotationSupplier(
            @PathVariable Integer qId,
            @RequestBody Map<String, Integer> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer supplierId = request.get("supplierId");
            quotationService.addQuotationSupplier(qId, supplierId);
            
            response.put("success", true);
            response.put("message", "Supplier added to quotation successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding supplier to quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Add item to existing quotation
     */
    @PostMapping("/{qId}/items")
    public ResponseEntity<Map<String, Object>> addQuotationItem(
            @PathVariable Integer qId,
            @RequestBody QuotationItemDTO item) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            item.setQId(qId);
            quotationService.addQuotationItem(item);
            
            response.put("success", true);
            response.put("message", "Item added to quotation successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error adding item to quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete quotation item
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> deleteQuotationItem(@PathVariable Integer itemId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = quotationService.deleteQuotationItem(itemId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Quotation item deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to delete quotation item");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting quotation item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ QUOTATION RESPONSE METHODS ============

    /**
     * Test database connection for quotation responses
     */
    @GetMapping("/responses/test")
    public ResponseEntity<Map<String, Object>> testQuotationResponseDatabase() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<QuotationResponseDTO> basicResponses = quotationResponseService.getAllQuotationResponses();
            List<QuotationResponseWithSupplierDTO> responsesWithSupplier = 
                quotationResponseService.getAllQuotationResponsesWithSupplier();
            
            response.put("success", true);
            response.put("basicResponsesCount", basicResponses.size());
            response.put("responsesWithSupplierCount", responsesWithSupplier.size());
            response.put("basicResponses", basicResponses);
            response.put("responsesWithSupplier", responsesWithSupplier);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("errorClass", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                response.put("cause", e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get all quotation responses with supplier details
     */
    @GetMapping("/responses/all")
    public ResponseEntity<Map<String, Object>> getAllQuotationResponsesWithSupplier() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<QuotationResponseWithSupplierDTO> responses = 
                quotationResponseService.getAllQuotationResponsesWithSupplier();
            response.put("success", true);
            response.put("responses", responses);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotation responses: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get quotation responses for a specific quotation with supplier details
     */
    @GetMapping("/{qId}/responses")
    public ResponseEntity<Map<String, Object>> getQuotationResponsesByQuotationId(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<QuotationResponseWithSupplierDTO> responses = 
                quotationResponseService.getQuotationResponsesWithSupplierByQuotationId(qId);
            response.put("success", true);
            response.put("responses", responses);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotation responses: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get quotation responses by supplier with supplier details
     */
    @GetMapping("/responses/supplier/{supplierId}")
    public ResponseEntity<Map<String, Object>> getQuotationResponsesBySupplierId(@PathVariable Integer supplierId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<QuotationResponseWithSupplierDTO> responses = 
                quotationResponseService.getQuotationResponsesWithSupplierBySupplierId(supplierId);
            response.put("success", true);
            response.put("responses", responses);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotation responses for supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get quotation response by ID with supplier details
     */
    @GetMapping("/responses/{responseId}")
    public ResponseEntity<Map<String, Object>> getQuotationResponseById(@PathVariable Integer responseId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            QuotationResponseWithSupplierDTO quotationResponse = 
                quotationResponseService.getQuotationResponseWithSupplierById(responseId);
            
            if (quotationResponse != null) {
                response.put("success", true);
                response.put("response", quotationResponse);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Quotation response not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching quotation response: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new quotation response
     */
    @PostMapping("/responses")
    public ResponseEntity<Map<String, Object>> createQuotationResponse(@RequestBody QuotationResponseDTO quotationResponse) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer responseId = quotationResponseService.createQuotationResponse(quotationResponse);
            
            if (responseId != null) {
                response.put("success", true);
                response.put("message", "Quotation response created successfully");
                response.put("responseId", responseId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to create quotation response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating quotation response: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update quotation response status
     */
    @PutMapping("/responses/{responseId}/status")
    public ResponseEntity<Map<String, Object>> updateQuotationResponseStatus(
            @PathVariable Integer responseId, 
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String status = request.get("status");
            boolean updated = quotationResponseService.updateQuotationResponseStatus(responseId, status);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Quotation response status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update quotation response status");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating quotation response status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update quotation response
     */
    @PutMapping("/responses/{responseId}")
    public ResponseEntity<Map<String, Object>> updateQuotationResponse(
            @PathVariable Integer responseId,
            @RequestBody QuotationResponseDTO quotationResponse) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            quotationResponse.setResponseId(responseId);
            boolean updated = quotationResponseService.updateQuotationResponse(quotationResponse);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Quotation response updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update quotation response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating quotation response: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete quotation response
     */
    @DeleteMapping("/responses/{responseId}")
    public ResponseEntity<Map<String, Object>> deleteQuotationResponse(@PathVariable Integer responseId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = quotationResponseService.deleteQuotationResponse(responseId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Quotation response deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to delete quotation response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting quotation response: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ PURCHASE ORDER METHODS ============

    /**
     * Convert quotation response to purchase order
     */
    @PostMapping("/responses/{responseId}/create-purchase-order")
    public ResponseEntity<Map<String, Object>> createPurchaseOrderFromResponse(
            @PathVariable Integer responseId,
            @RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Extract additional purchase order details (all optional)
            String deliveryDateStr = null;
            String paymentStatus = "pending";
            Boolean orderStatus = false;
            
            if (request != null) {
                deliveryDateStr = (String) request.get("estimatedDeliveryDate");
                String requestPaymentStatus = (String) request.get("paymentStatus");
                Boolean requestOrderStatus = (Boolean) request.get("orderStatus");
                
                if (requestPaymentStatus != null && !requestPaymentStatus.isEmpty()) {
                    paymentStatus = requestPaymentStatus;
                }
                if (requestOrderStatus != null) {
                    orderStatus = requestOrderStatus;
                }
            }
            
            java.time.LocalDate deliveryDate = null;
            if (deliveryDateStr != null && !deliveryDateStr.isEmpty()) {
                try {
                    deliveryDate = java.time.LocalDate.parse(deliveryDateStr);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "Invalid delivery date format. Use YYYY-MM-DD format.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }
            
            // Create purchase order from quotation response
            Integer purchaseOrderId = quotationResponseService.createPurchaseOrderFromResponse(
                responseId, deliveryDate, paymentStatus, orderStatus);
            
            if (purchaseOrderId != null) {
                response.put("success", true);
                response.put("message", "Purchase order created successfully from quotation response");
                response.put("purchaseOrderId", purchaseOrderId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to create purchase order from quotation response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating purchase order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ SUPPLIER QUOTATION ENDPOINTS ============
    // These endpoints bridge the gap between SupplierQuotationController and frontend expectations

    /**
     * Get all quotation requests for supplier dashboard
     */
    @GetMapping("/supplier/requests")
    public ResponseEntity<List<QuotationDTO>> getAllQuotationRequestsForSupplier() {
        try {
            List<QuotationDTO> quotations = quotationService.getAllQuotations();
            return ResponseEntity.ok(quotations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get quotations assigned to a specific supplier
     */
    @GetMapping("/supplier/{supplierId}/quotations")
    public ResponseEntity<List<QuotationDTO>> getQuotationsBySupplierId(@PathVariable Integer supplierId) {
        try {
            // For now, return all quotations. In a real scenario, you'd filter by supplier
            List<QuotationDTO> quotations = quotationService.getAllQuotations();
            return ResponseEntity.ok(quotations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get quotation details by ID for supplier
     */
    @GetMapping("/supplier/quotations/{quotationId}")
    public ResponseEntity<QuotationDTO> getSupplierQuotationById(@PathVariable Integer quotationId) {
        try {
            QuotationDTO quotation = quotationService.getQuotationById(quotationId);
            if (quotation != null) {
                return ResponseEntity.ok(quotation);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get quotation items for a specific quotation (supplier endpoint)
     */
    @GetMapping("/supplier/quotations/{quotationId}/items")
    public ResponseEntity<List<QuotationItemDTO>> getSupplierQuotationItems(@PathVariable Integer quotationId) {
        try {
            if (quotationId == null) {
                return ResponseEntity.badRequest().body(null);
            }
            List<QuotationItemDTO> items = quotationService.getQuotationItemsByQuotationId(quotationId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Submit quotation response (supplier endpoint)
     */
    @PostMapping("/supplier/quotations/respond")
    public ResponseEntity<Map<String, Object>> submitSupplierQuotationResponse(@RequestBody QuotationResponseDTO responseDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate required fields
            if (responseDTO.getQId() == null || responseDTO.getQId() <= 0) {
                response.put("success", false);
                response.put("message", "Valid quotation ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (responseDTO.getSupplierId() == null || responseDTO.getSupplierId() <= 0) {
                response.put("success", false);
                response.put("message", "Valid supplier ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (responseDTO.getTotalAmount() == null || responseDTO.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                response.put("success", false);
                response.put("message", "Valid total amount is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Set default values
            if (responseDTO.getStatus() == null || responseDTO.getStatus().trim().isEmpty()) {
                responseDTO.setStatus("SUBMITTED");
            }

            if (responseDTO.getRespondDate() == null) {
                responseDTO.setRespondDate(new java.sql.Date(System.currentTimeMillis()));
            }

            Integer responseId = quotationResponseService.createQuotationResponse(responseDTO);

            if (responseId != null) {
                response.put("success", true);
                response.put("message", "Quotation response submitted successfully");
                response.put("responseId", responseId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to create quotation response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating quotation response: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get quotation response by quotation ID (supplier endpoint)
     */
    @GetMapping("/supplier/quotations/{quotationId}/response")
    public ResponseEntity<QuotationResponseDTO> getSupplierQuotationResponse(@PathVariable Integer quotationId) {
        try {
            if (quotationId == null) {
                return ResponseEntity.badRequest().body(null);
            }

            // Get responses for this quotation
            List<QuotationResponseDTO> responses = quotationResponseService.getQuotationResponsesByQuotationId(quotationId);
            if (!responses.isEmpty()) {
                return ResponseEntity.ok(responses.get(0)); // Return first response
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Update quotation response (supplier endpoint)
     */
    @PutMapping("/supplier/quotations/response")
    public ResponseEntity<Map<String, Object>> updateSupplierQuotationResponse(@RequestBody QuotationResponseDTO responseDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (responseDTO.getResponseId() == null || responseDTO.getResponseId() <= 0) {
                response.put("success", false);
                response.put("message", "Valid response ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            boolean updated = quotationResponseService.updateQuotationResponse(responseDTO);

            if (updated) {
                response.put("success", true);
                response.put("message", "Quotation response updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update quotation response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating quotation response: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get quotation responses by supplier ID
     */
    @GetMapping("/supplier/responses/{supplierId}")
    public ResponseEntity<List<QuotationResponseWithSupplierDTO>> getSupplierQuotationResponses(@PathVariable Integer supplierId) {
        try {
            if (supplierId == null) {
                return ResponseEntity.badRequest().body(null);
            }

            List<QuotationResponseWithSupplierDTO> responses =
                quotationResponseService.getQuotationResponsesWithSupplierBySupplierId(supplierId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
