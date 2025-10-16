package com.structurax.root.structurax.root.service.Impl;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.PurchaseOrderPDFService;
import com.structurax.root.structurax.root.service.SQSService;

@Service
public class PurchaseOrderPDFServiceImpl implements PurchaseOrderPDFService {
    
    @Autowired
    private PurchaseOrderDAO purchaseOrderDAO;
    
    @Autowired
    private SQSService sqsService;
    
    @Override
    public byte[] generatePurchaseOrderPdf(Integer orderId) {
        PurchaseOrderDTO order = purchaseOrderDAO.getPurchaseOrderById(orderId);
        List<OrderItemDTO> items = purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("PURCHASE ORDER", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Order Information Section
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("ORDER INFORMATION", sectionFont));
            document.add(new Paragraph("Order ID: " + order.getOrderId()));
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            document.add(new Paragraph("Order Date: " + (order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "N/A")));
            document.add(new Paragraph("Estimated Delivery Date: " + (order.getEstimatedDeliveryDate() != null ? dateFormat.format(order.getEstimatedDeliveryDate()) : "N/A")));
            
            Boolean orderStatus = order.getOrderStatus();
            String orderStatusText = (orderStatus != null && orderStatus) ? "Completed" : "Pending";
            document.add(new Paragraph("Order Status: " + orderStatusText));
            
            String paymentStatus = "Paid".equals(order.getPaymentStatus()) ? "Paid" : "Pending";
            document.add(new Paragraph("Payment Status: " + paymentStatus));
            
            document.add(new Paragraph("Supplier ID: " + order.getSupplierId()));
            document.add(new Paragraph(" "));
            
            // Project Information
            try {
                com.structurax.root.structurax.root.dto.Project1DTO projectInfo = sqsService.getProjectById(order.getProjectId());
                document.add(new Paragraph("PROJECT INFORMATION", sectionFont));
                document.add(new Paragraph("Project Name: " + (projectInfo != null ? projectInfo.getName() : "N/A")));
                document.add(new Paragraph("Project ID: " + order.getProjectId()));
                document.add(new Paragraph(" "));
            } catch (Exception e) {
                document.add(new Paragraph("PROJECT INFORMATION", sectionFont));
                document.add(new Paragraph("Project ID: " + order.getProjectId()));
                document.add(new Paragraph(" "));
            }
            
            // Order Items Table
            document.add(new Paragraph("ORDER ITEMS", sectionFont));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Set column widths
            float[] columnWidths = {4f, 1.5f, 1f, 1.5f};
            table.setWidths(columnWidths);
            
            // Table headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            table.addCell(new Paragraph("Item Description", headerFont));
            table.addCell(new Paragraph("Unit Price", headerFont));
            table.addCell(new Paragraph("Quantity", headerFont));
            table.addCell(new Paragraph("Total", headerFont));
            
            // Add items to table
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            
            for (OrderItemDTO item : items) {
                table.addCell(item.getDescription() != null ? item.getDescription() : "N/A");
                double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0;
                int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                table.addCell(df.format(unitPrice));
                table.addCell(String.valueOf(quantity));
                
                double itemTotal = unitPrice * quantity;
                table.addCell(df.format(itemTotal));
                totalAmount += itemTotal;
            }
            
            // Add total row
            table.addCell(new Paragraph("TOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating purchase order PDF", e);
        }
    }
    
