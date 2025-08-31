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

import com.structurax.root.structurax.root.dao.QuotationResponseDAO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;

@Repository
public class QuotationResponseDAOImpl implements QuotationResponseDAO {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insertQuotationResponse(QuotationResponseDTO response) {
        String sql = "INSERT INTO quotation_response (q_id, supplier_id, total_amount, delivery_date, additional_note, respond_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, response.getQId());
            ps.setInt(2, response.getSupplierId());
            ps.setBigDecimal(3, response.getTotalAmount());
            ps.setObject(4, response.getDeliveryDate());
            ps.setString(5, response.getAdditionalNote());
            ps.setObject(6, response.getRespondDate());
            ps.setString(7, response.getStatus());
            return ps;
        }, keyHolder);
        
        Number key = keyHolder.getKey();
        if (key != null) {
            return key.intValue();
        }
        return null;
    }

    @Override
    public QuotationResponseDTO getQuotationResponseById(Integer responseId) {
        try {
            String sql = "SELECT response_id, q_id, supplier_id, total_amount, delivery_date, additional_note, respond_date, status " +
                        "FROM quotation_response WHERE response_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                QuotationResponseDTO response = new QuotationResponseDTO();
                response.setResponseId(rs.getInt("response_id"));
                response.setQId(rs.getInt("q_id"));
                response.setSupplierId(rs.getInt("supplier_id"));
                response.setTotalAmount(rs.getBigDecimal("total_amount"));
                response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
                response.setAdditionalNote(rs.getString("additional_note"));
                response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
                response.setStatus(rs.getString("status"));
                return response;
            }, responseId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<QuotationResponseDTO> getQuotationResponsesByQuotationId(Integer qId) {
        String sql = "SELECT response_id, q_id, supplier_id, total_amount, delivery_date, additional_note, respond_date, status " +
                    "FROM quotation_response WHERE q_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuotationResponseDTO response = new QuotationResponseDTO();
            response.setResponseId(rs.getInt("response_id"));
            response.setQId(rs.getInt("q_id"));
            response.setSupplierId(rs.getInt("supplier_id"));
            response.setTotalAmount(rs.getBigDecimal("total_amount"));
            response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
            response.setAdditionalNote(rs.getString("additional_note"));
            response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
            response.setStatus(rs.getString("status"));
            return response;
        }, qId);
    }

    @Override
    public List<QuotationResponseDTO> getQuotationResponsesBySupplierId(Integer supplierId) {
        String sql = "SELECT response_id, q_id, supplier_id, total_amount, delivery_date, additional_note, respond_date, status " +
                    "FROM quotation_response WHERE supplier_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuotationResponseDTO response = new QuotationResponseDTO();
            response.setResponseId(rs.getInt("response_id"));
            response.setQId(rs.getInt("q_id"));
            response.setSupplierId(rs.getInt("supplier_id"));
            response.setTotalAmount(rs.getBigDecimal("total_amount"));
            response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
            response.setAdditionalNote(rs.getString("additional_note"));
            response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
            response.setStatus(rs.getString("status"));
            return response;
        }, supplierId);
    }

    @Override
    public List<QuotationResponseDTO> getAllQuotationResponses() {
        String sql = "SELECT response_id, q_id, supplier_id, total_amount, delivery_date, additional_note, respond_date, status " +
                    "FROM quotation_response";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuotationResponseDTO response = new QuotationResponseDTO();
            response.setResponseId(rs.getInt("response_id"));
            response.setQId(rs.getInt("q_id"));
            response.setSupplierId(rs.getInt("supplier_id"));
            response.setTotalAmount(rs.getBigDecimal("total_amount"));
            response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
            response.setAdditionalNote(rs.getString("additional_note"));
            response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
            response.setStatus(rs.getString("status"));
            return response;
        });
    }

    @Override
    public QuotationResponseWithSupplierDTO getQuotationResponseWithSupplierById(Integer responseId) {
        try {
            String sql = "SELECT qr.response_id, qr.q_id, qr.supplier_id, s.supplier_name, s.email, s.phone, s.address, " +
                        "qr.total_amount, qr.delivery_date, qr.additional_note, qr.respond_date, qr.status " +
                        "FROM quotation_response qr " +
                        "JOIN supplier s ON qr.supplier_id = s.supplier_id " +
                        "WHERE qr.response_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                QuotationResponseWithSupplierDTO response = new QuotationResponseWithSupplierDTO();
                response.setResponseId(rs.getInt("response_id"));
                response.setQId(rs.getInt("q_id"));
                response.setSupplierId(rs.getInt("supplier_id"));
                response.setSupplierName(rs.getString("supplier_name"));
                response.setSupplierEmail(rs.getString("email"));
                response.setSupplierPhone(rs.getString("phone"));
                response.setSupplierAddress(rs.getString("address"));
                response.setTotalAmount(rs.getBigDecimal("total_amount"));
                response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
                response.setAdditionalNote(rs.getString("additional_note"));
                response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
                response.setStatus(rs.getString("status"));
                return response;
            }, responseId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<QuotationResponseWithSupplierDTO> getQuotationResponsesWithSupplierByQuotationId(Integer qId) {
        String sql = "SELECT qr.response_id, qr.q_id, qr.supplier_id, " +
                    "COALESCE(s.supplier_name, 'Unknown Supplier') as supplier_name, " +
                    "COALESCE(s.email, 'No Email') as email, " +
                    "COALESCE(s.phone, 'No Phone') as phone, " +
                    "COALESCE(s.address, 'No Address') as address, " +
                    "qr.total_amount, qr.delivery_date, qr.additional_note, qr.respond_date, qr.status " +
                    "FROM quotation_response qr " +
                    "LEFT JOIN supplier s ON qr.supplier_id = s.supplier_id " +
                    "WHERE qr.q_id = ? " +
                    "ORDER BY qr.respond_date DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuotationResponseWithSupplierDTO response = new QuotationResponseWithSupplierDTO();
            response.setResponseId(rs.getInt("response_id"));
            response.setQId(rs.getInt("q_id"));
            response.setSupplierId(rs.getInt("supplier_id"));
            response.setSupplierName(rs.getString("supplier_name"));
            response.setSupplierEmail(rs.getString("email"));
            response.setSupplierPhone(rs.getString("phone"));
            response.setSupplierAddress(rs.getString("address"));
            response.setTotalAmount(rs.getBigDecimal("total_amount"));
            response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
            response.setAdditionalNote(rs.getString("additional_note"));
            response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
            response.setStatus(rs.getString("status"));
            return response;
        }, qId);
    }

    @Override
    public List<QuotationResponseWithSupplierDTO> getQuotationResponsesWithSupplierBySupplierId(Integer supplierId) {
        String sql = "SELECT qr.response_id, qr.q_id, qr.supplier_id, s.supplier_name, s.email, s.phone, s.address, " +
                    "qr.total_amount, qr.delivery_date, qr.additional_note, qr.respond_date, qr.status " +
                    "FROM quotation_response qr " +
                    "JOIN supplier s ON qr.supplier_id = s.supplier_id " +
                    "WHERE qr.supplier_id = ? " +
                    "ORDER BY qr.respond_date DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuotationResponseWithSupplierDTO response = new QuotationResponseWithSupplierDTO();
            response.setResponseId(rs.getInt("response_id"));
            response.setQId(rs.getInt("q_id"));
            response.setSupplierId(rs.getInt("supplier_id"));
            response.setSupplierName(rs.getString("supplier_name"));
            response.setSupplierEmail(rs.getString("email"));
            response.setSupplierPhone(rs.getString("phone"));
            response.setSupplierAddress(rs.getString("address"));
            response.setTotalAmount(rs.getBigDecimal("total_amount"));
            response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
            response.setAdditionalNote(rs.getString("additional_note"));
            response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
            response.setStatus(rs.getString("status"));
            return response;
        }, supplierId);
    }

    @Override
    public List<QuotationResponseWithSupplierDTO> getAllQuotationResponsesWithSupplier() {
        String sql = "SELECT qr.response_id, qr.q_id, qr.supplier_id, " +
                    "COALESCE(s.supplier_name, 'Unknown Supplier') as supplier_name, " +
                    "COALESCE(s.email, 'No Email') as email, " +
                    "COALESCE(s.phone, 'No Phone') as phone, " +
                    "COALESCE(s.address, 'No Address') as address, " +
                    "qr.total_amount, qr.delivery_date, qr.additional_note, qr.respond_date, qr.status " +
                    "FROM quotation_response qr " +
                    "LEFT JOIN supplier s ON qr.supplier_id = s.supplier_id " +
                    "ORDER BY qr.respond_date DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            QuotationResponseWithSupplierDTO response = new QuotationResponseWithSupplierDTO();
            response.setResponseId(rs.getInt("response_id"));
            response.setQId(rs.getInt("q_id"));
            response.setSupplierId(rs.getInt("supplier_id"));
            response.setSupplierName(rs.getString("supplier_name"));
            response.setSupplierEmail(rs.getString("email"));
            response.setSupplierPhone(rs.getString("phone"));
            response.setSupplierAddress(rs.getString("address"));
            response.setTotalAmount(rs.getBigDecimal("total_amount"));
            response.setDeliveryDate(rs.getObject("delivery_date", java.time.LocalDate.class));
            response.setAdditionalNote(rs.getString("additional_note"));
            response.setRespondDate(rs.getObject("respond_date", java.time.LocalDate.class));
            response.setStatus(rs.getString("status"));
            return response;
        });
    }

    @Override
    public boolean updateQuotationResponse(QuotationResponseDTO response) {
        String sql = "UPDATE quotation_response SET q_id = ?, supplier_id = ?, total_amount = ?, delivery_date = ?, " +
                    "additional_note = ?, respond_date = ?, status = ? WHERE response_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
                response.getQId(),
                response.getSupplierId(),
                response.getTotalAmount(),
                response.getDeliveryDate(),
                response.getAdditionalNote(),
                response.getRespondDate(),
                response.getStatus(),
                response.getResponseId()
        );
        return rowsAffected > 0;
    }

    @Override
    public boolean updateQuotationResponseStatus(Integer responseId, String status) {
        String sql = "UPDATE quotation_response SET status = ? WHERE response_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, status, responseId);
        return rowsAffected > 0;
    }

    @Override
    public boolean deleteQuotationResponse(Integer responseId) {
        String sql = "DELETE FROM quotation_response WHERE response_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, responseId);
        return rowsAffected > 0;
    }

    @Override
    public void deleteQuotationResponsesByQuotationId(Integer qId) {
        String sql = "DELETE FROM quotation_response WHERE q_id = ?";
        jdbcTemplate.update(sql, qId);
    }
}
