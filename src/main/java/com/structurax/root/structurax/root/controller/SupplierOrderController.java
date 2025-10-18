package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.ProjectOrderResponseDTO;
import com.structurax.root.structurax.root.dto.ProjectOrdersDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.SupplierOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supplier/orders")
@CrossOrigin(origins = "*")
public class SupplierOrderController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierOrderController.class);

    private final SupplierOrderService supplierOrderService;

    public SupplierOrderController(SupplierOrderService supplierOrderService) {
        this.supplierOrderService = supplierOrderService;
    }


    @GetMapping
    public ResponseEntity<List<PurchaseOrderDTO>> getAllOrders() {
        try {
            logger.info("Fetching all purchase orders");
            List<PurchaseOrderDTO> orders = supplierOrderService.getAllOrders();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching orders: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseOrderDTO> getOrderById(@PathVariable Integer orderId) {
        try {
            logger.info("Fetching order with ID: {}", orderId);
            PurchaseOrderDTO order = supplierOrderService.getOrderById(orderId);
            if (order != null) {
                return new ResponseEntity<>(order, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error fetching order: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrderDTO>> getOrdersBySupplierId(@PathVariable Integer supplierId) {
        try {
            logger.info("Fetching orders for supplier ID: {}", supplierId);
            List<PurchaseOrderDTO> orders = supplierOrderService.getOrdersBySupplierId(supplierId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching orders for supplier: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectOrdersDTO> getOrdersByProjectId(@PathVariable String projectId) {
        try {
            logger.info("Fetching orders for project ID: {}", projectId);
            ProjectOrdersDTO orders = supplierOrderService.getOrdersByProjectId(projectId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching orders for project: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/project/{projectId}/details")
    public ResponseEntity<ProjectOrderResponseDTO> getProjectOrderDetails(@PathVariable String projectId) {
        try {
            logger.info("Fetching project order details for project ID: {}", projectId);
            ProjectOrderResponseDTO response = supplierOrderService.getProjectOrderDetails(projectId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error fetching project order details for project ID {}: {}", projectId, e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Integer orderId, @RequestBody Map<String, Integer> statusUpdate) {
        try {
            Integer newStatus = statusUpdate.get("orderStatus");
            if (newStatus == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            logger.info("Updating order status for order ID: {} to {}", orderId, newStatus);
            supplierOrderService.updateOrderStatus(orderId, newStatus);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating order status: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}