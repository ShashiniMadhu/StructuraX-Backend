package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.SupplierOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier/orders")
@CrossOrigin(origins = "*")
public class SupplierOrderController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierOrderController.class);

    @Autowired
    private SupplierOrderService supplierOrderService;


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
    public ResponseEntity<List<PurchaseOrderDTO>> getOrdersByProjectId(@PathVariable String projectId) {
        try {
            logger.info("Fetching orders for project ID: {}", projectId);
            List<PurchaseOrderDTO> orders = supplierOrderService.getOrdersByProjectId(projectId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching orders for project: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }








}