    @Override
    public byte[] generateSimplePurchaseOrderPdf(Integer orderId) {
        PurchaseOrderDTO order = purchaseOrderDAO.getPurchaseOrderById(orderId);
        List<OrderItemDTO> items = purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Simple header
            document.add(new Paragraph("Purchase Order ID: " + order.getOrderId()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            document.add(new Paragraph("Order Date: " + (order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "N/A")));
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
                table.addCell(item.getDescription() != null ? item.getDescription() : "N/A");
                double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0;
                int quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 0;
                table.addCell(df.format(unitPrice));
                table.addCell(String.valueOf(quantity));
                
                double itemTotal = unitPrice * quantity;
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
            throw new RuntimeException("Error generating simple purchase order PDF", e);
        }
    }
    
    @Override
    public byte[] generateDetailedPurchaseOrderPdf(Integer orderId) {
        PurchaseOrderDTO order = purchaseOrderDAO.getPurchaseOrderById(orderId);
        List<OrderItemDTO> items = purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Detailed header with company info
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("DETAILED PURCHASE ORDER", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Document information
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("DOCUMENT INFORMATION", sectionFont));
            document.add(new Paragraph("Document Type: Purchase Order"));
            document.add(new Paragraph("Order Reference: " + order.getOrderId()));
            document.add(new Paragraph("Response Reference: " + order.getResponseId()));
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            document.add(new Paragraph("Order Date: " + (order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "N/A")));
            document.add(new Paragraph("Expected Delivery: " + (order.getEstimatedDeliveryDate() != null ? dateFormat.format(order.getEstimatedDeliveryDate()) : "N/A")));
            
            Boolean orderStatus = order.getOrderStatus();
            String orderStatusText = (orderStatus != null && orderStatus) ? "Order Completed" : "Order Pending";
            document.add(new Paragraph("Order Status: " + orderStatusText));
            
            String paymentStatus = "Paid".equals(order.getPaymentStatus()) ? "Payment Completed" : "Payment Pending";
            document.add(new Paragraph("Payment Status: " + paymentStatus));
            
            document.add(new Paragraph("Supplier Reference: " + order.getSupplierId()));
            document.add(new Paragraph(" "));
            
            // Project Information
            try {
                com.structurax.root.structurax.root.dto.Project1DTO projectInfo = sqsService.getProjectById(order.getProjectId());
                document.add(new Paragraph("PROJECT INFORMATION", sectionFont));
                document.add(new Paragraph("Project Name: " + (projectInfo != null ? projectInfo.getName() : "N/A")));
                document.add(new Paragraph("Project ID: " + order.getProjectId()));
                document.add(new Paragraph(" "));
            } catch (Exception e) {
                document.add(new Paragraph("PROJECT INFORMATION", sectionFont));
                document.add(new Paragraph("Project ID: " + order.getProjectId()));
                document.add(new Paragraph(" "));
            }
            
            // Detailed items table
            document.add(new Paragraph("DETAILED ORDER BREAKDOWN", sectionFont));
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            float[] columnWidths = {0.5f, 2f, 2f, 1.5f, 1f, 1.5f};
            table.setWidths(columnWidths);
            
            // Table headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            table.addCell(new Paragraph("#", headerFont));
            table.addCell(new Paragraph("Item Description", headerFont));
            table.addCell(new Paragraph("Specifications", headerFont));
            table.addCell(new Paragraph("Unit Price", headerFont));
            table.addCell(new Paragraph("Qty", headerFont));
            table.addCell(new Paragraph("Amount", headerFont));
            
            // Add items to table
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            int itemNumber = 1;
            
            for (OrderItemDTO item : items) {
                table.addCell(String.valueOf(itemNumber++));
                table.addCell(item.getDescription() != null ? item.getDescription() : "N/A");
                table.addCell("Standard specifications"); // You might want to add this to your DTO
                double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0;
                int quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 0;
                table.addCell(df.format(unitPrice));
                table.addCell(String.valueOf(quantity));
                
                double itemTotal = unitPrice * quantity;
                table.addCell(df.format(itemTotal));
                totalAmount += itemTotal;
            }
            
            // Add summary rows
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph("SUBTOTAL:", headerFont));
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph("TAX (0%):", headerFont));
            table.addCell(new Paragraph("0.00", headerFont));
            
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph("TOTAL:", headerFont));
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            document.add(table);
            
            // Terms and conditions
            document.add(new Paragraph(" "));
            document.add(new Paragraph("TERMS AND CONDITIONS", sectionFont));
            document.add(new Paragraph("1. All items must be delivered as per specifications."));
            document.add(new Paragraph("2. Payment terms: Net 30 days from delivery."));
            document.add(new Paragraph("3. Any defective items must be replaced at supplier's cost."));
            
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating detailed purchase order PDF", e);
        }
    }
}
