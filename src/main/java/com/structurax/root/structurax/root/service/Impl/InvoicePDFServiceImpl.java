package com.structurax.root.structurax.root.service.Impl;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.structurax.root.structurax.root.dao.PurchaseOrderDAO;
import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.service.InvoicePDFService;
import com.structurax.root.structurax.root.service.SQSService;

@Service
public class InvoicePDFServiceImpl implements InvoicePDFService {
    
    @Autowired
    private PurchaseOrderDAO purchaseOrderDAO;
    
    @Autowired
    private SQSService sqsService;
    
    @Autowired
    private SupplierDAO supplierDAO;
    
    /**
     * Generate invoice PDF based on a purchase order
     * Treats purchase orders as invoices for billing purposes
     */
    @Override
    public byte[] generateInvoicePdf(Integer orderId) {
        PurchaseOrderDTO order = purchaseOrderDAO.getPurchaseOrderById(orderId);
        List<OrderItemDTO> items = purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Company Header
            Font companyFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Paragraph companyName = new Paragraph("StructuraX", companyFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            document.add(new Paragraph(" "));
            
            // Header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("PURCHASE DOCUMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Invoice Information Section
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("PURCHASE INFORMATION", sectionFont));
            document.add(new Paragraph("Document Number: PD-" + order.getOrderId()));
            document.add(new Paragraph("Purchase Order Reference: " + order.getOrderId()));
            document.add(new Paragraph("Quotation Response Reference: " + order.getResponseId()));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String invoiceDate = "N/A";
            String dueDate = "N/A";
            
            try {
                if (order.getOrderDate() != null) {
                    invoiceDate = order.getOrderDate().format(formatter);
                }
            } catch (Exception e) {
                System.err.println("Error formatting invoice date: " + e.getMessage());
                invoiceDate = order.getOrderDate().toString();
            }
            
            try {
                if (order.getEstimatedDeliveryDate() != null) {
                    dueDate = order.getEstimatedDeliveryDate().format(formatter);
                }
            } catch (Exception e) {
                System.err.println("Error formatting due date: " + e.getMessage());
                dueDate = order.getEstimatedDeliveryDate().toString();
            }
            
            document.add(new Paragraph("Invoice Date: " + invoiceDate));
            document.add(new Paragraph("Due Date: " + dueDate));
            
            String paymentStatus = "Payment Pending"; // Default to pending
            if (order.getPaymentStatus() != null) {
                String status = order.getPaymentStatus().trim().toLowerCase();
                System.out.println("DEBUG: Payment Status from DB: '" + order.getPaymentStatus() + "' (trimmed/lowercase: '" + status + "')");
                
                if ("paid".equals(status) || "payment completed".equals(status) || "complete".equals(status)) {
                    paymentStatus = "Payment Completed";
                } else if ("partial".equals(status) || "partially paid".equals(status)) {
                    paymentStatus = "Partially Paid";
                } else {
                    paymentStatus = "Payment Pending";
                }
            }
            document.add(new Paragraph("Payment Status: " + paymentStatus));
            
            document.add(new Paragraph(" "));
            
            // Supplier Information (Bill To)
            try {
                SupplierDTO supplier = supplierDAO.getSupplierById(order.getSupplierId());
                if (supplier != null) {
                    document.add(new Paragraph("BILL TO", sectionFont));
                    document.add(new Paragraph("Supplier: " + supplier.getSupplier_name()));
                    document.add(new Paragraph("Address: " + supplier.getAddress()));
                    document.add(new Paragraph("Phone: " + supplier.getPhone()));
                    document.add(new Paragraph("Email: " + supplier.getEmail()));
                    document.add(new Paragraph(" "));
                } else {
                    document.add(new Paragraph("BILL TO", sectionFont));
                    document.add(new Paragraph("Supplier information not available"));
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                System.err.println("Could not retrieve supplier information for invoice: " + orderId);
                document.add(new Paragraph("BILL TO", sectionFont));
                document.add(new Paragraph("Supplier information not available"));
                document.add(new Paragraph(" "));
            }
            
            // Invoice Items Table
            document.add(new Paragraph("INVOICE ITEMS", sectionFont));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Set column widths
            float[] columnWidths = {4f, 1.5f, 1f, 1.5f};
            table.setWidths(columnWidths);
            
            // Table headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            table.addCell(new Paragraph("Description", headerFont));
            table.addCell(new Paragraph("Unit Price", headerFont));
            table.addCell(new Paragraph("Quantity", headerFont));
            table.addCell(new Paragraph("Amount", headerFont));
            
            // Add items to table
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            
            for (OrderItemDTO item : items) {
                double itemTotal = item.getUnitPrice().doubleValue() * item.getQuantity();
                table.addCell(item.getDescription());
                table.addCell(df.format(item.getUnitPrice()));
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(df.format(itemTotal));
                totalAmount += itemTotal;
            }
            
            // Add subtotal, tax, and total rows
            table.addCell(new Paragraph("SUBTOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            // You can add tax calculation here if needed
            // For now, we'll just show the total
            table.addCell(new Paragraph("TOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            document.add(table);
            
            // Payment terms and notes
            document.add(new Paragraph(" "));
            document.add(new Paragraph("PAYMENT TERMS", sectionFont));
            document.add(new Paragraph("For inquiries, contact within 7 days after payment."));
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating invoice PDF", e);
        }
    }
    
    @Override
    public byte[] generateSimpleInvoicePdf(Integer orderId) {
        PurchaseOrderDTO order = purchaseOrderDAO.getPurchaseOrderById(orderId);
        List<OrderItemDTO> items = purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Company Header
            Font companyFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph companyName = new Paragraph("StructuraX", companyFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            document.add(new Paragraph(" "));
            
            // Simple header
            document.add(new Paragraph("Invoice Number: INV-" + order.getOrderId()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String invoiceDate = "N/A";
            
            try {
                if (order.getOrderDate() != null) {
                    invoiceDate = order.getOrderDate().format(formatter);
                }
            } catch (Exception e) {
                System.err.println("Error formatting invoice date: " + e.getMessage());
                invoiceDate = order.getOrderDate().toString();
            }
            
            document.add(new Paragraph("Invoice Date: " + invoiceDate));
            
            // Supplier Information (Bill To)
            try {
                SupplierDTO supplier = supplierDAO.getSupplierById(order.getSupplierId());
                if (supplier != null) {
                    document.add(new Paragraph("Bill To: " + supplier.getSupplier_name()));
                    document.add(new Paragraph("Address: " + supplier.getAddress()));
                    document.add(new Paragraph("Phone: " + supplier.getPhone()));
                } else {
                    document.add(new Paragraph("Bill To: Supplier information not available"));
                }
            } catch (Exception e) {
                document.add(new Paragraph("Bill To: Supplier information not available"));
            }
            
            document.add(new Paragraph(" "));
            
            // Simple table
            PdfPTable table = new PdfPTable(4);
            table.addCell("Description");
            table.addCell("Unit Price");
            table.addCell("Quantity");
            table.addCell("Total");
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            
            for (OrderItemDTO item : items) {
                double itemTotal = item.getUnitPrice().doubleValue() * item.getQuantity();
                table.addCell(item.getDescription());
                table.addCell(df.format(item.getUnitPrice()));
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(df.format(itemTotal));
                totalAmount += itemTotal;
            }
            
            // Total row
            table.addCell("TOTAL");
            table.addCell("");
            table.addCell("");
            table.addCell(df.format(totalAmount));
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating simple invoice PDF", e);
        }
    }
    
    @Override
    public byte[] generateDetailedInvoicePdf(Integer orderId) {
        PurchaseOrderDTO order = purchaseOrderDAO.getPurchaseOrderById(orderId);
        List<OrderItemDTO> items = purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Company Header
            Font companyFont = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD);
            Paragraph companyName = new Paragraph("StructuraX", companyFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            document.add(new Paragraph(" "));
            
            // Detailed header with company info
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("DETAILED PURCHASE INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Document information
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("INVOICE DETAILS", sectionFont));
            document.add(new Paragraph("Document Type: Purchase Invoice"));
            document.add(new Paragraph("Invoice Number: INV-" + order.getOrderId()));
            document.add(new Paragraph("Purchase Order Reference: " + order.getOrderId()));
            document.add(new Paragraph("Quotation Response Reference: " + order.getResponseId()));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String invoiceDate = "N/A";
            String dueDate = "N/A";
            
            try {
                if (order.getOrderDate() != null) {
                    invoiceDate = order.getOrderDate().format(formatter);
                }
            } catch (Exception e) {
                System.err.println("Error formatting invoice date: " + e.getMessage());
                invoiceDate = order.getOrderDate().toString();
            }
            
            try {
                if (order.getEstimatedDeliveryDate() != null) {
                    dueDate = order.getEstimatedDeliveryDate().format(formatter);
                }
            } catch (Exception e) {
                System.err.println("Error formatting due date: " + e.getMessage());
                dueDate = order.getEstimatedDeliveryDate().toString();
            }
            
            document.add(new Paragraph("Invoice Date: " + invoiceDate));
            document.add(new Paragraph("Due Date: " + dueDate));
            
            String paymentStatus = "Payment Pending"; // Default to pending
            if (order.getPaymentStatus() != null) {
                String status = order.getPaymentStatus().trim().toLowerCase();
                System.out.println("DEBUG: Payment Status from DB: '" + order.getPaymentStatus() + "' (trimmed/lowercase: '" + status + "')");
                
                if ("paid".equals(status) || "payment completed".equals(status) || "complete".equals(status)) {
                    paymentStatus = "Payment Completed";
                } else if ("partial".equals(status) || "partially paid".equals(status)) {
                    paymentStatus = "Partially Paid";
                } else {
                    paymentStatus = "Payment Pending";
                }
            }
            document.add(new Paragraph("Payment Status: " + paymentStatus));
            
            document.add(new Paragraph("Supplier ID: " + order.getSupplierId()));
            document.add(new Paragraph(" "));
            
            // Supplier Information (Bill to)
            try {
                SupplierDTO supplier = supplierDAO.getSupplierById(order.getSupplierId());
                if (supplier != null) {
                    document.add(new Paragraph("SUPPLIER DETAILS (BILL TO)", sectionFont));
                    document.add(new Paragraph("Supplier Name: " + supplier.getSupplier_name()));
                    document.add(new Paragraph("Address: " + supplier.getAddress()));
                    document.add(new Paragraph("Phone: " + supplier.getPhone()));
                    document.add(new Paragraph("Email: " + supplier.getEmail()));
                    document.add(new Paragraph(" "));
                } else {
                    document.add(new Paragraph("SUPPLIER DETAILS (BILL TO)", sectionFont));
                    document.add(new Paragraph("Supplier information not available"));
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                System.err.println("Could not retrieve supplier information for invoice: " + orderId);
                document.add(new Paragraph("SUPPLIER DETAILS (BILL TO)", sectionFont));
                document.add(new Paragraph("Supplier information not available"));
                document.add(new Paragraph(" "));
            }
            
            // Detailed items table
            document.add(new Paragraph("ITEMIZED INVOICE", sectionFont));
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            float[] columnWidths = {0.5f, 4f, 1.5f, 1f, 1.5f, 1f};
            table.setWidths(columnWidths);
            
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            table.addCell(new Paragraph("S.No", headerFont));
            table.addCell(new Paragraph("Item Description", headerFont));
            table.addCell(new Paragraph("Unit Price", headerFont));
            table.addCell(new Paragraph("Quantity", headerFont));
            table.addCell(new Paragraph("Line Total", headerFont));
            table.addCell(new Paragraph("Item ID", headerFont));
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            int serialNo = 1;
            
            for (OrderItemDTO item : items) {
                double itemTotal = item.getUnitPrice().doubleValue() * item.getQuantity();
                table.addCell(String.valueOf(serialNo++));
                table.addCell(item.getDescription());
                table.addCell(df.format(item.getUnitPrice()));
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(df.format(itemTotal));
                table.addCell(String.valueOf(item.getItemId()));
                totalAmount += itemTotal;
            }
            
            // Summary rows
            table.addCell("");
            table.addCell(new Paragraph("SUBTOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            table.addCell("");
            
            table.addCell("");
            table.addCell(new Paragraph("TOTAL AMOUNT", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            table.addCell("");
            
            document.add(table);
            
            // Invoice summary
            document.add(new Paragraph(" "));
            document.add(new Paragraph("INVOICE SUMMARY", sectionFont));
            document.add(new Paragraph("Total Number of Items: " + items.size()));
            document.add(new Paragraph("Total Invoice Amount: " + df.format(totalAmount)));
            
            // Terms and conditions
            document.add(new Paragraph(" "));
            document.add(new Paragraph("TERMS AND CONDITIONS", sectionFont));
            document.add(new Paragraph("1. Payment is due within 30 days of invoice date."));
            document.add(new Paragraph("2. Late payments may be subject to interest charges."));
            document.add(new Paragraph("3. All prices are in local currency."));
            document.add(new Paragraph("4. Goods sold are subject to our standard terms and conditions."));
            
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating detailed invoice PDF", e);
        }
    }
}
