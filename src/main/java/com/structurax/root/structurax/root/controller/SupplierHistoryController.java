package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.SupplierHistoryDTO;
import com.structurax.root.structurax.root.service.SupplierHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supplier/history")
@CrossOrigin(origins = "*")
public class SupplierHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierHistoryController.class);

    private final SupplierHistoryService supplierHistoryService;

    public SupplierHistoryController(SupplierHistoryService supplierHistoryService) {
        this.supplierHistoryService = supplierHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierHistoryDTO>> getAllHistory() {
        try {
            logger.info("Fetching all supplier history records");
            List<SupplierHistoryDTO> list = supplierHistoryService.getAllHistory();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error fetching supplier history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<SupplierHistoryDTO>> getHistoryBySupplierId(@PathVariable Integer supplierId) {
        try {
            logger.info("Fetching supplier history for supplierId={}", supplierId);
            List<SupplierHistoryDTO> list = supplierHistoryService.getHistoryBySupplierId(supplierId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error fetching supplier history for supplier {}: {}", supplierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{historyId}")
    public ResponseEntity<SupplierHistoryDTO> getHistoryById(@PathVariable Integer historyId) {
        try {
            logger.info("Fetching supplier history by historyId={}", historyId);
            SupplierHistoryDTO history = supplierHistoryService.getHistoryById(historyId);
            if (history == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error fetching supplier history by id {}: {}", historyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getSupplierHistoryByOrderId(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching supplier history for orderId={}", orderId);
            List<SupplierHistoryDTO> list = supplierHistoryService.getHistoryByOrderId(orderId);
            response.put("success", true);
            response.put("histories", list);
            response.put("orderId", orderId);
            response.put("totalCount", list.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching supplier history for order {}: {}", orderId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching supplier history: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSupplierHistory(@RequestBody SupplierHistoryDTO historyDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Creating supplier history for supplierId={}", historyDTO.getSupplierId());
            SupplierHistoryDTO created = supplierHistoryService.createHistory(historyDTO);
            response.put("success", true);
            response.put("history", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating supplier history: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating supplier history: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/items/order/{orderId}/supplier/{supplierId}")
    public ResponseEntity<Map<String, Object>> getOrderItemsByOrderIdAndSupplierId(
            @PathVariable Integer orderId,
            @PathVariable Integer supplierId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching order items for orderId={} and supplierId={}", orderId, supplierId);
            List<OrderItemDTO> items = supplierHistoryService.getOrderItemsByOrderIdAndSupplierId(orderId, supplierId);
            response.put("success", true);
            response.put("items", items);
            response.put("orderId", orderId);
            response.put("supplierId", supplierId);
            response.put("totalCount", items.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching order items for order {} and supplier {}: {}", orderId, supplierId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching order items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
