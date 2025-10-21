package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.TransactionDTO;
import java.util.List;

public interface FinancialTransactionDAO {
    List<TransactionDTO> getAllTransactions();
}