package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.SupplierInvoiceDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SupplierInvoiceService {

    // Create invoice with file upload
    SupplierInvoiceDTO createInvoice(SupplierInvoiceDTO invoiceDTO, MultipartFile file);

    // Get all invoices
    List<SupplierInvoiceDTO> getAllInvoices();

    // Get invoice by ID
    SupplierInvoiceDTO getInvoiceById(Integer invoiceId);

    // Get invoices by supplier ID
    List<SupplierInvoiceDTO> getInvoicesBySupplierId(Integer supplierId);

    // Get invoices by order ID
    List<SupplierInvoiceDTO> getInvoicesByOrderId(Integer orderId);

    // Update invoice status
    void updateInvoiceStatus(Integer invoiceId, String status);
}