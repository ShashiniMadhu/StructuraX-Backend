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

import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.service.AdminService;
import com.structurax.root.structurax.root.service.MailService;
import com.structurax.root.structurax.root.service.PurchaseOrderService;
import com.structurax.root.structurax.root.service.QuotationResponseService;
import com.structurax.root.structurax.root.service.QuotationService;
import com.structurax.root.structurax.root.service.SQSService;
import com.structurax.root.structurax.root.service.SupplierService;

@RestController
@RequestMapping("/quotation")
@CrossOrigin(origins = "http://localhost:5173")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private QuotationResponseService quotationResponseService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private SQSService sqsService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

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
     * Add supplier to existing quotation
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
     * Update quotation response status and auto-check for quotation closure
     */
    @PutMapping("/responses/{responseId}/status-with-auto-close")
    public ResponseEntity<Map<String, Object>> updateQuotationResponseStatusWithAutoClose(
        @PathVariable Integer responseId,
        @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String status = request.get("status");

            // Get the quotation response to find the quotation ID
            QuotationResponseDTO quotationResponse = quotationResponseService.getQuotationResponseById(responseId);
            if (quotationResponse == null) {
                response.put("success", false);
                response.put("message", "Quotation response not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update the status
            boolean updated = quotationResponseService.updateQuotationResponseStatus(responseId, status);

            if (updated) {
                response.put("success", true);
                response.put("message", "Quotation response status updated successfully");
                response.put("responseId", responseId);
                response.put("newStatus", status);

                // If status is rejected, try to close the quotation
                if ("rejected".equalsIgnoreCase(status)) {
                    boolean closed = quotationService.closeQuotationIfNoResponsesOrAllRejected(quotationResponse.getQId());
                    if (closed) {
                        response.put("quotationClosed", true);
                        response.put("quotationId", quotationResponse.getQId());
                        response.put("message", "Quotation response status updated and quotation closed automatically");
                    } else {
                        response.put("quotationClosed", false);
                        response.put("quotationId", quotationResponse.getQId());
                    }
                } else {
                    response.put("quotationClosed", false);
                }

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

    // ============ SUPPLIER METHODS (from Malith-new-Backend) ============

    /**
     * Get all suppliers with their contact information (including emails)
     */
    @GetMapping("/suppliers/all")
    public ResponseEntity<Map<String, Object>> getAllSuppliers() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SupplierDTO> suppliers = supplierService.getAllSuppliers();

            // Create a simplified view with essential supplier information
            List<Map<String, Object>> supplierList = new java.util.ArrayList<>();
            for (SupplierDTO supplier : suppliers) {
                Map<String, Object> supplierInfo = new HashMap<>();
                supplierInfo.put("supplierId", supplier.getSupplier_id());
                supplierInfo.put("supplierName", supplier.getSupplier_name());
                supplierInfo.put("email", supplier.getEmail());
                supplierInfo.put("phone", supplier.getPhone());
                supplierInfo.put("address", supplier.getAddress());
                supplierInfo.put("status", supplier.getStatus());
                supplierInfo.put("joinedDate", supplier.getJoined_date());
                supplierList.add(supplierInfo);
            }

            response.put("success", true);
            response.put("suppliers", supplierList);
            response.put("totalCount", suppliers.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching suppliers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get suppliers with only essential contact information (ID, Name, Email)
     */
    @GetMapping("/suppliers/contacts")
    public ResponseEntity<Map<String, Object>> getSuppliersContacts() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SupplierDTO> suppliers = supplierService.getAllSuppliers();

            // Create a minimal view with only contact essentials
            List<Map<String, Object>> contactList = new java.util.ArrayList<>();
            for (SupplierDTO supplier : suppliers) {
                Map<String, Object> contact = new HashMap<>();
                contact.put("supplierId", supplier.getSupplier_id());
                contact.put("supplierName", supplier.getSupplier_name());
                contact.put("email", supplier.getEmail());
                contactList.add(contact);
            }

            response.put("success", true);
            response.put("contacts", contactList);
            response.put("totalCount", suppliers.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching supplier contacts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get active suppliers only
     */
    @GetMapping("/suppliers/active")
    public ResponseEntity<Map<String, Object>> getActiveSuppliers() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<SupplierDTO> allSuppliers = supplierService.getAllSuppliers();

            // Filter for active suppliers only
            List<Map<String, Object>> activeSuppliers = new java.util.ArrayList<>();
            for (SupplierDTO supplier : allSuppliers) {
                if ("active".equalsIgnoreCase(supplier.getStatus()) || "Active".equalsIgnoreCase(supplier.getStatus())) {
                    Map<String, Object> supplierInfo = new HashMap<>();
                    supplierInfo.put("supplierId", supplier.getSupplier_id());
                    supplierInfo.put("supplierName", supplier.getSupplier_name());
                    supplierInfo.put("email", supplier.getEmail());
                    supplierInfo.put("phone", supplier.getPhone());
                    supplierInfo.put("address", supplier.getAddress());
                    supplierInfo.put("status", supplier.getStatus());
                    activeSuppliers.add(supplierInfo);
                }
            }

            response.put("success", true);
            response.put("suppliers", activeSuppliers);
            response.put("totalActive", activeSuppliers.size());
            response.put("totalSuppliers", allSuppliers.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching active suppliers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ EMAIL NOTIFICATION METHODS (from Malith-new-Backend) ============

    /**
     * Send quotation request to specific suppliers
     */
    @PostMapping("/{qId}/send-quotation-request")
    public ResponseEntity<Map<String, Object>> sendQuotationRequestToSuppliers(
        @PathVariable Integer qId,
        @RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get the quotation details
            QuotationDTO quotation = quotationService.getQuotationById(qId);
            if (quotation == null) {
                response.put("success", false);
                response.put("message", "Quotation not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Get project details
            Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
            String projectName = project != null ? project.getName() : "Project #" + quotation.getProjectId();

            // Get QS details
            EmployeeDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
            String qsName = qsEmployee != null ? qsEmployee.getName() : "QS";
            String qsEmail = qsEmployee != null ? qsEmployee.getEmail() : "noreply@structurax.com";

            // Determine which suppliers to send to
            List<Integer> supplierIds = null;
            if (request != null && request.containsKey("supplierIds")) {
                @SuppressWarnings("unchecked")
                List<Object> supplierData = (List<Object>) request.get("supplierIds");
                if (supplierData != null && !supplierData.isEmpty()) {
                    supplierIds = new java.util.ArrayList<>();
                    for (Object supplierId : supplierData) {
                        supplierIds.add(Integer.valueOf(supplierId.toString()));
                    }
                }
            }

            // If no specific suppliers provided, get all suppliers for this quotation
            if (supplierIds == null || supplierIds.isEmpty()) {
                List<com.structurax.root.structurax.root.dto.QuotationSupplierDTO> quotationSuppliers =
                    quotationService.getQuotationSuppliersByQuotationId(qId);
                supplierIds = new java.util.ArrayList<>();
                for (com.structurax.root.structurax.root.dto.QuotationSupplierDTO qs : quotationSuppliers) {
                    supplierIds.add(qs.getSupplierId());
                }
            }

            if (supplierIds.isEmpty()) {
                response.put("success", false);
                response.put("message", "No suppliers found for this quotation");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Send emails to each supplier
            int emailsSent = 0;
            List<Map<String, Object>> emailResults = new java.util.ArrayList<>();

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

                        emailsSent++;
                        Map<String, Object> emailResult = new HashMap<>();
                        emailResult.put("supplierId", supplierId);
                        emailResult.put("supplierName", supplier.getSupplier_name());
                        emailResult.put("email", supplier.getEmail());
                        emailResult.put("status", "sent");
                        emailResults.add(emailResult);
                    } else {
                        Map<String, Object> emailResult = new HashMap<>();
                        emailResult.put("supplierId", supplierId);
                        emailResult.put("status", "failed");
                        emailResult.put("reason", "Invalid supplier or email not found");
                        emailResults.add(emailResult);
                    }
                } catch (Exception e) {
                    Map<String, Object> emailResult = new HashMap<>();
                    emailResult.put("supplierId", supplierId);
                    emailResult.put("status", "failed");
                    emailResult.put("reason", "Error: " + e.getMessage());
                    emailResults.add(emailResult);
                }
            }

            // Update quotation status to 'sent' if emails were sent
            if (emailsSent > 0) {
                quotationService.updateQuotationStatus(qId, "sent");
            }

            response.put("success", true);
            response.put("message", "Quotation request processed");
            response.put("quotationId", qId);
            response.put("projectName", projectName);
            response.put("emailsSent", emailsSent);
            response.put("totalSuppliers", supplierIds.size());
            response.put("emailResults", emailResults);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending quotation request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Send purchase order notification to supplier
     */
    @PostMapping("/purchase-orders/{orderId}/send-notification")
    public ResponseEntity<Map<String, Object>> sendPurchaseOrderNotification(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get purchase order details
            PurchaseOrderDTO purchaseOrder = purchaseOrderService.getPurchaseOrderById(orderId);
            if (purchaseOrder == null) {
                response.put("success", false);
                response.put("message", "Purchase order not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Get supplier details
            SupplierDTO supplier = supplierService.getSupplierById(purchaseOrder.getSupplierId());
            if (supplier == null || supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
                response.put("success", false);
                response.put("message", "Supplier not found or email not available");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Get project details
            Project1DTO project = sqsService.getProjectById(purchaseOrder.getProjectId());
            String projectName = project != null ? project.getName() : "Project #" + purchaseOrder.getProjectId();

            // Get quotation response to find the original QS
            QuotationResponseWithSupplierDTO quotationResponse =
                quotationResponseService.getQuotationResponseWithSupplierById(purchaseOrder.getResponseId());

            String qsName = "QS";
            String qsEmail = "noreply@structurax.com";

            if (quotationResponse != null) {
                // Get the quotation to find QS details
                QuotationDTO quotation = quotationService.getQuotationById(quotationResponse.getQId());
                if (quotation != null) {
                    EmployeeDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
                    if (qsEmployee != null) {
                        qsName = qsEmployee.getName();
                        qsEmail = qsEmployee.getEmail();
                    }
                }
            }

            // Send purchase order notification
            mailService.sendPurchaseOrderNotification(
                supplier.getEmail(),
                supplier.getSupplier_name(),
                orderId,
                projectName,
                qsName,
                qsEmail,
                purchaseOrder.getOrderDate() != null ? purchaseOrder.getOrderDate().toString() : "Today",
                purchaseOrder.getEstimatedDeliveryDate() != null ? purchaseOrder.getEstimatedDeliveryDate().toString() : null
            );

            response.put("success", true);
            response.put("message", "Purchase order notification sent successfully");
            response.put("orderId", orderId);
            response.put("supplierName", supplier.getSupplier_name());
            response.put("supplierEmail", supplier.getEmail());
            response.put("projectName", projectName);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending purchase order notification: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Send quotation request to all suppliers in the system
     */
    @PostMapping("/{qId}/send-to-all-suppliers")
    public ResponseEntity<Map<String, Object>> sendQuotationRequestToAllSuppliers(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get the quotation details
            QuotationDTO quotation = quotationService.getQuotationById(qId);
            if (quotation == null) {
                response.put("success", false);
                response.put("message", "Quotation not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Get all active suppliers
            List<SupplierDTO> allSuppliers = supplierService.getAllSuppliers();
            List<SupplierDTO> activeSuppliers = new java.util.ArrayList<>();

            for (SupplierDTO supplier : allSuppliers) {
                if ("active".equalsIgnoreCase(supplier.getStatus()) && supplier.getEmail() != null && !supplier.getEmail().isEmpty()) {
                    activeSuppliers.add(supplier);
                }
            }

            if (activeSuppliers.isEmpty()) {
                response.put("success", false);
                response.put("message", "No active suppliers with valid emails found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Get project and QS details
            Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
            String projectName = project != null ? project.getName() : "Project #" + quotation.getProjectId();

            EmployeeDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
            String qsName = qsEmployee != null ? qsEmployee.getName() : "QS";
            String qsEmail = qsEmployee != null ? qsEmployee.getEmail() : "noreply@structurax.com";

            // Send emails to all active suppliers
            int emailsSent = 0;
            List<Map<String, Object>> emailResults = new java.util.ArrayList<>();

            for (SupplierDTO supplier : activeSuppliers) {
                try {
                    mailService.sendQuotationRequest(
                        supplier.getEmail(),
                        supplier.getSupplier_name(),
                        qId,
                        projectName,
                        qsName,
                        qsEmail,
                        quotation.getDeadline() != null ? quotation.getDeadline().toString() : "Not specified"
                    );

                    // Add supplier to quotation_supplier table if not already added
                    try {
                        quotationService.addQuotationSupplier(qId, supplier.getSupplier_id());
                    } catch (Exception e) {
                        // Supplier might already be added, ignore duplicate errors
                    }

                    emailsSent++;
                    Map<String, Object> emailResult = new HashMap<>();
                    emailResult.put("supplierId", supplier.getSupplier_id());
                    emailResult.put("supplierName", supplier.getSupplier_name());
                    emailResult.put("email", supplier.getEmail());
                    emailResult.put("status", "sent");
                    emailResults.add(emailResult);

                } catch (Exception e) {
                    Map<String, Object> emailResult = new HashMap<>();
                    emailResult.put("supplierId", supplier.getSupplier_id());
                    emailResult.put("supplierName", supplier.getSupplier_name());
                    emailResult.put("status", "failed");
                    emailResult.put("reason", "Error: " + e.getMessage());
                    emailResults.add(emailResult);
                }
            }

            // Update quotation status to 'sent'
            if (emailsSent > 0) {
                quotationService.updateQuotationStatus(qId, "sent");
            }

            response.put("success", true);
            response.put("message", "Quotation request sent to all active suppliers");
            response.put("quotationId", qId);
            response.put("projectName", projectName);
            response.put("emailsSent", emailsSent);
            response.put("totalActiveSuppliers", activeSuppliers.size());
            response.put("emailResults", emailResults);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending quotation request to all suppliers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Close quotation if there are no responses or all responses are rejected
     */
    @PutMapping("/{qId}/close-if-no-responses-or-rejected")
    public ResponseEntity<Map<String, Object>> closeQuotationIfNoResponsesOrAllRejected(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean closed = quotationService.closeQuotationIfNoResponsesOrAllRejected(qId);

            if (closed) {
                response.put("success", true);
                response.put("message", "Quotation closed successfully");
                response.put("qId", qId);
                response.put("action", "closed");
                return ResponseEntity.ok(response);
            } else {
                // Check why it wasn't closed - provide more detailed feedback
                QuotationDTO quotation = quotationService.getQuotationById(qId);
                if (quotation == null) {
                    response.put("success", false);
                    response.put("message", "Quotation not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                List<QuotationResponseDTO> responses = quotationResponseService.getQuotationResponsesByQuotationId(qId);

                if ("closed".equalsIgnoreCase(quotation.getStatus())) {
                    response.put("success", true);
                    response.put("message", "Quotation is already closed");
                    response.put("qId", qId);
                    response.put("action", "already_closed");
                } else if (responses != null && !responses.isEmpty()) {
                    // Check if there are any non-rejected responses
                    boolean hasNonRejectedResponses = false;
                    for (QuotationResponseDTO resp : responses) {
                        if (!"rejected".equalsIgnoreCase(resp.getStatus())) {
                            hasNonRejectedResponses = true;
                            break;
                        }
                    }
                    if (hasNonRejectedResponses) {
                        response.put("success", true);
                        response.put("message", "Quotation has pending or accepted responses, cannot close");
                        response.put("qId", qId);
                        response.put("action", "not_closed_has_responses");
                        response.put("totalResponses", responses.size());
                    } else {
                        response.put("success", false);
                        response.put("message", "Failed to close quotation despite all responses being rejected");
                        response.put("qId", qId);
                    }
                } else {
                    response.put("success", false);
                    response.put("message", "Failed to close quotation despite no responses");
                    response.put("qId", qId);
                }

                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error checking/closing quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get detailed status of a quotation including response analysis
     */
    @GetMapping("/{qId}/close-status")
    public ResponseEntity<Map<String, Object>> getQuotationCloseStatus(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();

        try {
            QuotationDTO quotation = quotationService.getQuotationById(qId);
            if (quotation == null) {
                response.put("success", false);
                response.put("message", "Quotation not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<QuotationResponseDTO> responses = quotationResponseService.getQuotationResponsesByQuotationId(qId);

            // Analyze response statuses
            int totalResponses = responses != null ? responses.size() : 0;
            int pendingResponses = 0;
            int acceptedResponses = 0;
            int rejectedResponses = 0;

            if (responses != null) {
                for (QuotationResponseDTO resp : responses) {
                    String status = resp.getStatus();
                    if ("pending".equalsIgnoreCase(status)) {
                        pendingResponses++;
                    } else if ("accepted".equalsIgnoreCase(status)) {
                        acceptedResponses++;
                    } else if ("rejected".equalsIgnoreCase(status)) {
                        rejectedResponses++;
                    }
                }
            }

            boolean canClose = (totalResponses == 0) || (totalResponses > 0 && rejectedResponses == totalResponses);
            boolean isAlreadyClosed = "closed".equalsIgnoreCase(quotation.getStatus());

            response.put("success", true);
            response.put("quotationId", qId);
            response.put("currentStatus", quotation.getStatus());
            response.put("isAlreadyClosed", isAlreadyClosed);
            response.put("canClose", canClose);
            response.put("totalResponses", totalResponses);
            response.put("pendingResponses", pendingResponses);
            response.put("acceptedResponses", acceptedResponses);
            response.put("rejectedResponses", rejectedResponses);

            if (canClose && !isAlreadyClosed) {
                response.put("closeReason", totalResponses == 0 ? "No responses received" : "All responses are rejected");
            } else if (!canClose) {
                response.put("closeReason", "Has pending or accepted responses");
            } else if (isAlreadyClosed) {
                response.put("closeReason", "Already closed");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error analyzing quotation status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Batch close all eligible quotations (no responses or all rejected)
     */
    @PutMapping("/close-all-eligible")
    public ResponseEntity<Map<String, Object>> closeAllEligibleQuotations() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> result = quotationService.closeAllEligibleQuotations();

            response.put("success", true);
            response.put("message", "Batch processing completed");
            response.putAll(result); // Include all details from service result

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error in batch processing: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get email sending status for a quotation
     */
    @GetMapping("/{qId}/email-status")
    public ResponseEntity<Map<String, Object>> getQuotationEmailStatus(@PathVariable Integer qId) {
        Map<String, Object> response = new HashMap<>();

        try {
            QuotationDTO quotation = quotationService.getQuotationById(qId);
            if (quotation == null) {
                response.put("success", false);
                response.put("message", "Quotation not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<com.structurax.root.structurax.root.dto.QuotationSupplierDTO> quotationSuppliers =
                quotationService.getQuotationSuppliersByQuotationId(qId);

            List<Map<String, Object>> supplierDetails = new java.util.ArrayList<>();
            for (com.structurax.root.structurax.root.dto.QuotationSupplierDTO qs : quotationSuppliers) {
                SupplierDTO supplier = supplierService.getSupplierById(qs.getSupplierId());
                if (supplier != null) {
                    Map<String, Object> supplierDetail = new HashMap<>();
                    supplierDetail.put("supplierId", supplier.getSupplier_id());
                    supplierDetail.put("supplierName", supplier.getSupplier_name());
                    supplierDetail.put("email", supplier.getEmail());
                    supplierDetail.put("status", supplier.getStatus());
                    supplierDetails.add(supplierDetail);
                }
            }

            response.put("success", true);
            response.put("quotationId", qId);
            response.put("quotationStatus", quotation.getStatus());
            response.put("suppliers", supplierDetails);
            response.put("totalSuppliers", supplierDetails.size());
            response.put("deadline", quotation.getDeadline());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching email status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
	
	// ============ SUPPLIER QUOTATION ENDPOINTS (from Dev) ============
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