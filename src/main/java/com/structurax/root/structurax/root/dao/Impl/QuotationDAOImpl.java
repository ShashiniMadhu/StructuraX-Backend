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
                quotation.setDate(rs.getDate("date"));
                quotation.setDeadline(rs.getDate("deadline"));
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

    @Override
    public List<QuotationDTO> getQuotationsBySupplierId(Integer supplierId) {
        try {
            // Fixed SQL query to match actual database schema
            String sql = "SELECT DISTINCT q.q_id, q.project_id, " +
                        "p.name as project_name, " +
                        "q.date, q.deadline, q.status " +
                        "FROM quotation q " +
                        "LEFT JOIN project p ON q.project_id = p.project_id " +
                        "JOIN quotation_supplier qs ON q.q_id = qs.q_id " +
                        "WHERE qs.supplier_id = ? " +
                        "ORDER BY q.date DESC";

            return jdbcTemplate.query(sql, new Object[]{supplierId}, (rs, rowNum) -> {
                QuotationDTO quotation = new QuotationDTO();
                quotation.setQId(rs.getInt("q_id"));
                quotation.setProjectId(rs.getString("project_id"));
                quotation.setProjectName(rs.getString("project_name"));
                // Set default values for missing columns
                quotation.setQsId(""); // Not in database, set empty
                quotation.setDate(rs.getDate("date"));
                quotation.setDeadline(rs.getDate("deadline"));
                quotation.setStatus(rs.getString("status"));
                quotation.setDescription(""); // Not in database, set empty
                return quotation;
            });
        } catch (Exception e) {
            // If the above fails, try a simpler query without project join
            try {
                String fallbackSql = "SELECT DISTINCT q.q_id, q.project_id, " +
                                   "q.date, q.deadline, q.status " +
                                   "FROM quotation q " +
                                   "JOIN quotation_supplier qs ON q.q_id = qs.q_id " +
                                   "WHERE qs.supplier_id = ? " +
                                   "ORDER BY q.date DESC";

                return jdbcTemplate.query(fallbackSql, new Object[]{supplierId}, (rs, rowNum) -> {
                    QuotationDTO quotation = new QuotationDTO();
                    quotation.setQId(rs.getInt("q_id"));
                    quotation.setProjectId(rs.getString("project_id"));
                    quotation.setProjectName("Project " + rs.getString("project_id")); // Default name
                    quotation.setQsId(""); // Not in database
                    quotation.setDate(rs.getDate("date"));
                    quotation.setDeadline(rs.getDate("deadline"));
                    quotation.setStatus(rs.getString("status"));
                    quotation.setDescription(""); // Not in database
                    return quotation;
                });
            } catch (Exception fallbackException) {
                // Log the error and return empty list
                System.err.println("Error in getQuotationsBySupplierId: " + fallbackException.getMessage());
                return new java.util.ArrayList<>();
            }
        }
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

        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                QuotationDTO quotation = new QuotationDTO();
                quotation.setQId(rs.getInt("q_id"));
                quotation.setProjectId(rs.getString("project_id"));
                quotation.setProjectName(rs.getString("project_name"));
                quotation.setQsId(rs.getString("qs_id"));
                quotation.setDate(rs.getDate("date"));
                quotation.setDeadline(rs.getDate("deadline"));
                quotation.setStatus(rs.getString("status"));
                quotation.setDescription(rs.getString("description"));
                return quotation;
            });
        } catch (Exception e) {
            System.err.println("Database error in getAllQuotations: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
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
            quotation.setDate(rs.getDate("date"));
            quotation.setDeadline(rs.getDate("deadline"));
            quotation.setStatus(rs.getString("status"));
            quotation.setDescription(rs.getString("description"));
            return quotation;
        });
    }

    @Override
    public List<QuotationItemDTO> getEnhancedQuotationItemsByQuotationId(Integer qId) {
        // Enhanced SQL query to fetch complete item details
        String sql = "SELECT " +
                    "qi.item_id, " +
                    "qi.q_id, " +
                    "qi.name, " +
                    "qi.description, " +
                    "qi.amount, " +
                    "qi.quantity, " +
                    "COALESCE(qi.unit, 'units') as unit, " +
                    "COALESCE(qi.category, 'General') as category, " +
                    "qi.specifications, " +
                    "qi.brand, " +
                    "qi.model, " +
                    "COALESCE(qi.unit_price, qi.amount) as unit_price, " +
                    "CASE " +
                    "  WHEN qi.unit_price IS NOT NULL THEN qi.unit_price * qi.quantity " +
                    "  ELSE qi.amount * qi.quantity " +
                    "END as total_price, " +
                    "qi.item_code, " +
                    "COALESCE(qi.priority, 'MEDIUM') as priority, " +
                    "qi.required_date, " +
                    "qi.notes, " +
                    "COALESCE(qi.status, 'REQUESTED') as status, " +
                    "qi.created_date, " +
                    "qi.updated_date " +
                    "FROM quotation_item qi " +
                    "WHERE qi.q_id = ? " +
                    "ORDER BY qi.item_id";

        try {
            return jdbcTemplate.query(sql, new Object[]{qId}, (rs, rowNum) -> {
                QuotationItemDTO item = new QuotationItemDTO();

                // Set basic fields
                item.setItemId(rs.getInt("item_id"));
                item.setQId(rs.getInt("q_id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setAmount(rs.getBigDecimal("amount"));
                item.setQuantity(rs.getInt("quantity"));

                // Set enhanced fields
                item.setUnit(rs.getString("unit"));
                item.setCategory(rs.getString("category"));
                item.setSpecifications(rs.getString("specifications"));
                item.setBrand(rs.getString("brand"));
                item.setModel(rs.getString("model"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setTotalPrice(rs.getBigDecimal("total_price"));
                item.setItemCode(rs.getString("item_code"));
                item.setPriority(rs.getString("priority"));
                item.setRequiredDate(rs.getDate("required_date"));
                item.setNotes(rs.getString("notes"));
                item.setStatus(rs.getString("status"));
                item.setCreatedDate(rs.getDate("created_date"));
                item.setUpdatedDate(rs.getDate("updated_date"));

                // Calculate total price if not already calculated in SQL
                item.calculateTotalPrice();

                return item;
            });
        } catch (Exception e) {
            // If enhanced query fails, fallback to basic query for backward compatibility
            System.err.println("Enhanced query failed, using fallback: " + e.getMessage());
            return getQuotationItemsByQuotationId(qId);
        }
    }
}
