package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.SupplierInvoiceDTO;
import com.structurax.root.structurax.root.service.SupplierInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/supplier/invoice")
@CrossOrigin(origins = "http://localhost:5173")
public class SupplierInvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierInvoiceController.class);

    private final SupplierInvoiceService invoiceService;

    public SupplierInvoiceController(SupplierInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createInvoice(
            @RequestPart("invoice") SupplierInvoiceDTO invoiceDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Creating invoice for order_id={}", invoiceDTO.getOrderId());
            SupplierInvoiceDTO created = invoiceService.createInvoice(invoiceDTO, file);
            response.put("success", true);
            response.put("invoice", created);
            response.put("message", "Invoice created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating invoice: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating invoice: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInvoices() {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching all invoices");
            List<SupplierInvoiceDTO> invoices = invoiceService.getAllInvoices();
            response.put("success", true);
            response.put("invoices", invoices);
            response.put("totalCount", invoices.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching invoices: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching invoices: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<Map<String, Object>> getInvoiceById(@PathVariable Integer invoiceId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching invoice by id={}", invoiceId);
            SupplierInvoiceDTO invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice == null) {
                response.put("success", false);
                response.put("message", "Invoice not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("success", true);
            response.put("invoice", invoice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching invoice: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching invoice: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Map<String, Object>> getInvoicesBySupplierId(@PathVariable Integer supplierId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching invoices for supplier_id={}", supplierId);
            List<SupplierInvoiceDTO> invoices = invoiceService.getInvoicesBySupplierId(supplierId);
            response.put("success", true);
            response.put("invoices", invoices);
            response.put("totalCount", invoices.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching invoices for supplier: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching invoices: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getInvoicesByOrderId(@PathVariable Integer orderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching invoices for order_id={}", orderId);
            List<SupplierInvoiceDTO> invoices = invoiceService.getInvoicesByOrderId(orderId);
            response.put("success", true);
            response.put("invoices", invoices);
            response.put("totalCount", invoices.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching invoices for order: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching invoices: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{invoiceId}/status")
    public ResponseEntity<Map<String, Object>> updateInvoiceStatus(
            @PathVariable Integer invoiceId,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating invoice status for invoice_id={} to {}", invoiceId, status);
            invoiceService.updateInvoiceStatus(invoiceId, status);
            response.put("success", true);
            response.put("message", "Invoice status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating invoice status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating invoice status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
