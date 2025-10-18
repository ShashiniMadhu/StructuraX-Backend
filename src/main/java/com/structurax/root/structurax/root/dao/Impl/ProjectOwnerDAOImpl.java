package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ProjectOwnerDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectOwnerDAOImpl implements ProjectOwnerDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerDAOImpl.class);
    private final DatabaseConnection databaseConnection;

    public ProjectOwnerDAOImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<ProjectMaterialDTO> getMaterialsByProjectId(String projectId) {
        String sql = "SELECT oi.item_id, oi.order_id, oi.description, oi.quantity, oi.unit_price, " +
                     "qi.name, po.estimated_delivery_date, po.order_status, po.project_id " +
                     "FROM order_item oi " +
                     "INNER JOIN purchase_order po ON oi.order_id = po.order_id " +
                     "LEFT JOIN quotation_response qr ON po.response_id = qr.response_id " +
                     "LEFT JOIN quotation_item qi ON qr.q_id = qi.q_id AND oi.description = qi.description " +
                     "WHERE po.project_id = ? " +
                     "ORDER BY po.order_date DESC";

        List<ProjectMaterialDTO> materials = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ProjectMaterialDTO material = new ProjectMaterialDTO();
                material.setMaterialId(rs.getInt("item_id"));
                material.setProjectId(rs.getString("project_id"));
                material.setName(rs.getString("name"));
                material.setBrand("Standard"); // Default value
                material.setQuantity(rs.getInt("quantity") + " units");
                material.setDescription(rs.getString("description"));

                // Map order status to material status
                boolean orderStatus = rs.getBoolean("order_status");
                Date deliveryDate = rs.getDate("estimated_delivery_date");

                if (orderStatus) {
                    material.setStatus("Delivered");
                } else if (deliveryDate != null && deliveryDate.toLocalDate().isAfter(java.time.LocalDate.now())) {
                    material.setStatus("Ordered");
                } else {
                    material.setStatus("Pending");
                }

                material.setDeliveryDate(deliveryDate != null ? deliveryDate.toLocalDate() : null);
                material.setCost(rs.getBigDecimal("unit_price"));
                material.setOrderId(rs.getInt("order_id"));

                materials.add(material);
            }

            logger.info("Retrieved {} materials for project_id: {}", materials.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving materials for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving materials: " + e.getMessage(), e);
        }

        return materials;
    }

    @Override
    public MaterialSummaryDTO getMaterialSummaryByProjectId(String projectId) {
        String sql = "SELECT " +
                     "COUNT(*) as total_materials, " +
                     "SUM(CASE WHEN po.order_status = true THEN 1 ELSE 0 END) as delivered_count, " +
                     "SUM(CASE WHEN po.order_status = false AND po.estimated_delivery_date > CURDATE() THEN 1 ELSE 0 END) as in_transit_count, " +
                     "SUM(CASE WHEN po.order_status = false AND (po.estimated_delivery_date IS NULL OR po.estimated_delivery_date <= CURDATE()) THEN 1 ELSE 0 END) as pending_count, " +
                     "SUM(oi.quantity * oi.unit_price) as total_cost " +
                     "FROM order_item oi " +
                     "INNER JOIN purchase_order po ON oi.order_id = po.order_id " +
                     "WHERE po.project_id = ?";

        MaterialSummaryDTO summary = new MaterialSummaryDTO();
        summary.setProjectId(projectId);

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                summary.setTotalMaterials(rs.getInt("total_materials"));
                summary.setDeliveredCount(rs.getInt("delivered_count"));
                summary.setInTransitCount(rs.getInt("in_transit_count"));
                summary.setPendingCount(rs.getInt("pending_count"));

                BigDecimal totalCost = rs.getBigDecimal("total_cost");
                summary.setTotalCost(totalCost != null ? totalCost : BigDecimal.ZERO);

                // Rough estimation: 75% raw materials, 25% finishing materials
                if (totalCost != null) {
                    summary.setRawMaterialsCost(totalCost.multiply(new BigDecimal("0.75")));
                    summary.setFinishingMaterialsCost(totalCost.multiply(new BigDecimal("0.25")));
                } else {
                    summary.setRawMaterialsCost(BigDecimal.ZERO);
                    summary.setFinishingMaterialsCost(BigDecimal.ZERO);
                }
            }

            logger.info("Retrieved material summary for project_id: {}", projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving material summary for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving material summary: " + e.getMessage(), e);
        }

        return summary;
    }

    @Override
    public SiteVisitDTO createSiteVisit(SiteVisitDTO siteVisitDTO) {
        String sql = "INSERT INTO site_visit (project_id, type, date, time, status, requested_by, purpose, client_id, created_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, siteVisitDTO.getProjectId());
            ps.setString(2, siteVisitDTO.getType());
            ps.setDate(3, Date.valueOf(siteVisitDTO.getDate()));
            ps.setString(4, siteVisitDTO.getTime());
            ps.setString(5, siteVisitDTO.getStatus() != null ? siteVisitDTO.getStatus() : "Requested");
            ps.setString(6, siteVisitDTO.getRequestedBy());
            ps.setString(7, siteVisitDTO.getPurpose());
            ps.setInt(8, siteVisitDTO.getClientId());
            ps.setDate(9, Date.valueOf(java.time.LocalDate.now()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to create site visit");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                siteVisitDTO.setVisitId(rs.getInt(1));
            }

            if (siteVisitDTO.getStatus() == null) {
                siteVisitDTO.setStatus("Requested");
            }
            if (siteVisitDTO.getCreatedDate() == null) {
                siteVisitDTO.setCreatedDate(java.time.LocalDate.now());
            }

            logger.info("Created site visit with visit_id: {}", siteVisitDTO.getVisitId());
            return siteVisitDTO;

        } catch (SQLException e) {
            logger.error("Error creating site visit: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating site visit: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SiteVisitDTO> getSiteVisitsByProjectId(String projectId) {
        String sql = "SELECT visit_id, project_id, type, date, time, status, requested_by, purpose, client_id, created_date " +
                     "FROM site_visit " +
                     "WHERE project_id = ? " +
                     "ORDER BY created_date DESC, date DESC";

        List<SiteVisitDTO> siteVisits = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SiteVisitDTO visit = new SiteVisitDTO();
                visit.setVisitId(rs.getInt("visit_id"));
                visit.setProjectId(rs.getString("project_id"));
                visit.setType(rs.getString("type"));

                Date date = rs.getDate("date");
                visit.setDate(date != null ? date.toLocalDate() : null);

                visit.setTime(rs.getString("time"));
                visit.setStatus(rs.getString("status"));
                visit.setRequestedBy(rs.getString("requested_by"));
                visit.setPurpose(rs.getString("purpose"));
                visit.setClientId(rs.getInt("client_id"));

                Date createdDate = rs.getDate("created_date");
                visit.setCreatedDate(createdDate != null ? createdDate.toLocalDate() : null);

                siteVisits.add(visit);
            }

            logger.info("Retrieved {} site visits for project_id: {}", siteVisits.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving site visits for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving site visits: " + e.getMessage(), e);
        }

        return siteVisits;
    }

    @Override
    public SiteVisitDTO getSiteVisitById(Integer visitId) {
        String sql = "SELECT visit_id, project_id, type, date, time, status, requested_by, purpose, client_id, created_date " +
                     "FROM site_visit " +
                     "WHERE visit_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, visitId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                SiteVisitDTO visit = new SiteVisitDTO();
                visit.setVisitId(rs.getInt("visit_id"));
                visit.setProjectId(rs.getString("project_id"));
                visit.setType(rs.getString("type"));

                Date date = rs.getDate("date");
                visit.setDate(date != null ? date.toLocalDate() : null);

                visit.setTime(rs.getString("time"));
                visit.setStatus(rs.getString("status"));
                visit.setRequestedBy(rs.getString("requested_by"));
                visit.setPurpose(rs.getString("purpose"));
                visit.setClientId(rs.getInt("client_id"));

                Date createdDate = rs.getDate("created_date");
                visit.setCreatedDate(createdDate != null ? createdDate.toLocalDate() : null);

                logger.info("Retrieved site visit with visit_id: {}", visitId);
                return visit;
            } else {
                logger.warn("No site visit found with visit_id: {}", visitId);
                return null;
            }

        } catch (SQLException e) {
            logger.error("Error retrieving site visit by id {}: {}", visitId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving site visit: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateSiteVisitStatus(Integer visitId, String status) {
        String sql = "UPDATE site_visit SET status = ? WHERE visit_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, visitId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warn("No site visit found with visit_id: {}", visitId);
                throw new RuntimeException("Site visit not found with ID: " + visitId);
            }

            logger.info("Updated site visit status to {} for visit_id: {}", status, visitId);

        } catch (SQLException e) {
            logger.error("Error updating site visit status: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating site visit status: " + e.getMessage(), e);
        }
    }

    // ========== PAYMENT METHODS ==========

    @Override
    public PaymentSummaryDTO getPaymentSummaryByProjectId(String projectId) {
        String sql = "SELECT " +
                     "pp.total_amount as total_budget, " +
                     "SUM(CASE WHEN i.status = 'Paid' THEN i.amount ELSE 0 END) as amount_paid, " +
                     "COUNT(*) as total_payments, " +
                     "SUM(CASE WHEN i.status = 'Paid' THEN 1 ELSE 0 END) as completed_payments, " +
                     "SUM(CASE WHEN i.status = 'Pending' THEN 1 ELSE 0 END) as pending_payments " +
                     "FROM payment_plan pp " +
                     "LEFT JOIN installment i ON pp.payment_plan_id = i.payment_plan_id " +
                     "WHERE pp.project_id = ? " +
                     "GROUP BY pp.payment_plan_id, pp.total_amount";

        PaymentSummaryDTO summary = new PaymentSummaryDTO();
        summary.setProjectId(projectId);

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BigDecimal totalBudget = rs.getBigDecimal("total_budget");
                BigDecimal amountPaid = rs.getBigDecimal("amount_paid");

                summary.setTotalBudget(totalBudget != null ? totalBudget : BigDecimal.ZERO);
                summary.setAmountPaid(amountPaid != null ? amountPaid : BigDecimal.ZERO);
                summary.setPendingAmount(totalBudget != null && amountPaid != null ?
                    totalBudget.subtract(amountPaid) : totalBudget);
                summary.setTotalPayments(rs.getInt("total_payments"));
                summary.setCompletedPayments(rs.getInt("completed_payments"));
                summary.setPendingPayments(rs.getInt("pending_payments"));
            } else {
                // No payment plan exists
                summary.setTotalBudget(BigDecimal.ZERO);
                summary.setAmountPaid(BigDecimal.ZERO);
                summary.setPendingAmount(BigDecimal.ZERO);
                summary.setTotalPayments(0);
                summary.setCompletedPayments(0);
                summary.setPendingPayments(0);
            }

            logger.info("Retrieved payment summary for project_id: {}", projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving payment summary for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payment summary: " + e.getMessage(), e);
        }

        return summary;
    }

    @Override
    public List<InstallmentDTO> getPaymentHistoryByProjectId(String projectId) {
        String sql = "SELECT i.installment_id, i.payment_plan_id, i.amount, i.due_date, i.status, i.paid_date " +
                     "FROM installment i " +
                     "INNER JOIN payment_plan pp ON i.payment_plan_id = pp.payment_plan_id " +
                     "WHERE pp.project_id = ? AND i.status = 'paid' " +
                     "ORDER BY i.paid_date DESC";

        List<InstallmentDTO> paymentHistory = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InstallmentDTO installment = new InstallmentDTO();
                installment.setInstallmentId(rs.getInt("installment_id"));
                installment.setPaymentPlanId(rs.getInt("payment_plan_id"));
                installment.setAmount(rs.getDouble("amount"));

                Date dueDate = rs.getDate("due_date");
                installment.setDueDate(dueDate);

                installment.setStatus(rs.getString("status"));

                Date paidDate = rs.getDate("paid_date");
                installment.setPaidDate(paidDate);

                paymentHistory.add(installment);
            }

            logger.info("Retrieved {} payment history records for project_id: {}", paymentHistory.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving payment history for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payment history: " + e.getMessage(), e);
        }

        return paymentHistory;
    }

    @Override
    public List<InstallmentDTO> getUpcomingPaymentsByProjectId(String projectId) {
        String sql = "SELECT i.installment_id, i.payment_plan_id, i.amount, i.due_date, i.status, i.paid_date " +
                     "FROM installment i " +
                     "INNER JOIN payment_plan pp ON i.payment_plan_id = pp.payment_plan_id " +
                     "WHERE pp.project_id = ? AND i.status = 'upcoming' " +
                     "ORDER BY i.due_date ASC";

        List<InstallmentDTO> upcomingPayments = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InstallmentDTO installment = new InstallmentDTO();
                installment.setInstallmentId(rs.getInt("installment_id"));
                installment.setPaymentPlanId(rs.getInt("payment_plan_id"));
                installment.setAmount(rs.getDouble("amount"));

                Date dueDate = rs.getDate("due_date");
                installment.setDueDate(dueDate);

                installment.setStatus(rs.getString("status"));

                Date paidDate = rs.getDate("paid_date");
                installment.setPaidDate(paidDate);

                upcomingPayments.add(installment);
            }

            logger.info("Retrieved {} upcoming payments for project_id: {}", upcomingPayments.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving upcoming payments for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving upcoming payments: " + e.getMessage(), e);
        }

        return upcomingPayments;
    }

    @Override
    public PaymentReceiptDTO uploadPaymentReceipt(PaymentReceiptDTO receiptDTO) {
        String sql = "INSERT INTO payment_receipt (installment_id, project_id, phase, amount, payment_date, " +
                     "receipt_file_path, description, status, uploaded_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, receiptDTO.getInstallmentId());
            ps.setString(2, receiptDTO.getProjectId());
            ps.setString(3, receiptDTO.getPhase());
            ps.setBigDecimal(4, receiptDTO.getAmount());
            ps.setDate(5, Date.valueOf(receiptDTO.getPaymentDate()));
            ps.setString(6, receiptDTO.getReceiptFilePath());
            ps.setString(7, receiptDTO.getDescription());
            ps.setString(8, receiptDTO.getStatus() != null ? receiptDTO.getStatus() : "Pending");
            ps.setDate(9, Date.valueOf(java.time.LocalDate.now()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to upload payment receipt");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                receiptDTO.setReceiptId(rs.getInt(1));
            }

            if (receiptDTO.getStatus() == null) {
                receiptDTO.setStatus("Pending");
            }
            if (receiptDTO.getUploadedDate() == null) {
                receiptDTO.setUploadedDate(java.time.LocalDate.now());
            }

            logger.info("Uploaded payment receipt with receipt_id: {}", receiptDTO.getReceiptId());
            return receiptDTO;

        } catch (SQLException e) {
            logger.error("Error uploading payment receipt: {}", e.getMessage(), e);
            throw new RuntimeException("Error uploading payment receipt: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PaymentReceiptDTO> getPaymentReceiptsByProjectId(String projectId) {
        String sql = "SELECT receipt_id, installment_id, project_id, phase, amount, payment_date, " +
                     "receipt_file_path, description, status, uploaded_date " +
                     "FROM payment_receipt " +
                     "WHERE project_id = ? " +
                     "ORDER BY uploaded_date DESC";

        List<PaymentReceiptDTO> receipts = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PaymentReceiptDTO receipt = new PaymentReceiptDTO();
                receipt.setReceiptId(rs.getInt("receipt_id"));
                receipt.setInstallmentId(rs.getInt("installment_id"));
                receipt.setProjectId(rs.getString("project_id"));
                receipt.setPhase(rs.getString("phase"));
                receipt.setAmount(rs.getBigDecimal("amount"));

                Date paymentDate = rs.getDate("payment_date");
                receipt.setPaymentDate(paymentDate != null ? paymentDate.toLocalDate() : null);

                receipt.setReceiptFilePath(rs.getString("receipt_file_path"));
                receipt.setDescription(rs.getString("description"));
                receipt.setStatus(rs.getString("status"));

                Date uploadedDate = rs.getDate("uploaded_date");
                receipt.setUploadedDate(uploadedDate != null ? uploadedDate.toLocalDate() : null);

                receipts.add(receipt);
            }

            logger.info("Retrieved {} payment receipts for project_id: {}", receipts.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving payment receipts for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payment receipts: " + e.getMessage(), e);
        }

        return receipts;
    }
}