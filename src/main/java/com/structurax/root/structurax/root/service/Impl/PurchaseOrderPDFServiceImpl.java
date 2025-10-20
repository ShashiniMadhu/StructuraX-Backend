package com.structurax.root.structurax.root.service.Impl;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.structurax.root.structurax.root.dao.PurchaseOrderDAO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.service.PurchaseOrderPDFService;
import com.structurax.root.structurax.root.service.SQSService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PurchaseOrderPDFServiceImpl implements PurchaseOrderPDFService {
    
    @Autowired
    private PurchaseOrderDAO purchaseOrderDAO;
    
    @Autowired
    private SQSService sqsService;
    
    // Define brand colors and fonts
    private static final BaseColor COLOR_GOLD = new BaseColor(253, 186, 18); // #FDBA12
    private static final BaseColor COLOR_DARK_TEXT = new BaseColor(34, 34, 34); // #222222
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, COLOR_DARK_TEXT);
    private static final Font FONT_TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_DARK_TEXT);
    private static final Font FONT_BODY_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_DARK_TEXT);
    
    @Override
    public byte[] generatePurchaseOrderPdf(Integer orderId) {
        try {
            log.info("Generating purchase order PDF for order ID: {}", orderId);
            
            PurchaseOrderDTO order = purchaseOrderDAO.getPurchaseOrderById(orderId);
            if (order == null) {
                log.error("Purchase order not found with ID: {}", orderId);
                throw new RuntimeException("Purchase order not found with ID: " + orderId);
            }
            log.info("Purchase order retrieved: {}", order.getOrderId());
            
            List<OrderItemDTO> items = purchaseOrderDAO.getPurchaseOrderItemsByOrderId(orderId);
            if (items == null || items.isEmpty()) {
                log.error("No items found for purchase order ID: {}", orderId);
                throw new RuntimeException("No items found for purchase order ID: " + orderId);
            }
            log.info("Found {} items for purchase order", items.size());
            
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Document document = new Document(PageSize.A4, 36, 36, 90, 50);
                PdfWriter writer = PdfWriter.getInstance(document, out);
                
                // Attach custom page event for headers and footers
                writer.setPageEvent(new PurchaseOrderPdfStyler());
                document.open();
                
                // Document Title
                Paragraph title = new Paragraph("PURCHASE ORDER", FONT_TITLE);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(25f);
                document.add(title);
                
                // Order Information Section
                addOrderInfoSection(document, order);
                
                // Project Information
                try {
                    String projectId = order.getProjectId();
                    if (projectId != null && !projectId.isEmpty()) {
                        com.structurax.root.structurax.root.dto.Project1DTO projectInfo = sqsService.getProjectById(projectId);
                        addProjectInfoSection(document, projectInfo, projectId);
                    }
                } catch (Exception e) {
                    // If project info fails, add minimal info
                    if (order.getProjectId() != null) {
                        addProjectInfoSection(document, null, order.getProjectId());
                    }
                }
                
                // Order Items Table
                addOrderItemsTable(document, items);
                
                document.close();
                log.info("Purchase order PDF generated successfully for order ID: {}", orderId);
                return out.toByteArray();
            }
        } catch (DocumentException e) {
            log.error("DocumentException while generating purchase order PDF for ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Document error generating purchase order PDF: " + e.getMessage(), e);
        } catch (java.io.IOException e) {
            log.error("IOException while generating purchase order PDF for ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("IO error generating purchase order PDF: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while generating purchase order PDF for ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Unexpected error generating purchase order PDF: " + e.getMessage(), e);
        }
    }
    
    // Helper methods for clean code
    
    private void addOrderInfoSection(Document document, PurchaseOrderDTO order) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        infoTable.addCell(createLabelCell("Order ID:"));
        infoTable.addCell(createValueCell(String.valueOf(order.getOrderId())));
        infoTable.addCell(createLabelCell("Supplier ID:"));
        infoTable.addCell(createValueCell(String.valueOf(order.getSupplierId())));
        
        infoTable.addCell(createLabelCell("Order Date:"));
        infoTable.addCell(createValueCell(order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "N/A"));
        infoTable.addCell(createLabelCell("Delivery Date:"));
        infoTable.addCell(createValueCell(order.getEstimatedDeliveryDate() != null ? order.getEstimatedDeliveryDate().format(dateFormatter) : "N/A"));
        
        Boolean orderStatus = order.getOrderStatus();
        String orderStatusText = (orderStatus != null && orderStatus) ? "Completed" : "Pending";
        String paymentStatus = "Paid".equals(order.getPaymentStatus()) ? "Paid" : "Pending";
        
        infoTable.addCell(createLabelCell("Order Status:"));
        infoTable.addCell(createValueCell(orderStatusText));
        infoTable.addCell(createLabelCell("Payment Status:"));
        infoTable.addCell(createValueCell(paymentStatus));
        
        document.add(infoTable);
    }
    
    private void addProjectInfoSection(Document document, com.structurax.root.structurax.root.dto.Project1DTO projectInfo, String projectId) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);
        
        infoTable.addCell(createLabelCell("Project Name:"));
        infoTable.addCell(createValueCell(projectInfo != null ? projectInfo.getName() : "N/A"));
        infoTable.addCell(createLabelCell("Project ID:"));
        infoTable.addCell(createValueCell(projectId));
        
        if (projectInfo != null) {
            infoTable.addCell(createLabelCell("Location:"));
            infoTable.addCell(createValueCell(projectInfo.getLocation()));
            infoTable.addCell(createLabelCell("Category:"));
            infoTable.addCell(createValueCell(projectInfo.getCategory()));
        }
        
        document.add(infoTable);
    }
    
    private void addOrderItemsTable(Document document, List<OrderItemDTO> items) throws DocumentException {
        float[] columnWidths = {0.7f, 4f, 1.5f, 1f, 1.5f};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(10f);
        
        // Header cells
        addHeaderCell(table, "No.");
        addHeaderCell(table, "Item Description");
        addHeaderCell(table, "Unit Price");
        addHeaderCell(table, "Quantity");
        addHeaderCell(table, "Total");
        
        DecimalFormat df = new DecimalFormat("#,##0.00");
        double totalAmount = 0.0;
        int serialNo = 1;
        
        // Data rows
        for (OrderItemDTO item : items) {
            double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0.0;
            int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            double itemTotal = unitPrice * quantity;
            
            table.addCell(createDataCell(String.valueOf(serialNo++), Element.ALIGN_CENTER));
            table.addCell(createDataCell(item.getDescription() != null ? item.getDescription() : "N/A", Element.ALIGN_LEFT));
            table.addCell(createDataCell(df.format(unitPrice), Element.ALIGN_RIGHT));
            table.addCell(createDataCell(String.valueOf(quantity), Element.ALIGN_CENTER));
            table.addCell(createDataCell(df.format(itemTotal), Element.ALIGN_RIGHT));
            totalAmount += itemTotal;
        }
        
        // Total row
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("GRAND TOTAL", FONT_BODY_BOLD));
        totalLabelCell.setColspan(4);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setPadding(8f);
        totalLabelCell.setBorderWidth(0);
        totalLabelCell.setBorderWidthTop(1f);
        table.addCell(totalLabelCell);
        
        PdfPCell totalValueCell = new PdfPCell(new Phrase(df.format(totalAmount), FONT_BODY_BOLD));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValueCell.setPadding(8f);
        totalValueCell.setBorderWidth(0);
        totalValueCell.setBorderWidthTop(1f);
        table.addCell(totalValueCell);
        
        document.add(table);
    }
    
    private PdfPCell createLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_BODY_BOLD));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5f);
        return cell;
    }
    
    private PdfPCell createValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_BODY));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5f);
        return cell;
    }
    
    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_TABLE_HEADER));
        cell.setBackgroundColor(COLOR_GOLD);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8f);
        table.addCell(cell);
    }
    
    private PdfPCell createDataCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_BODY));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }
    
    // Inner class for page headers and footers
    static class PurchaseOrderPdfStyler extends PdfPageEventHelper {
        private final Font FONT_FOOTER = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        private final Font FONT_HEADER_BRAND = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, new BaseColor(34, 34, 34));
        private final BaseColor COLOR_GOLD_INNER = new BaseColor(253, 186, 18);
        
        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            try {
                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setWidthPercentage(100);
                headerTable.setTotalWidth(document.right() - document.left());
                
                // Left side: Logo
                Phrase logoPhrase = new Phrase();
                logoPhrase.add(new Chunk("Structura", FONT_HEADER_BRAND));
                logoPhrase.add(new Chunk("X", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_GOLD_INNER)));
                PdfPCell logoCell = new PdfPCell(logoPhrase);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerTable.addCell(logoCell);
                
                // Right side: Document Title
                PdfPCell titleCell = new PdfPCell(new Phrase("CONFIDENTIAL DOCUMENT", FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY)));
                titleCell.setBorder(Rectangle.NO_BORDER);
                titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerTable.addCell(titleCell);
                
                headerTable.writeSelectedRows(0, -1, document.leftMargin(), document.top() + 45, writer.getDirectContent());
            } catch (Exception e) {
                // handle exception
            }
        }
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(String.format("Page %d", writer.getPageNumber()), FONT_FOOTER),
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
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
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            document.add(new Paragraph("Order Date: " + (order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "N/A")));
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
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            document.add(new Paragraph("Order Date: " + (order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "N/A")));
            document.add(new Paragraph("Expected Delivery: " + (order.getEstimatedDeliveryDate() != null ? order.getEstimatedDeliveryDate().format(dateFormatter) : "N/A")));
            
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
