package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.ProjectOwnerPaymentDAO;
import com.structurax.root.structurax.root.dto.PaymentConfirmationDTO;
import com.structurax.root.structurax.root.dto.PaymentDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProjectOwnerPaymentDAOImpl implements ProjectOwnerPaymentDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerPaymentDAOImpl.class);
    private final DatabaseConnection databaseConnection;

    public ProjectOwnerPaymentDAOImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // ========== PAYMENT METHODS ==========

    @Override
    public List<PaymentDTO> getPaymentsByProjectId(String projectId) {
        String sql = "SELECT p.payment_id, p.invoice_id, p.due_date, p.paid_date, p.status, p.amount " +
                     "FROM payment p " +
                     "INNER JOIN invoice i ON p.invoice_id = i.invoice_id " +
                     "WHERE i.project_id = ? " +
                     "ORDER BY p.due_date DESC";
        List<PaymentDTO> payments = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payments.add(mapResultSetToPaymentDTO(rs));
            }
            logger.info("Retrieved {} payments for project_id: {}", payments.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving payments for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payments: " + e.getMessage(), e);
        }
        return payments;
    }

    @Override
    public PaymentDTO getPaymentById(Integer paymentId) {
        String sql = "SELECT payment_id, invoice_id, due_date, paid_date, status, amount " +
                     "FROM payment WHERE payment_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("Retrieved payment with payment_id: {}", paymentId);
                return mapResultSetToPaymentDTO(rs);
            } else {
                logger.warn("No payment found with payment_id: {}", paymentId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving payment by id {}: {}", paymentId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payment: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PaymentDTO> getPaymentsByInvoiceId(Integer invoiceId) {
        String sql = "SELECT payment_id, invoice_id, due_date, paid_date, status, amount " +
                     "FROM payment WHERE invoice_id = ? ORDER BY due_date DESC";
        List<PaymentDTO> payments = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payments.add(mapResultSetToPaymentDTO(rs));
            }
            logger.info("Retrieved {} payments for invoice_id: {}", payments.size(), invoiceId);
        } catch (SQLException e) {
            logger.error("Error retrieving payments for invoice {}: {}", invoiceId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payments: " + e.getMessage(), e);
        }
        return payments;
    }

    @Override
    public List<PaymentDTO> getPendingPayments(String projectId) {
        String sql = "SELECT p.payment_id, p.invoice_id, p.due_date, p.paid_date, p.status, p.amount " +
                     "FROM payment p " +
                     "INNER JOIN invoice i ON p.invoice_id = i.invoice_id " +
                     "WHERE i.project_id = ? AND p.status = 'Pending' " +
                     "ORDER BY p.due_date ASC";
        List<PaymentDTO> payments = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payments.add(mapResultSetToPaymentDTO(rs));
            }
            logger.info("Retrieved {} pending payments for project_id: {}", payments.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving pending payments for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving pending payments: " + e.getMessage(), e);
        }
        return payments;
    }

    @Override
    public List<PaymentDTO> getPaidPayments(String projectId) {
        String sql = "SELECT p.payment_id, p.invoice_id, p.due_date, p.paid_date, p.status, p.amount " +
                     "FROM payment p " +
                     "INNER JOIN invoice i ON p.invoice_id = i.invoice_id " +
                     "WHERE i.project_id = ? AND p.status = 'Paid' " +
                     "ORDER BY p.paid_date DESC";
        List<PaymentDTO> payments = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payments.add(mapResultSetToPaymentDTO(rs));
            }
            logger.info("Retrieved {} paid payments for project_id: {}", payments.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving paid payments for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving paid payments: " + e.getMessage(), e);
        }
        return payments;
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        String sql = "INSERT INTO payment (invoice_id, due_date, paid_date, status, amount) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, paymentDTO.getInvoiceId());
            ps.setDate(2, Date.valueOf(paymentDTO.getDuedate()));
            ps.setDate(3, paymentDTO.getPaiddate() != null ? Date.valueOf(paymentDTO.getPaiddate()) : null);
            ps.setString(4, paymentDTO.getStatus());
            ps.setDouble(5, paymentDTO.getAmount());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to insert payment");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                paymentDTO.setPaymentId(rs.getInt(1));
            }
            logger.info("Created payment with payment_id: {}", paymentDTO.getPaymentId());
            return paymentDTO;
        } catch (SQLException e) {
            logger.error("Error creating payment: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating payment: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePaymentStatus(Integer paymentId, String status) {
        String sql = "UPDATE payment SET status = ?, paid_date = CASE WHEN ? = 'Paid' THEN CURDATE() ELSE paid_date END " +
                     "WHERE payment_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, paymentId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warn("No payment found with payment_id: {}", paymentId);
                throw new RuntimeException("Payment not found with ID: " + paymentId);
            }
            logger.info("Updated payment status to {} for payment_id: {}", status, paymentId);
        } catch (SQLException e) {
            logger.error("Error updating payment status: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating payment status: " + e.getMessage(), e);
        }
    }

    // ========== PAYMENT CONFIRMATION METHODS ==========

    @Override
    public List<PaymentConfirmationDTO> getPaymentConfirmationsByProjectId(String projectId) {
        String sql = "SELECT confirmation_id, payment_id, project_id, amount, document_id, status, confirmation_date " +
                     "FROM payment_confirmation WHERE project_id = ? ORDER BY confirmation_date DESC";
        List<PaymentConfirmationDTO> confirmations = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                confirmations.add(mapResultSetToPaymentConfirmationDTO(rs));
            }
            logger.info("Retrieved {} payment confirmations for project_id: {}", confirmations.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving payment confirmations for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payment confirmations: " + e.getMessage(), e);
        }
        return confirmations;
    }

    @Override
    public PaymentConfirmationDTO getPaymentConfirmationById(Integer confirmationId) {
        String sql = "SELECT confirmation_id, payment_id, project_id, amount, document_id, status, confirmation_date " +
                     "FROM payment_confirmation WHERE confirmation_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, confirmationId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("Retrieved payment confirmation with confirmation_id: {}", confirmationId);
                return mapResultSetToPaymentConfirmationDTO(rs);
            } else {
                logger.warn("No payment confirmation found with confirmation_id: {}", confirmationId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving payment confirmation by id {}: {}", confirmationId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payment confirmation: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PaymentConfirmationDTO> getPaymentConfirmationsByPaymentId(Integer paymentId) {
        String sql = "SELECT confirmation_id, payment_id, project_id, amount, document_id, status, confirmation_date " +
                     "FROM payment_confirmation WHERE payment_id = ? ORDER BY confirmation_date DESC";
        List<PaymentConfirmationDTO> confirmations = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                confirmations.add(mapResultSetToPaymentConfirmationDTO(rs));
            }
            logger.info("Retrieved {} payment confirmations for payment_id: {}", confirmations.size(), paymentId);
        } catch (SQLException e) {
            logger.error("Error retrieving payment confirmations for payment {}: {}", paymentId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving payment confirmations: " + e.getMessage(), e);
        }
        return confirmations;
    }

    @Override
    public PaymentConfirmationDTO createPaymentConfirmation(PaymentConfirmationDTO confirmationDTO) {
        String sql = "INSERT INTO payment_confirmation (payment_id, project_id, amount, document_id, status, confirmation_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, confirmationDTO.getPaymentId());
            ps.setString(2, confirmationDTO.getProjectId());
            ps.setDouble(3, confirmationDTO.getAmount());
            ps.setInt(4, confirmationDTO.getDocumentId());
            ps.setString(5, confirmationDTO.getStatus());
            ps.setDate(6, confirmationDTO.getConfirmationDate() != null ?
                         Date.valueOf(confirmationDTO.getConfirmationDate()) : Date.valueOf(java.time.LocalDate.now()));

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to insert payment confirmation");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                confirmationDTO.setConfirmationId(rs.getInt(1));
            }
            logger.info("Created payment confirmation with confirmation_id: {}", confirmationDTO.getConfirmationId());
            return confirmationDTO;
        } catch (SQLException e) {
            logger.error("Error creating payment confirmation: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating payment confirmation: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePaymentConfirmationStatus(Integer confirmationId, String status) {
        String sql = "UPDATE payment_confirmation SET status = ? WHERE confirmation_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, confirmationId);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warn("No payment confirmation found with confirmation_id: {}", confirmationId);
                throw new RuntimeException("Payment confirmation not found with ID: " + confirmationId);
            }
            logger.info("Updated payment confirmation status to {} for confirmation_id: {}", status, confirmationId);
        } catch (SQLException e) {
            logger.error("Error updating payment confirmation status: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating payment confirmation status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PaymentConfirmationDTO> getPendingConfirmations(String projectId) {
        String sql = "SELECT confirmation_id, payment_id, project_id, amount, document_id, status, confirmation_date " +
                     "FROM payment_confirmation WHERE project_id = ? AND status = 'Pending' " +
                     "ORDER BY confirmation_date ASC";
        List<PaymentConfirmationDTO> confirmations = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                confirmations.add(mapResultSetToPaymentConfirmationDTO(rs));
            }
            logger.info("Retrieved {} pending payment confirmations for project_id: {}", confirmations.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving pending payment confirmations for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving pending payment confirmations: " + e.getMessage(), e);
        }
        return confirmations;
    }

    // ========== HELPER METHODS ==========

    private PaymentDTO mapResultSetToPaymentDTO(ResultSet rs) throws SQLException {
        PaymentDTO payment = new PaymentDTO();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setInvoiceId(rs.getInt("invoice_id"));

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            payment.setDuedate(dueDate.toLocalDate());
        }

        Date paidDate = rs.getDate("paid_date");
        if (paidDate != null) {
            payment.setPaiddate(paidDate.toLocalDate());
        }

        payment.setStatus(rs.getString("status"));
        payment.setAmount(rs.getDouble("amount"));
        return payment;
    }

    private PaymentConfirmationDTO mapResultSetToPaymentConfirmationDTO(ResultSet rs) throws SQLException {
        PaymentConfirmationDTO confirmation = new PaymentConfirmationDTO();
        confirmation.setConfirmationId(rs.getInt("confirmation_id"));
        confirmation.setPaymentId(rs.getInt("payment_id"));
        confirmation.setProjectId(rs.getString("project_id"));
        confirmation.setAmount(rs.getDouble("amount"));
        confirmation.setDocumentId(rs.getInt("document_id"));
        confirmation.setStatus(rs.getString("status"));

        Date confirmationDate = rs.getDate("confirmation_date");
        if (confirmationDate != null) {
            confirmation.setConfirmationDate(confirmationDate.toLocalDate());
        }

        return confirmation;
    }
}
