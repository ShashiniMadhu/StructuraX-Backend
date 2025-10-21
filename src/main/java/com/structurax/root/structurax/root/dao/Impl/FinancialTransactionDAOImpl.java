package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.FinancialTransactionDAO;
import com.structurax.root.structurax.root.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FinancialTransactionDAOImpl implements FinancialTransactionDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<TransactionDTO> getAllTransactions() {
        List<TransactionDTO> allTransactions = new ArrayList<>();

        // 1. Get Labor Payments
        allTransactions.addAll(getLaborPayments());

        // 2. Get Petty Cash
        allTransactions.addAll(getPettyCash());

        // 3. Get Purchases (from confirmed purchase orders)
        allTransactions.addAll(getPurchases());

        // 4. Get Client Payments (Income)
        allTransactions.addAll(getClientPayments());

        return allTransactions;
    }

    private List<TransactionDTO> getLaborPayments() {
        String sql = """
            SELECT 
                lp.project_id,
                p.name AS project_name,
                lp.date AS transaction_date,
                lp.amount,
                'Labor Payment' AS transaction_type
            FROM labor_payment lp
            INNER JOIN project p ON lp.project_id = p.project_id
            WHERE lp.status = 'paid'
            ORDER BY lp.date DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new TransactionDTO(
                    rs.getString("project_id"),
                    rs.getString("project_name"),
                    rs.getDate("transaction_date").toLocalDate(),
                    rs.getBigDecimal("amount"),
                    rs.getString("transaction_type")
            );
        });
    }

    private List<TransactionDTO> getPettyCash() {
        String sql = """
            SELECT 
                pc.project_id,
                p.name AS project_name,
                pc.date AS transaction_date,
                pc.amount,
                'Petty Cash' AS transaction_type
            FROM petty_cash pc
            INNER JOIN project p ON pc.project_id = p.project_id
            ORDER BY pc.date DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new TransactionDTO(
                    rs.getString("project_id"),
                    rs.getString("project_name"),
                    rs.getDate("transaction_date").toLocalDate(),
                    rs.getBigDecimal("amount"),
                    rs.getString("transaction_type")
            );
        });
    }

    private List<TransactionDTO> getPurchases() {
        String sql = """
            SELECT 
                po.project_id,
                p.name AS project_name,
                po.order_date AS transaction_date,
                qr.total_amount AS amount,
                'Purchase' AS transaction_type
            FROM purchase_order po
            INNER JOIN project p ON po.project_id = p.project_id
            INNER JOIN quotation_response qr ON po.response_id = qr.response_id
            WHERE po.payment_status = 'paid'
            ORDER BY po.order_date DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new TransactionDTO(
                    rs.getString("project_id"),
                    rs.getString("project_name"),
                    rs.getDate("transaction_date").toLocalDate(),
                    rs.getBigDecimal("amount"),
                    rs.getString("transaction_type")
            );
        });
    }

    private List<TransactionDTO> getClientPayments() {
        String sql = """
            SELECT 
                pc.project_id,
                p.name AS project_name,
                pay.paid_date AS transaction_date,
                pc.amount,
                'Client Payment' AS transaction_type
            FROM payment_confirmation pc
            INNER JOIN project p ON pc.project_id = p.project_id
            INNER JOIN payment pay ON pc.payment_id = pay.payment_id
            WHERE pc.status = 'confirmed' 
            AND pay.paid_date IS NOT NULL
            ORDER BY pay.paid_date DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new TransactionDTO(
                    rs.getString("project_id"),
                    rs.getString("project_name"),
                    rs.getDate("transaction_date").toLocalDate(),
                    rs.getBigDecimal("amount"),
                    rs.getString("transaction_type")
            );
        });
    }
}