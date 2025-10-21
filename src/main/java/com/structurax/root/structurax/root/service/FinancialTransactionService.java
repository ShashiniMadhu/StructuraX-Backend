package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.TransactionDTO;
import java.util.List;

public interface FinancialTransactionService {
    List<TransactionDTO> getAllTransactions();
}