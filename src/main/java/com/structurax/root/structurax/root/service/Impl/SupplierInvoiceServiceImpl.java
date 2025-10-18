package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.SupplierInvoiceDTO;
import com.structurax.root.structurax.root.service.SupplierInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Service
public class SupplierInvoiceServiceImpl implements SupplierInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierInvoiceServiceImpl.class);
    private static final String UPLOAD_DIR = "uploads/invoices/";

    private final SupplierDAO supplierDAO;

    public SupplierInvoiceServiceImpl(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    @Override
    public SupplierInvoiceDTO createInvoice(SupplierInvoiceDTO invoiceDTO, MultipartFile file) {
        logger.info("Service: creating invoice for order_id={}", invoiceDTO.getOrderId());

        // Set default values
        invoiceDTO.setDate(LocalDate.now());
        invoiceDTO.setStatus("PENDING");

        // Handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                String filePath = saveFile(file);
                invoiceDTO.setFilePath(filePath);
                logger.info("Invoice file saved at: {}", filePath);
            } catch (IOException e) {
                logger.error("Error saving invoice file: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to save invoice file: " + e.getMessage());
            }
        }

        return supplierDAO.createInvoice(invoiceDTO);
    }

    @Override
    public List<SupplierInvoiceDTO> getAllInvoices() {
        logger.info("Service: fetching all invoices");
        return supplierDAO.getAllInvoices();
    }

    @Override
    public SupplierInvoiceDTO getInvoiceById(Integer invoiceId) {
        logger.info("Service: fetching invoice by id={}", invoiceId);
        return supplierDAO.getInvoiceById(invoiceId);
    }

    @Override
    public List<SupplierInvoiceDTO> getInvoicesBySupplierId(Integer supplierId) {
        logger.info("Service: fetching invoices for supplier_id={}", supplierId);
        return supplierDAO.getInvoicesBySupplierId(supplierId);
    }

    @Override
    public List<SupplierInvoiceDTO> getInvoicesByOrderId(Integer orderId) {
        logger.info("Service: fetching invoices for order_id={}", orderId);
        return supplierDAO.getInvoicesByOrderId(orderId);
    }

    @Override
    public void updateInvoiceStatus(Integer invoiceId, String status) {
        logger.info("Service: updating invoice status for invoice_id={} to {}", invoiceId, status);
        supplierDAO.updateInvoiceStatus(invoiceId, status);
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Paths.get(UPLOAD_DIR + filename);

        // Save file
        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }
}