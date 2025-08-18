package com.structurax.root.structurax.root.dao.Impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.QuotationDAO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationSupplierDTO;

@Repository
public class QuotationDAOImpl implements QuotationDAO {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insertQuotation(QuotationDTO quotation) {
        String sql = "INSERT INTO quotation (project_id, qs_id, date, deadline, status, description) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, quotation.getProjectId());
            ps.setString(2, quotation.getQsId());
            ps.setObject(3, quotation.getDate());
            ps.setObject(4, quotation.getDeadline());
            ps.setString(5, quotation.getStatus());
            ps.setString(6, quotation.getDescription());
            return ps;
        }, keyHolder);
        
        Number key = keyHolder.getKey();
        if (key != null) {
            return key.intValue();
        }
        return null;
    }

    @Override
    public void insertQuotationItem(QuotationItemDTO item) {
        String sql = "INSERT INTO quotation_item (q_id, name, description, amount, quantity) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                item.getQId(),
                item.getName(),
                item.getDescription(),
                item.getAmount(),
                item.getQuantity()
        );
    }

    @Override
    public void insertQuotationSupplier(QuotationSupplierDTO quotationSupplier) {
        String sql = "INSERT INTO quotation_supplier (q_id, supplier_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,
                quotationSupplier.getQId(),
                quotationSupplier.getSupplierId()
        );
    }

    @Override
    public QuotationDTO getQuotationById(Integer qId) {
        try {
            String sql = "SELECT q.q_id, q.project_id, p.name as project_name, q.qs_id, q.date, q.deadline, q.status, q.description " +
                        "FROM quotation q " +
                        "JOIN project p ON q.project_id = p.project_id " +
                        "WHERE q.q_id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{qId}, (rs, rowNum) -> {
                QuotationDTO quotation = new QuotationDTO();
                quotation.setQId(rs.getInt("q_id"));
                quotation.setProjectId(rs.getString("project_id"));
                quotation.setProjectName(rs.getString("project_name"));
                quotation.setQsId(rs.getString("qs_id"));
                quotation.setDate(rs.getObject("date", java.time.LocalDate.class));
                quotation.setDeadline(rs.getObject("deadline", java.time.LocalDate.class));
                quotation.setStatus(rs.getString("status"));
                quotation.setDescription(rs.getString("description"));
                return quotation;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<QuotationItemDTO> getQuotationItemsByQuotationId(Integer qId) {
        String sql = "SELECT item_id, q_id, name, description, amount, quantity FROM quotation_item WHERE q_id = ?";
        return jdbcTemplate.query(sql, new Object[]{qId}, (rs, rowNum) -> {
            QuotationItemDTO item = new QuotationItemDTO();
            item.setItemId(rs.getInt("item_id"));
            item.setQId(rs.getInt("q_id"));
            item.setName(rs.getString("name"));
            item.setDescription(rs.getString("description"));
            item.setAmount(rs.getBigDecimal("amount"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        });
    }

    @Override
    public List<QuotationSupplierDTO> getQuotationSuppliersByQuotationId(Integer qId) {
        String sql = "SELECT qs.q_id, qs.supplier_id, s.supplier_name " +
                     "FROM quotation_supplier qs " +
                     "JOIN supplier s ON qs.supplier_id = s.supplier_id " +
                     "WHERE qs.q_id = ?";
        return jdbcTemplate.query(sql, new Object[]{qId}, (rs, rowNum) -> {
            QuotationSupplierDTO quotationSupplier = new QuotationSupplierDTO();
            quotationSupplier.setQId(rs.getInt("q_id"));
            quotationSupplier.setSupplierId(rs.getInt("supplier_id"));
            quotationSupplier.setSupplierName(rs.getString("supplier_name"));
            return quotationSupplier;
        });
    }

    @Override
    public List<QuotationDTO> getAllQuotations() {
        String sql = "SELECT q.q_id, q.project_id, p.name as project_name, q.qs_id, q.date, q.deadline, q.status, q.description " +
                    "FROM quotation q " +
                    "JOIN project p ON q.project_id = p.project_id " +
                    "ORDER BY q.date DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuotationDTO quotation = new QuotationDTO();
            quotation.setQId(rs.getInt("q_id"));
            quotation.setProjectId(rs.getString("project_id"));
            quotation.setProjectName(rs.getString("project_name"));
            quotation.setQsId(rs.getString("qs_id"));
            quotation.setDate(rs.getObject("date", java.time.LocalDate.class));
            quotation.setDeadline(rs.getObject("deadline", java.time.LocalDate.class));
            quotation.setStatus(rs.getString("status"));
            quotation.setDescription(rs.getString("description"));
            return quotation;
        });
    }

    @Override
    public List<QuotationDTO> getQuotationsByQsId(String qsId) {
        String sql = "SELECT q.q_id, q.project_id, p.name as project_name, q.qs_id, q.date, q.deadline, q.status, q.description " +
                    "FROM quotation q " +
                    "JOIN project p ON q.project_id = p.project_id " +
                    "WHERE q.qs_id = ? ORDER BY q.date DESC";
        return jdbcTemplate.query(sql, new Object[]{qsId}, (rs, rowNum) -> {
            QuotationDTO quotation = new QuotationDTO();
            quotation.setQId(rs.getInt("q_id"));
            quotation.setProjectId(rs.getString("project_id"));
            quotation.setProjectName(rs.getString("project_name"));
            quotation.setQsId(rs.getString("qs_id"));
            quotation.setDate(rs.getObject("date", java.time.LocalDate.class));
            quotation.setDeadline(rs.getObject("deadline", java.time.LocalDate.class));
            quotation.setStatus(rs.getString("status"));
            quotation.setDescription(rs.getString("description"));
            return quotation;
        });
    }

    @Override
    public boolean updateQuotation(QuotationDTO quotation) {
        try {
            String sql = "UPDATE quotation SET project_id = ?, qs_id = ?, date = ?, deadline = ?, status = ?, description = ? WHERE q_id = ?";
            int rowsAffected = jdbcTemplate.update(sql,
                    quotation.getProjectId(),
                    quotation.getQsId(),
                    quotation.getDate(),
                    quotation.getDeadline(),
                    quotation.getStatus(),
                    quotation.getDescription(),
                    quotation.getQId()
            );
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateQuotationItem(QuotationItemDTO item) {
        try {
            String sql = "UPDATE quotation_item SET name = ?, description = ?, amount = ?, quantity = ? WHERE item_id = ?";
            int rowsAffected = jdbcTemplate.update(sql,
                    item.getName(),
                    item.getDescription(),
                    item.getAmount(),
                    item.getQuantity(),
                    item.getItemId()
            );
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateQuotationStatus(Integer qId, String status) {
        try {
            String sql = "UPDATE quotation SET status = ? WHERE q_id = ?";
            int rowsAffected = jdbcTemplate.update(sql, status, qId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteQuotation(Integer qId) {
        try {
            // First delete all items and suppliers associated with this quotation
            deleteQuotationItemsByQuotationId(qId);
            deleteQuotationSuppliersByQuotationId(qId);
            
            // Then delete the quotation
            String sql = "DELETE FROM quotation WHERE q_id = ?";
            int rowsAffected = jdbcTemplate.update(sql, qId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteQuotationItem(Integer itemId) {
        try {
            String sql = "DELETE FROM quotation_item WHERE item_id = ?";
            int rowsAffected = jdbcTemplate.update(sql, itemId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteQuotationItemsByQuotationId(Integer qId) {
        String sql = "DELETE FROM quotation_item WHERE q_id = ?";
        jdbcTemplate.update(sql, qId);
    }

    @Override
    public void deleteQuotationSuppliersByQuotationId(Integer qId) {
        String sql = "DELETE FROM quotation_supplier WHERE q_id = ?";
        jdbcTemplate.update(sql, qId);
    }
}