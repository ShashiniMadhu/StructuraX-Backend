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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.PurchaseOrderService;

@RestController
@RequestMapping("/purchase-order")
@CrossOrigin(origins = "http://localhost:5173")
public class PurchaseOrderController {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * Get all purchase orders
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPurchaseOrders() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> orders = purchaseOrderService.getAllPurchaseOrders();
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrderById(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PurchaseOrderDTO order = purchaseOrderService.getPurchaseOrderById(orderId);
            
            if (order != null) {
                response.put("success", true);
                response.put("order", order);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Purchase order not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase orders by project ID
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersByProjectId(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersByProjectId(projectId);
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders for project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase orders by supplier ID
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersBySupplierId(@PathVariable Integer supplierId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersBySupplierId(supplierId);
            response.put("success", true);
            response.put("orders", orders);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders for supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase order items by order ID
     */
    @GetMapping("/{orderId}/items")
    public ResponseEntity<Map<String, Object>> getPurchaseOrderItems(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<OrderItemDTO> items = purchaseOrderService.getPurchaseOrderItemsByOrderId(orderId);
            response.put("success", true);
            response.put("items", items);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase order items: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update purchase order status
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updatePurchaseOrderStatus(
            @PathVariable Integer orderId, 
            @RequestBody Map<String, Boolean> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Boolean orderStatus = request.get("orderStatus");
            boolean updated = purchaseOrderService.updatePurchaseOrderStatus(orderId, orderStatus);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Purchase order status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update purchase order status");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating purchase order status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update payment status
     */
    @PutMapping("/{orderId}/payment-status")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable Integer orderId, 
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String paymentStatus = request.get("paymentStatus");
            boolean updated = purchaseOrderService.updatePaymentStatus(orderId, paymentStatus);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Payment status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update payment status");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating payment status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update purchase order
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> updatePurchaseOrder(
            @PathVariable Integer orderId,
            @RequestBody PurchaseOrderDTO purchaseOrder) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            purchaseOrder.setOrderId(orderId);
            boolean updated = purchaseOrderService.updatePurchaseOrder(purchaseOrder);
            
            if (updated) {
                response.put("success", true);
                response.put("message", "Purchase order updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to update purchase order");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating purchase order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete purchase order
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> deletePurchaseOrder(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = purchaseOrderService.deletePurchaseOrder(orderId);
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Purchase order deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to delete purchase order");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting purchase order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
