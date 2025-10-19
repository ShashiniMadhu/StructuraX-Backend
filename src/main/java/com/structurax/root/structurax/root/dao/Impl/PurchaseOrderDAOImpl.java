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

import com.structurax.root.structurax.root.dao.PurchaseOrderDAO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;

@Repository
public class PurchaseOrderDAOImpl implements PurchaseOrderDAO {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Integer insertPurchaseOrder(PurchaseOrderDTO purchaseOrder) {
        String sql = "INSERT INTO purchase_order (project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, purchaseOrder.getProjectId());
                ps.setInt(2, purchaseOrder.getSupplierId());
                ps.setInt(3, purchaseOrder.getResponseId());
                ps.setString(4, purchaseOrder.getPaymentStatus());
                ps.setObject(5, purchaseOrder.getEstimatedDeliveryDate());
                ps.setObject(6, purchaseOrder.getOrderDate());
                ps.setBoolean(7, purchaseOrder.getOrderStatus() != null ? purchaseOrder.getOrderStatus() : false);
                return ps;
            }, keyHolder);
            
            Number key = keyHolder.getKey();
            if (key != null) {
                return key.intValue();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error inserting purchase order: " + e.getMessage() + 
                " | project_id=" + purchaseOrder.getProjectId() + 
                ", supplier_id=" + purchaseOrder.getSupplierId() + 
                ", response_id=" + purchaseOrder.getResponseId(), e);
        }
    }

    @Override
    public boolean insertPurchaseOrderItems(Integer orderId, List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return true; // No items to insert
        }
        
        String sql = "INSERT INTO order_item (order_id, item_id, description, unit_price, quantity) VALUES (?, ?, ?, ?, ?)";
        
        try {
            for (OrderItemDTO item : items) {
                jdbcTemplate.update(sql, 
                    orderId,
                    item.getItemId(), // Can be null for quotation-based items
                    item.getDescription(),
                    item.getUnitPrice(),
                    item.getQuantity()
                );
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error inserting purchase order items: " + e.getMessage() + 
                " | orderId=" + orderId + 
                ", itemCount=" + items.size() + 
                ", firstItemDescription=" + (items.get(0) != null ? items.get(0).getDescription() : "null"), e);
        }
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderById(Integer orderId) {
        try {
            String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status " +
                        "FROM purchase_order WHERE order_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                PurchaseOrderDTO order = new PurchaseOrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setProjectId(rs.getString("project_id"));
                order.setSupplierId(rs.getInt("supplier_id"));
                order.setResponseId(rs.getInt("response_id"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setEstimatedDeliveryDate(rs.getObject("estimated_delivery_date", java.time.LocalDate.class));
                order.setOrderDate(rs.getObject("order_date", java.time.LocalDate.class));
                order.setOrderStatus(rs.getBoolean("order_status"));
                return order;
            }, orderId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersByProjectId(String projectId) {
        String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status " +
                    "FROM purchase_order WHERE project_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PurchaseOrderDTO order = new PurchaseOrderDTO();
            order.setOrderId(rs.getInt("order_id"));
            order.setProjectId(rs.getString("project_id"));
            order.setSupplierId(rs.getInt("supplier_id"));
            order.setResponseId(rs.getInt("response_id"));
            order.setPaymentStatus(rs.getString("payment_status"));
            order.setEstimatedDeliveryDate(rs.getObject("estimated_delivery_date", java.time.LocalDate.class));
            order.setOrderDate(rs.getObject("order_date", java.time.LocalDate.class));
            order.setOrderStatus(rs.getBoolean("order_status"));
            return order;
        }, projectId);
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersBySupplierId(Integer supplierId) {
        String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status " +
                    "FROM purchase_order WHERE supplier_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PurchaseOrderDTO order = new PurchaseOrderDTO();
            order.setOrderId(rs.getInt("order_id"));
            order.setProjectId(rs.getString("project_id"));
            order.setSupplierId(rs.getInt("supplier_id"));
            order.setResponseId(rs.getInt("response_id"));
            order.setPaymentStatus(rs.getString("payment_status"));
            order.setEstimatedDeliveryDate(rs.getObject("estimated_delivery_date", java.time.LocalDate.class));
            order.setOrderDate(rs.getObject("order_date", java.time.LocalDate.class));
            order.setOrderStatus(rs.getBoolean("order_status"));
            return order;
        }, supplierId);
    }

    @Override
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status " +
                    "FROM purchase_order";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PurchaseOrderDTO order = new PurchaseOrderDTO();
            order.setOrderId(rs.getInt("order_id"));
            order.setProjectId(rs.getString("project_id"));
            order.setSupplierId(rs.getInt("supplier_id"));
            order.setResponseId(rs.getInt("response_id"));
            order.setPaymentStatus(rs.getString("payment_status"));
            order.setEstimatedDeliveryDate(rs.getObject("estimated_delivery_date", java.time.LocalDate.class));
            order.setOrderDate(rs.getObject("order_date", java.time.LocalDate.class));
            order.setOrderStatus(rs.getBoolean("order_status"));
            return order;
        });
    }

    @Override
    public List<PurchaseOrderDTO> getPurchaseOrdersByQsId(String qsId) {
        String sql = "SELECT po.order_id, po.project_id, po.supplier_id, po.response_id, po.payment_status, " +
                    "po.estimated_delivery_date, po.order_date, po.order_status " +
                    "FROM purchase_order po " +
                    "INNER JOIN quotation_response qr ON po.response_id = qr.response_id " +
                    "INNER JOIN quotation q ON qr.q_id = q.q_id " +
                    "WHERE q.qs_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PurchaseOrderDTO order = new PurchaseOrderDTO();
            order.setOrderId(rs.getInt("order_id"));
            order.setProjectId(rs.getString("project_id"));
            order.setSupplierId(rs.getInt("supplier_id"));
            order.setResponseId(rs.getInt("response_id"));
            order.setPaymentStatus(rs.getString("payment_status"));
            order.setEstimatedDeliveryDate(rs.getObject("estimated_delivery_date", java.time.LocalDate.class));
            order.setOrderDate(rs.getObject("order_date", java.time.LocalDate.class));
            order.setOrderStatus(rs.getBoolean("order_status"));
            return order;
        }, qsId);
    }

    @Override
    public List<OrderItemDTO> getPurchaseOrderItemsByOrderId(Integer orderId) {
        String sql = "SELECT order_id, item_id, description, unit_price, quantity " +
                    "FROM order_item WHERE order_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OrderItemDTO item = new OrderItemDTO();
            item.setOrderId(rs.getInt("order_id"));
            item.setItemId(rs.getInt("item_id"));
            item.setDescription(rs.getString("description"));
            item.setUnitPrice(rs.getBigDecimal("unit_price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        }, orderId);
    }

    @Override
    public boolean updatePurchaseOrder(PurchaseOrderDTO purchaseOrder) {
        String sql = "UPDATE purchase_order SET project_id = ?, supplier_id = ?, response_id = ?, payment_status = ?, " +
                    "estimated_delivery_date = ?, order_date = ?, order_status = ? WHERE order_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql,
                purchaseOrder.getProjectId(),
                purchaseOrder.getSupplierId(),
                purchaseOrder.getResponseId(),
                purchaseOrder.getPaymentStatus(),
                purchaseOrder.getEstimatedDeliveryDate(),
                purchaseOrder.getOrderDate(),
                purchaseOrder.getOrderStatus(),
                purchaseOrder.getOrderId()
            );
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updatePurchaseOrderStatus(Integer orderId, Boolean orderStatus) {
        String sql = "UPDATE purchase_order SET order_status = ? WHERE order_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, orderStatus, orderId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updatePaymentStatus(Integer orderId, String paymentStatus) {
        String sql = "UPDATE purchase_order SET payment_status = ? WHERE order_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, paymentStatus, orderId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deletePurchaseOrder(Integer orderId) {
        String sql = "DELETE FROM purchase_order WHERE order_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, orderId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deletePurchaseOrderItems(Integer orderId) {
        String sql = "DELETE FROM order_item WHERE order_id = ?";
        try {
            jdbcTemplate.update(sql, orderId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
