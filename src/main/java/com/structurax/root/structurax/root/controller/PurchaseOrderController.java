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
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;
import com.structurax.root.structurax.root.service.PurchaseOrderService;
import com.structurax.root.structurax.root.service.QuotationResponseService;
import com.structurax.root.structurax.root.service.SQSService;

@RestController
@RequestMapping("/purchase-order")
@CrossOrigin(origins = "http://localhost:5173")
public class PurchaseOrderController {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    @Autowired
    private QuotationResponseService quotationResponseService;
    
    @Autowired
    private SQSService sqsService;

    /**
     * Get all purchase orders with enhanced details
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllPurchaseOrders() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> orders = purchaseOrderService.getAllPurchaseOrders();
            
            // Enhance each order with additional details
            List<Map<String, Object>> enhancedOrders = new java.util.ArrayList<>();
            for (PurchaseOrderDTO order : orders) {
                Map<String, Object> enhancedOrder = createEnhancedOrderResponse(order);
                enhancedOrders.add(enhancedOrder);
            }
            
            response.put("success", true);
            response.put("orders", enhancedOrders);
            response.put("totalCount", orders.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase order by ID with complete details
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrderById(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PurchaseOrderDTO order = purchaseOrderService.getPurchaseOrderById(orderId);
            
            if (order != null) {
                Map<String, Object> enhancedOrder = createEnhancedOrderResponse(order);
                response.put("success", true);
                response.put("order", enhancedOrder);
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
     * Get purchase orders by project ID with enhanced details
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersByProjectId(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersByProjectId(projectId);
            
            // Enhance each order with additional details
            List<Map<String, Object>> enhancedOrders = new java.util.ArrayList<>();
            for (PurchaseOrderDTO order : orders) {
                Map<String, Object> enhancedOrder = createEnhancedOrderResponse(order);
                enhancedOrders.add(enhancedOrder);
            }
            
            response.put("success", true);
            response.put("orders", enhancedOrders);
            response.put("projectId", projectId);
            response.put("totalCount", orders.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders for project: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase orders by supplier ID with enhanced details
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersBySupplierId(@PathVariable Integer supplierId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersBySupplierId(supplierId);
            
            // Enhance each order with additional details
            List<Map<String, Object>> enhancedOrders = new java.util.ArrayList<>();
            for (PurchaseOrderDTO order : orders) {
                Map<String, Object> enhancedOrder = createEnhancedOrderResponse(order);
                enhancedOrders.add(enhancedOrder);
            }
            
            response.put("success", true);
            response.put("orders", enhancedOrders);
            response.put("supplierId", supplierId);
            response.put("totalCount", orders.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders for supplier: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase orders by QS ID with items and supplier details
     */
    @GetMapping("/qs/{qsId}")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersByQsId(@PathVariable String qsId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersByQsId(qsId);
            
            // Enhance each order with supplier details and items
            List<Map<String, Object>> enhancedOrders = new java.util.ArrayList<>();
            for (PurchaseOrderDTO order : orders) {
                Map<String, Object> enhancedOrder = createEnhancedOrderResponseWithSupplier(order);
                enhancedOrders.add(enhancedOrder);
            }
            
            response.put("success", true);
            response.put("orders", enhancedOrders);
            response.put("qsId", qsId);
            response.put("totalCount", orders.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders for QS: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get purchase order with items by order ID
     */
    @GetMapping("/{orderId}/with-items")
    public ResponseEntity<Map<String, Object>> getPurchaseOrderWithItems(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PurchaseOrderDTO order = purchaseOrderService.getPurchaseOrderById(orderId);
            List<OrderItemDTO> items = purchaseOrderService.getPurchaseOrderItemsByOrderId(orderId);
            
            if (order != null) {
                response.put("success", true);
                response.put("order", order);
                response.put("items", items);
                response.put("itemCount", items != null ? items.size() : 0);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Purchase order not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase order with items: " + e.getMessage());
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

    /**
     * Get purchase orders summary by status
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPurchaseOrdersSummary() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<PurchaseOrderDTO> allOrders = purchaseOrderService.getAllPurchaseOrders();
            
            // Create summary statistics
            Map<String, Object> summary = new HashMap<>();
            int totalOrders = allOrders.size();
            int activeOrders = 0;
            int pendingPayments = 0;
            int partialPayments = 0;
            int paidOrders = 0;
            
            for (PurchaseOrderDTO order : allOrders) {
                if (order.getOrderStatus() != null && order.getOrderStatus()) {
                    activeOrders++;
                }
                
                String paymentStatus = order.getPaymentStatus();
                if ("pending".equals(paymentStatus)) {
                    pendingPayments++;
                } else if ("partial".equals(paymentStatus)) {
                    partialPayments++;
                } else if ("paid".equals(paymentStatus)) {
                    paidOrders++;
                }
            }
            
            summary.put("totalOrders", totalOrders);
            summary.put("activeOrders", activeOrders);
            summary.put("inactiveOrders", totalOrders - activeOrders);
            summary.put("pendingPayments", pendingPayments);
            summary.put("partialPayments", partialPayments);
            summary.put("paidOrders", paidOrders);
            
            response.put("success", true);
            response.put("summary", summary);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching purchase orders summary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ HELPER METHODS ============

    /**
     * Create enhanced order response with additional details
     */
    private Map<String, Object> createEnhancedOrderResponse(PurchaseOrderDTO order) {
        Map<String, Object> enhancedOrder = new HashMap<>();
        
        // Basic order information
        enhancedOrder.put("orderId", order.getOrderId());
        enhancedOrder.put("projectId", order.getProjectId());
        enhancedOrder.put("supplierId", order.getSupplierId());
        enhancedOrder.put("responseId", order.getResponseId());
        enhancedOrder.put("paymentStatus", order.getPaymentStatus());
        enhancedOrder.put("estimatedDeliveryDate", order.getEstimatedDeliveryDate());
        enhancedOrder.put("orderDate", order.getOrderDate());
        enhancedOrder.put("orderStatus", order.getOrderStatus());
        
        // Add project name for better context
        try {
            Project1DTO project = sqsService.getProjectById(order.getProjectId());
            if (project != null) {
                enhancedOrder.put("projectName", project.getName());
            }
        } catch (Exception e) {
            // If we can't get project name, continue without it (don't break the response)
            enhancedOrder.put("projectNameError", "Error loading project name: " + e.getMessage());
        }
        
        // Add order items
        try {
            List<OrderItemDTO> items = purchaseOrderService.getPurchaseOrderItemsByOrderId(order.getOrderId());
            enhancedOrder.put("items", items);
            enhancedOrder.put("itemCount", items != null ? items.size() : 0);
            
            // Calculate total amount from items
            java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
            if (items != null) {
                for (OrderItemDTO item : items) {
                    if (item.getUnitPrice() != null && item.getQuantity() != null) {
                        java.math.BigDecimal lineTotal = item.getUnitPrice()
                            .multiply(java.math.BigDecimal.valueOf(item.getQuantity()));
                        totalAmount = totalAmount.add(lineTotal);
                    }
                }
            }
            enhancedOrder.put("totalAmount", totalAmount);
            
        } catch (Exception e) {
            enhancedOrder.put("items", new java.util.ArrayList<>());
            enhancedOrder.put("itemCount", 0);
            enhancedOrder.put("totalAmount", java.math.BigDecimal.ZERO);
            enhancedOrder.put("itemsError", "Error loading items: " + e.getMessage());
        }
        
        // Add status descriptions
        enhancedOrder.put("orderStatusText", order.getOrderStatus() != null && order.getOrderStatus() ? "Active" : "Inactive");
        enhancedOrder.put("paymentStatusText", getPaymentStatusText(order.getPaymentStatus()));
        
        return enhancedOrder;
    }

    /**
     * Create enhanced order response with supplier details
     */
    private Map<String, Object> createEnhancedOrderResponseWithSupplier(PurchaseOrderDTO order) {
        Map<String, Object> enhancedOrder = createEnhancedOrderResponse(order);
        
        // Add supplier details from quotation response
        try {
            QuotationResponseWithSupplierDTO responseWithSupplier = 
                quotationResponseService.getQuotationResponseWithSupplierById(order.getResponseId());
            
            if (responseWithSupplier != null) {
                Map<String, Object> supplierDetails = new HashMap<>();
                supplierDetails.put("supplierId", responseWithSupplier.getSupplierId());
                supplierDetails.put("supplierName", responseWithSupplier.getSupplierName());
                supplierDetails.put("supplierEmail", responseWithSupplier.getSupplierEmail());
                supplierDetails.put("supplierPhone", responseWithSupplier.getSupplierPhone());
                supplierDetails.put("supplierAddress", responseWithSupplier.getSupplierAddress());
                
                enhancedOrder.put("supplierDetails", supplierDetails);
                enhancedOrder.put("quotationId", responseWithSupplier.getQId());
                enhancedOrder.put("quotationResponseStatus", responseWithSupplier.getStatus());
                enhancedOrder.put("originalDeliveryDate", responseWithSupplier.getDeliveryDate());
                enhancedOrder.put("totalQuotationAmount", responseWithSupplier.getTotalAmount());
            }
        } catch (Exception e) {
            enhancedOrder.put("supplierDetailsError", "Error loading supplier details: " + e.getMessage());
        }
        
        // Add project details including project name
        try {
            Project1DTO project = sqsService.getProjectById(order.getProjectId());
            if (project != null) {
                Map<String, Object> projectDetails = new HashMap<>();
                projectDetails.put("projectId", project.getProjectId());
                projectDetails.put("projectName", project.getName());
                projectDetails.put("projectDescription", project.getDescription());
                projectDetails.put("projectLocation", project.getLocation());
                projectDetails.put("projectStatus", project.getStatus());
                projectDetails.put("projectCategory", project.getCategory());
                
                enhancedOrder.put("projectDetails", projectDetails);
                enhancedOrder.put("projectName", project.getName()); // Add project name directly for easy access
            }
        } catch (Exception e) {
            enhancedOrder.put("projectDetailsError", "Error loading project details: " + e.getMessage());
        }
        
        return enhancedOrder;
    }

    /**
     * Get human-readable payment status text
     */
    private String getPaymentStatusText(String paymentStatus) {
        if (paymentStatus == null) return "Unknown";
        
        switch (paymentStatus.toLowerCase()) {
            case "pending": return "Payment Pending";
            case "partial": return "Partially Paid";
            case "paid": return "Fully Paid";
            default: return paymentStatus;
        }
    }
}
