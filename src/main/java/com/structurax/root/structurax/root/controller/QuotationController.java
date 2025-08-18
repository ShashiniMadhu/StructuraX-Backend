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
import com.structurax.root.structurax.root.service.QuotationService;

@RestController
@RequestMapping("/quotation")
@CrossOrigin(origins = "http://localhost:5173")
public class QuotationController {
    
    @Autowired
    private QuotationService quotationService;

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
            quotation.setDate(java.time.LocalDate.parse((String) quotationData.get("date")));
            quotation.setDeadline(java.time.LocalDate.parse((String) quotationData.get("deadline")));
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
            quotation.setDate(java.time.LocalDate.parse((String) quotationData.get("date")));
            quotation.setDeadline(java.time.LocalDate.parse((String) quotationData.get("deadline")));
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
}
