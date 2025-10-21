package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.SupplierPaymentDTO;
import com.structurax.root.structurax.root.service.SupplierPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supplier/payments")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class SupplierPaymentController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierPaymentController.class);

    private final SupplierPaymentService supplierPaymentService;

    public SupplierPaymentController(SupplierPaymentService supplierPaymentService) {
        this.supplierPaymentService = supplierPaymentService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierPaymentDTO>> getAllPayments() {
        try {
            logger.info("Fetching all supplier payments");
            List<SupplierPaymentDTO> payments = supplierPaymentService.getAllPayments();
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching supplier payments: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{paymentId}/pay")
    public ResponseEntity<Void> markAsPaid(@PathVariable Integer paymentId) {
        try {
            logger.info("Marking supplier payment with ID {} as paid", paymentId);
            supplierPaymentService.markAsPaid(paymentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error marking supplier payment as paid: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
