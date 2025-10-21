package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.FinancialTransactionDAO;
import com.structurax.root.structurax.root.dto.TransactionDTO;
import com.structurax.root.structurax.root.service.FinancialTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialTransactionServiceImpl implements FinancialTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialTransactionServiceImpl.class);

    @Autowired
    private FinancialTransactionDAO financialTransactionDAO;

    @Override
    public List<TransactionDTO> getAllTransactions() {
        logger.info("Fetching all financial transactions");
        List<TransactionDTO> transactions = financialTransactionDAO.getAllTransactions();
        logger.info("Fetched {} transactions", transactions.size());
        return transactions;
    }
}