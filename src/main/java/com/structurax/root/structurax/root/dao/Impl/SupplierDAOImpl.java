package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.dto.SupplierPaymentDTO;
import com.structurax.root.structurax.root.dto.SupplierHistoryDTO;
import com.structurax.root.structurax.root.dto.SupplierInvoiceDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SupplierDAOImpl implements SupplierDAO {

    private static final Logger logger = LoggerFactory.getLogger(SupplierDAOImpl.class);

    private final DatabaseConnection databaseConnection;
    private final JdbcTemplate jdbcTemplate;

    public SupplierDAOImpl(DatabaseConnection databaseConnection, JdbcTemplate jdbcTemplate) {
        this.databaseConnection = databaseConnection;
        this.jdbcTemplate = jdbcTemplate;
    }

    // ========== EXISTING SUPPLIER METHODS ==========

    @Override
    public Optional<SupplierDTO> findByEmail(String email) {
        String sql = "SELECT * FROM supplier WHERE email = ?";
        try {
            SupplierDTO supplier = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SupplierDTO.class), email);
            return Optional.ofNullable(supplier);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public SupplierDTO getSupplierById(Integer supplierId) {
        String sql = "SELECT * FROM supplier WHERE supplier_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SupplierDTO.class), supplierId);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("No supplier found with supplier_id: {}", supplierId);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving supplier by supplier_id {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving supplier by supplier_id: " + e.getMessage(), e);
        }
    }



    @Override
    public CatalogDTO createCatalog(CatalogDTO catalogDTO) {
        String sql;
        if (catalogDTO.getItemId() != null) {
            sql = "INSERT INTO catalog(item_id, name, description, rate, availability, category) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO catalog(name, description, rate, availability, category) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int paramIndex = 1;

            if (catalogDTO.getItemId() != null) {
                ps.setInt(paramIndex++, catalogDTO.getItemId());
            }

            ps.setString(paramIndex++, catalogDTO.getName());
            ps.setString(paramIndex++, catalogDTO.getDescription());
            ps.setFloat(paramIndex++, catalogDTO.getRate());
            ps.setBoolean(paramIndex++, catalogDTO.getAvailability());
            ps.setString(paramIndex, catalogDTO.getCategory());

            int rowsAffected = ps.executeUpdate();
            logger.info("Catalog created successfully. Rows affected: {}", rowsAffected);

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                if (catalogDTO.getItemId() == null) {
                    catalogDTO.setItemId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating catalog: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating catalog: " + e.getMessage(), e);
        }
        return catalogDTO;
    }

    @Override
    public List<CatalogDTO> getAllCatalogs() {
        String sql = "SELECT item_id, name, description, rate, availability, category FROM catalog";
        List<CatalogDTO> catalogs = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CatalogDTO catalog = new CatalogDTO();
                catalog.setItemId(rs.getInt("item_id"));
                catalog.setName(rs.getString("name"));
                catalog.setDescription(rs.getString("description"));
                catalog.setRate(rs.getFloat("rate"));
                catalog.setAvailability(rs.getBoolean("availability"));
                catalog.setCategory(rs.getString("category"));
                catalogs.add(catalog);
            }
            logger.info("Retrieved {} catalogs from database", catalogs.size());
        } catch (SQLException e) {
            logger.error("Error retrieving catalogs: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving catalogs: " + e.getMessage(), e);
        }
        return catalogs;
    }

    @Override
    public void deleteCatalog(Integer itemId) {
        String sql = "DELETE FROM catalog WHERE item_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No catalog found with item_id: {}", itemId);
                throw new RuntimeException("No catalog found with item_id: " + itemId);
            }
            logger.info("Catalog with item_id {} deleted successfully", itemId);
        } catch (SQLException e) {
            logger.error("Error deleting catalog with item_id {}: {}", itemId, e.getMessage(), e);
            throw new RuntimeException("Error deleting catalog: " + e.getMessage(), e);
        }
    }

    @Override
    public CatalogDTO getCatalogById(Integer itemId) {
        String sql = "SELECT item_id, name, description, rate, availability, category FROM catalog WHERE item_id = ?";
        CatalogDTO catalog = null;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                catalog = new CatalogDTO();
                catalog.setItemId(rs.getInt("item_id"));
                catalog.setName(rs.getString("name"));
                catalog.setDescription(rs.getString("description"));
                catalog.setRate(rs.getFloat("rate"));
                catalog.setAvailability(rs.getBoolean("availability"));
                catalog.setCategory(rs.getString("category"));
                logger.info("Retrieved catalog with item_id: {}", itemId);
            } else {
                logger.warn("No catalog found with item_id: {}", itemId);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving catalog by item_id {}: {}", itemId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving catalog by item_id: " + e.getMessage(), e);
        }
        return catalog;
    }



    @Override
    public List<PurchaseOrderDTO> getAllOrders() {
        String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status FROM purchase_order";
        List<PurchaseOrderDTO> orders = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToPurchaseOrderDTO(rs));
            }
            logger.info("Retrieved {} purchase orders from database", orders.size());
        } catch (SQLException e) {
            logger.error("Error retrieving purchase orders: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving purchase orders: " + e.getMessage(), e);
        }
        return orders;
    }

    @Override
    public PurchaseOrderDTO getOrderById(Integer orderId) {
        String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status FROM purchase_order WHERE order_id = ?";
        PurchaseOrderDTO order = null;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                order = mapResultSetToPurchaseOrderDTO(rs);
                logger.info("Retrieved purchase order with order_id: {}", orderId);
            } else {
                logger.warn("No purchase order found with order_id: {}", orderId);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving purchase order by order_id {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving purchase order by order_id: " + e.getMessage(), e);
        }
        return order;
    }

    @Override
    public List<PurchaseOrderDTO> getOrdersBySupplierId(Integer supplierId) {
        String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status FROM purchase_order WHERE supplier_id = ?";
        List<PurchaseOrderDTO> orders = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToPurchaseOrderDTO(rs));
            }
            logger.info("Retrieved {} purchase orders for supplier_id: {}", orders.size(), supplierId);
        } catch (SQLException e) {
            logger.error("Error retrieving purchase orders for supplier_id {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving purchase orders for supplier: " + e.getMessage(), e);
        }
        return orders;
    }

    @Override
    public List<PurchaseOrderDTO> getOrdersByProjectId(String projectId) {
        String sql = "SELECT order_id, project_id, supplier_id, response_id, payment_status, estimated_delivery_date, order_date, order_status FROM purchase_order WHERE project_id = ?";
        List<PurchaseOrderDTO> orders = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToPurchaseOrderDTO(rs));
            }
            logger.info("Retrieved {} purchase orders for project_id: {}", orders.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving purchase orders for project_id {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving purchase orders for project: " + e.getMessage(), e);
        }
        return orders;
    }

    private PurchaseOrderDTO mapResultSetToPurchaseOrderDTO(ResultSet rs) throws SQLException {
        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setOrderId(rs.getInt("order_id"));
        order.setProjectId(rs.getString("project_id"));
        order.setSupplierId(rs.getInt("supplier_id"));
        order.setResponseId((Integer) rs.getObject("response_id"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setEstimatedDeliveryDate(rs.getDate("estimated_delivery_date") != null ? rs.getDate("estimated_delivery_date").toLocalDate() : null);
        order.setOrderDate(rs.getDate("order_date") != null ? rs.getDate("order_date").toLocalDate() : null);
        order.setOrderStatus(rs.getBoolean("order_status"));
        return order;
    }
    @Override
    public ProjectDTO getProjectById(String projectId) {
        String sql = "SELECT project_id, name FROM project WHERE project_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{projectId}, (rs, rowNum) -> {
                ProjectDTO project = new ProjectDTO();
                project.setProjectId(rs.getString("project_id"));
                project.setName(rs.getString("name"));
                return project;
            });
        } catch (EmptyResultDataAccessException e) {
            logger.error("No project found with project_id: {}", projectId);
            throw new RuntimeException("Project not found with ID: " + projectId);
        }
    }


    @Override
    public PurchaseOrderDTO getOrderByProjectId(String projectId) {
        String sql = "SELECT order_id, project_id, order_date, order_status FROM purchase_order WHERE project_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{projectId}, (rs, rowNum) -> {
                PurchaseOrderDTO order = new PurchaseOrderDTO();
                order.setOrderId(rs.getInt("order_id"));
                order.setProjectId(rs.getString("project_id"));
                order.setOrderDate(rs.getDate("order_date").toLocalDate());
                order.setStatus(rs.getString("order_status"));
                return order;
            });
        } catch (EmptyResultDataAccessException e) {
            logger.error("No purchase order found for project_id: {}", projectId);
            throw new RuntimeException("Purchase order not found for project ID: " + projectId);
        }
    }

    @Override
    public BigDecimal getQuotationAmountByResponseId(Integer responseId) {
        String sql = "SELECT total_amount FROM quotation_response WHERE response_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{responseId}, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            logger.error("No quotation response found with response_id: {}", responseId);
            throw new RuntimeException("Quotation response not found with ID: " + responseId);
        }
    }


    @Override
    public List<OrderItemDTO> getOrderItemsByOrderId(Integer orderId) {
        String sql = "SELECT item_id, order_id, description, quantity, unit_price FROM order_items WHERE order_id = ?";
        return jdbcTemplate.query(sql, new Object[]{orderId}, (rs, rowNum) -> {
            OrderItemDTO item = new OrderItemDTO();
            item.setItemId(rs.getInt("item_id"));
            item.setOrderId(rs.getInt("order_id"));
            item.setDescription(rs.getString("description"));  // Changed from setProductName
            item.setQuantity(rs.getInt("quantity"));
            item.setUnitPrice(rs.getBigDecimal("unit_price"));  // Changed from setPrice
            return item;
        });
    }

    @Override
    public void updateOrderStatus(Integer orderId, Integer orderStatus) {
        String sql = "UPDATE purchase_order SET order_status = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, orderStatus, orderId);
    }

    @Override
    public List<SupplierPaymentDTO> getAllSupplierPayments() {
        String sql = "SELECT sp.supplier_payment_id, sp.project_id, p.name AS project_name, sp.invoice_id, oi.date AS invoice_date, sp.due_date, sp.payed_date, sp.amount, sp.status " +
                "FROM supplier_payment sp " +
                "LEFT JOIN project p ON sp.project_id = p.project_id " +
                "LEFT JOIN order_invoice oi ON sp.invoice_id = oi.invoice_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            SupplierPaymentDTO payment = new SupplierPaymentDTO();
            payment.setSupplierPaymentId(rs.getInt("supplier_payment_id"));
            payment.setProjectId(rs.getString("project_id"));
            payment.setProjectName(rs.getString("project_name"));
            payment.setInvoiceId(rs.getInt("invoice_id"));
            Date invoiceDate = rs.getDate("invoice_date");
            if (invoiceDate != null) {
                payment.setInvoiceDate(invoiceDate.toLocalDate());
            }
            Date dueDate = rs.getDate("due_date");
            if (dueDate != null) {
                payment.setDueDate(dueDate.toLocalDate());
            }
            Date payedDate = rs.getDate("payed_date");
            if (payedDate != null) {
                payment.setPayedDate(payedDate.toLocalDate());
            }
            payment.setAmount(rs.getBigDecimal("amount"));
            payment.setStatus(rs.getString("status"));
            return payment;
        });
    }

    @Override
    public void updatePaymentStatusToPaid(Integer paymentId) {
        String sql = "UPDATE supplier_payment SET status = 'Paid', payed_date = ? WHERE supplier_payment_id = ?";
        jdbcTemplate.update(sql, Date.valueOf(LocalDate.now()), paymentId);
    }

    // ========== SUPPLIER HISTORY METHODS ==========

    @Override
    public List<SupplierHistoryDTO> getAllSupplierHistory() {
        String sql = "SELECT history_id, supplier_id, order_id, supply_date, amount FROM supplier_history";
        List<SupplierHistoryDTO> histories = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SupplierHistoryDTO h = new SupplierHistoryDTO();
                h.setHistoryId(rs.getInt("history_id"));
                h.setSupplierId(rs.getInt("supplier_id"));
                h.setOrderId(rs.getInt("order_id"));
                Date d = rs.getDate("supply_date");
                h.setSupplyDate(d);
                h.setAmount(rs.getBigDecimal("amount"));
                histories.add(h);
            }
            logger.info("Retrieved {} supplier history records", histories.size());
        } catch (SQLException e) {
            logger.error("Error retrieving supplier history: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving supplier history: " + e.getMessage(), e);
        }
        return histories;
    }

    @Override
    public SupplierHistoryDTO getSupplierHistoryById(Integer historyId) {
        String sql = "SELECT history_id, supplier_id, order_id, supply_date, amount FROM supplier_history WHERE history_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, historyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SupplierHistoryDTO h = new SupplierHistoryDTO();
                h.setHistoryId(rs.getInt("history_id"));
                h.setSupplierId(rs.getInt("supplier_id"));
                h.setOrderId(rs.getInt("order_id"));
                h.setSupplyDate(rs.getDate("supply_date"));
                h.setAmount(rs.getBigDecimal("amount"));
                return h;
            } else {
                logger.warn("No supplier history found with history_id: {}", historyId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving supplier history by id {}: {}", historyId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving supplier history: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SupplierHistoryDTO> getSupplierHistoryBySupplierId(Integer supplierId) {
        String sql = "SELECT history_id, supplier_id, order_id, supply_date, amount FROM supplier_history WHERE supplier_id = ?";
        List<SupplierHistoryDTO> histories = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SupplierHistoryDTO h = new SupplierHistoryDTO();
                h.setHistoryId(rs.getInt("history_id"));
                h.setSupplierId(rs.getInt("supplier_id"));
                h.setOrderId(rs.getInt("order_id"));
                h.setSupplyDate(rs.getDate("supply_date"));
                h.setAmount(rs.getBigDecimal("amount"));
                histories.add(h);
            }
            logger.info("Retrieved {} supplier history records for supplier_id: {}", histories.size(), supplierId);
        } catch (SQLException e) {
            logger.error("Error retrieving supplier history for supplier {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving supplier history: " + e.getMessage(), e);
        }
        return histories;
    }

    @Override
    public List<SupplierHistoryDTO> getSupplierHistoryByOrderId(Integer orderId) {
        String sql = "SELECT history_id, supplier_id, order_id, supply_date, amount FROM supplier_history WHERE order_id = ?";
        List<SupplierHistoryDTO> histories = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SupplierHistoryDTO h = new SupplierHistoryDTO();
                h.setHistoryId(rs.getInt("history_id"));
                h.setSupplierId(rs.getInt("supplier_id"));
                h.setOrderId(rs.getInt("order_id"));
                h.setSupplyDate(rs.getDate("supply_date"));
                h.setAmount(rs.getBigDecimal("amount"));
                histories.add(h);
            }
            logger.info("Retrieved {} supplier history records for order_id: {}", histories.size(), orderId);
        } catch (SQLException e) {
            logger.error("Error retrieving supplier history for order {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving supplier history: " + e.getMessage(), e);
        }
        return histories;
    }

    @Override
    public SupplierHistoryDTO createSupplierHistory(SupplierHistoryDTO historyDTO) {
        String sql = "INSERT INTO supplier_history (supplier_id, order_id, supply_date, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, historyDTO.getSupplierId());
            ps.setInt(2, historyDTO.getOrderId());
            ps.setDate(3, historyDTO.getSupplyDate());
            ps.setBigDecimal(4, historyDTO.getAmount());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to insert supplier history");
            }
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                historyDTO.setHistoryId(rs.getInt(1));
            }
            logger.info("Created supplier history with history_id: {}", historyDTO.getHistoryId());
            return historyDTO;
        } catch (SQLException e) {
            logger.error("Error creating supplier history: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating supplier history: " + e.getMessage(), e);
        }
    }

    // ========== ITEM RETRIEVAL METHODS ==========

    @Override
    public List<OrderItemDTO> getOrderItemsByOrderIdAndSupplierId(Integer orderId, Integer supplierId) {
        String sql = "SELECT oi.item_id, oi.order_id, qi.name, oi.description, oi.quantity, oi.unit_price, p.name AS project_name " +
                     "FROM order_item oi " +
                     "INNER JOIN purchase_order po ON oi.order_id = po.order_id " +
                     "LEFT JOIN project p ON po.project_id = p.project_id " +
                     "LEFT JOIN quotation_response qr ON po.response_id = qr.response_id " +
                     "LEFT JOIN quotation_item qi ON qr.q_id = qi.q_id AND oi.description = qi.description " +
                     "WHERE oi.order_id = ? AND po.supplier_id = ?";
        List<OrderItemDTO> items = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, supplierId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItemDTO item = new OrderItemDTO();
                item.setItemId(rs.getInt("item_id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setProjectName(rs.getString("project_name"));
                items.add(item);
            }
            logger.info("Retrieved {} order items for order_id: {} and supplier_id: {}", items.size(), orderId, supplierId);
        } catch (SQLException e) {
            logger.error("Error retrieving order items for order {} and supplier {}: {}", orderId, supplierId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving order items: " + e.getMessage(), e);
        }
        return items;
    }

    // ========== INVOICE METHODS ==========

    @Override
    public SupplierInvoiceDTO createInvoice(SupplierInvoiceDTO invoiceDTO) {
        String sql = "INSERT INTO order_invoice (order_id, supplier_id, amount, description, file_path, date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, invoiceDTO.getOrderId());
            ps.setInt(2, invoiceDTO.getSupplierId());
            ps.setBigDecimal(3, invoiceDTO.getAmount());
            ps.setString(4, invoiceDTO.getDescription());
            ps.setString(5, invoiceDTO.getFilePath());
            ps.setDate(6, Date.valueOf(invoiceDTO.getDate()));
            ps.setString(7, invoiceDTO.getStatus());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to insert invoice");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                invoiceDTO.setInvoiceId(rs.getInt(1));
            }
            logger.info("Created invoice with invoice_id: {}", invoiceDTO.getInvoiceId());
            return invoiceDTO;
        } catch (SQLException e) {
            logger.error("Error creating invoice: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SupplierInvoiceDTO> getAllInvoices() {
        String sql = "SELECT oi.invoice_id, oi.order_id, oi.supplier_id, oi.amount, oi.description, " +
                     "oi.file_path, oi.date, oi.status, p.name AS project_name, s.company AS supplier_name " +
                     "FROM order_invoice oi " +
                     "LEFT JOIN purchase_order po ON oi.order_id = po.order_id " +
                     "LEFT JOIN project p ON po.project_id = p.project_id " +
                     "LEFT JOIN supplier s ON oi.supplier_id = s.supplier_id";
        List<SupplierInvoiceDTO> invoices = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoiceDTO(rs));
            }
            logger.info("Retrieved {} invoices", invoices.size());
        } catch (SQLException e) {
            logger.error("Error retrieving invoices: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving invoices: " + e.getMessage(), e);
        }
        return invoices;
    }

    @Override
    public SupplierInvoiceDTO getInvoiceById(Integer invoiceId) {
        String sql = "SELECT oi.invoice_id, oi.order_id, oi.supplier_id, oi.amount, oi.description, " +
                     "oi.file_path, oi.date, oi.status, p.name AS project_name, s.company AS supplier_name " +
                     "FROM order_invoice oi " +
                     "LEFT JOIN purchase_order po ON oi.order_id = po.order_id " +
                     "LEFT JOIN project p ON po.project_id = p.project_id " +
                     "LEFT JOIN supplier s ON oi.supplier_id = s.supplier_id " +
                     "WHERE oi.invoice_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("Retrieved invoice with invoice_id: {}", invoiceId);
                return mapResultSetToInvoiceDTO(rs);
            } else {
                logger.warn("No invoice found with invoice_id: {}", invoiceId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving invoice by id {}: {}", invoiceId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SupplierInvoiceDTO> getInvoicesBySupplierId(Integer supplierId) {
        String sql = "SELECT oi.invoice_id, oi.order_id, oi.supplier_id, oi.amount, oi.description, " +
                     "oi.file_path, oi.date, oi.status, p.name AS project_name, s.company AS supplier_name " +
                     "FROM order_invoice oi " +
                     "LEFT JOIN purchase_order po ON oi.order_id = po.order_id " +
                     "LEFT JOIN project p ON po.project_id = p.project_id " +
                     "LEFT JOIN supplier s ON oi.supplier_id = s.supplier_id " +
                     "WHERE oi.supplier_id = ?";
        List<SupplierInvoiceDTO> invoices = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                invoices.add(mapResultSetToInvoiceDTO(rs));
            }
            logger.info("Retrieved {} invoices for supplier_id: {}", invoices.size(), supplierId);
        } catch (SQLException e) {
            logger.error("Error retrieving invoices for supplier {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving invoices for supplier: " + e.getMessage(), e);
        }
        return invoices;
    }

    @Override
    public List<SupplierInvoiceDTO> getInvoicesByOrderId(Integer orderId) {
        String sql = "SELECT oi.invoice_id, oi.order_id, oi.supplier_id, oi.amount, oi.description, " +
                     "oi.file_path, oi.date, oi.status, p.name AS project_name, s.company AS supplier_name " +
                     "FROM order_invoice oi " +
                     "LEFT JOIN purchase_order po ON oi.order_id = po.order_id " +
                     "LEFT JOIN project p ON po.project_id = p.project_id " +
                     "LEFT JOIN supplier s ON oi.supplier_id = s.supplier_id " +
                     "WHERE oi.order_id = ?";
        List<SupplierInvoiceDTO> invoices = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                invoices.add(mapResultSetToInvoiceDTO(rs));
            }
            logger.info("Retrieved {} invoices for order_id: {}", invoices.size(), orderId);
        } catch (SQLException e) {
            logger.error("Error retrieving invoices for order {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving invoices for order: " + e.getMessage(), e);
        }
        return invoices;
    }

    @Override
    public void updateInvoiceStatus(Integer invoiceId, String status) {
        String sql = "UPDATE order_invoice SET status = ? WHERE invoice_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warn("No invoice found with invoice_id: {}", invoiceId);
                throw new RuntimeException("Invoice not found with ID: " + invoiceId);
            }
            logger.info("Updated invoice status to {} for invoice_id: {}", status, invoiceId);
        } catch (SQLException e) {
            logger.error("Error updating invoice status: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating invoice status: " + e.getMessage(), e);
        }
    }

    private SupplierInvoiceDTO mapResultSetToInvoiceDTO(ResultSet rs) throws SQLException {
        SupplierInvoiceDTO invoice = new SupplierInvoiceDTO();
        invoice.setInvoiceId(rs.getInt("invoice_id"));
        invoice.setOrderId(rs.getInt("order_id"));
        invoice.setSupplierId(rs.getInt("supplier_id"));
        invoice.setAmount(rs.getBigDecimal("amount"));
        invoice.setDescription(rs.getString("description"));
        invoice.setFilePath(rs.getString("file_path"));
        Date date = rs.getDate("date");
        if (date != null) {
            invoice.setDate(date.toLocalDate());
        }
        invoice.setStatus(rs.getString("status"));
        invoice.setProjectName(rs.getString("project_name"));
        invoice.setSupplierName(rs.getString("supplier_name"));
        return invoice;
    }

}
