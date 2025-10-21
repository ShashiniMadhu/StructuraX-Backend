package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.Constants.Constants;
import com.structurax.root.structurax.root.dto.TransactionDTO;
import com.structurax.root.structurax.root.service.FinancialTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping(value = "/transactions")
public class FinancialTransactionController {

    @Autowired
    private FinancialTransactionService financialTransactionService;

    @GetMapping(value = "/all", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getAllTransactions() {
        try {
            log.info("Request received to fetch all financial transactions");
            List<TransactionDTO> transactions = financialTransactionService.getAllTransactions();

            if (transactions.isEmpty()) {
                log.warn("No transactions found");
                return ResponseEntity.ok(transactions);
            }

            log.info("Successfully retrieved {} transactions", transactions.size());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage());
            return new ResponseEntity<>("Error fetching transactions: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}