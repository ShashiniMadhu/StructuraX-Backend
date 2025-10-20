package com.structurax.root.structurax.root.service.Impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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
import com.structurax.root.structurax.root.dao.QuotationDAO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;
import com.structurax.root.structurax.root.dto.UserDTO;
import com.structurax.root.structurax.root.service.AdminService;
import com.structurax.root.structurax.root.service.QuotationPDFService;
import com.structurax.root.structurax.root.service.QuotationResponseService;
import com.structurax.root.structurax.root.service.SQSService;

@Service
public class QuotationPDFServiceImpl implements QuotationPDFService {
    
    @Autowired
    private QuotationDAO quotationDAO;
    
    @Autowired
    private SQSService sqsService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private QuotationResponseService quotationResponseService;
    
    // Define brand colors and fonts
    private static final BaseColor COLOR_GOLD = new BaseColor(253, 186, 18); // #FDBA12
    private static final BaseColor COLOR_DARK_TEXT = new BaseColor(34, 34, 34); // #222222
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, COLOR_DARK_TEXT);
    private static final Font FONT_TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_DARK_TEXT);
    private static final Font FONT_BODY_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_DARK_TEXT);
    
    @Override
    public byte[] generateQuotationPdf(Integer quotationId) {
        QuotationDTO quotation = quotationDAO.getQuotationById(quotationId);
        List<QuotationItemDTO> items = quotationDAO.getQuotationItemsByQuotationId(quotationId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 90, 50);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            
            // Attach custom page event for headers and footers
            writer.setPageEvent(new QuotationPdfStyler());
            document.open();
            
            // Document Title
            Paragraph title = new Paragraph("QUOTATION", FONT_TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(25f);
            document.add(title);
            
            // Quotation Information Section
            addQuotationInfoSection(document, quotation);
            
            // Project Information
            try {
                Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
                if (project != null) {
                    addProjectInfoSection(document, project);
                }
            } catch (Exception e) {
                System.err.println("Could not retrieve project information for quotation: " + quotationId);
            }
            
            // QS Information
            try {
                UserDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
                if (qsEmployee != null) {
                    addQSInfoSection(document, qsEmployee);
                }
            } catch (Exception e) {
                System.err.println("Could not retrieve QS information for quotation: " + quotationId);
            }
            
            // Quotation Items Table
            addQuotationItemsTable(document, items);
            
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating quotation PDF", e);
        }
    }
    
    // Helper methods for clean code
    
    private void addQuotationInfoSection(Document document, QuotationDTO quotation) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        infoTable.addCell(createLabelCell("Quotation ID:"));
        infoTable.addCell(createValueCell(String.valueOf(quotation.getQId())));
        infoTable.addCell(createLabelCell("Status:"));
        infoTable.addCell(createValueCell(quotation.getStatus()));
        
        infoTable.addCell(createLabelCell("Date Issued:"));
        infoTable.addCell(createValueCell(quotation.getDate() != null ? quotation.getDate().toLocalDate().format(dateFormatter) : "N/A"));
        infoTable.addCell(createLabelCell("Deadline:"));
        infoTable.addCell(createValueCell(quotation.getDeadline() != null ? quotation.getDeadline().toLocalDate().format(dateFormatter) : "N/A"));
        
        if (quotation.getDescription() != null) {
            infoTable.addCell(createLabelCell("Description:"));
            PdfPCell descCell = createValueCell(quotation.getDescription());
            descCell.setColspan(3);
            infoTable.addCell(descCell);
        }
        
        document.add(infoTable);
    }
    
    private void addProjectInfoSection(Document document, Project1DTO project) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);
        
        infoTable.addCell(createLabelCell("Project Name:"));
        infoTable.addCell(createValueCell(project.getName()));
        infoTable.addCell(createLabelCell("Category:"));
        infoTable.addCell(createValueCell(project.getCategory()));
        
        infoTable.addCell(createLabelCell("Location:"));
        infoTable.addCell(createValueCell(project.getLocation()));
        infoTable.addCell(createLabelCell("Status:"));
        infoTable.addCell(createValueCell(project.getStatus()));
        
        document.add(infoTable);
    }
    
    private void addQSInfoSection(Document document, UserDTO qsEmployee) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);
        
        infoTable.addCell(createLabelCell("Quantity Surveyor:"));
        infoTable.addCell(createValueCell(qsEmployee.getName()));
        infoTable.addCell(createLabelCell("Email:"));
        infoTable.addCell(createValueCell(qsEmployee.getEmail()));
        
        infoTable.addCell(createLabelCell("Phone:"));
        infoTable.addCell(createValueCell(qsEmployee.getPhoneNumber()));
        infoTable.addCell(createLabelCell("Address:"));
        infoTable.addCell(createValueCell(qsEmployee.getAddress()));
        
        document.add(infoTable);
    }
    
    private void addQuotationItemsTable(Document document, List<QuotationItemDTO> items) throws DocumentException {
        float[] columnWidths = {0.7f, 3.5f, 1.5f, 1f, 1.5f};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        table.setSpacingBefore(10f);
        
        // Header cells
        addHeaderCell(table, "No.");
        addHeaderCell(table, "Item Name");
        addHeaderCell(table, "Amount");
        addHeaderCell(table, "Quantity");
        addHeaderCell(table, "Total");
        
        DecimalFormat df = new DecimalFormat("#,##0.00");
        BigDecimal totalAmount = BigDecimal.ZERO;
        int serialNo = 1;
        
        // Data rows
        for (QuotationItemDTO item : items) {
            BigDecimal itemTotal = item.getAmount().multiply(new BigDecimal(item.getQuantity()));
            table.addCell(createDataCell(String.valueOf(serialNo++), Element.ALIGN_CENTER));
            table.addCell(createDataCell(item.getName(), Element.ALIGN_LEFT));
            table.addCell(createDataCell(df.format(item.getAmount()), Element.ALIGN_RIGHT));
            table.addCell(createDataCell(String.valueOf(item.getQuantity()), Element.ALIGN_CENTER));
            table.addCell(createDataCell(df.format(itemTotal), Element.ALIGN_RIGHT));
            totalAmount = totalAmount.add(itemTotal);
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
    static class QuotationPdfStyler extends PdfPageEventHelper {
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
    public byte[] generateSimpleQuotationPdf(Integer quotationId) {
        QuotationDTO quotation = quotationDAO.getQuotationById(quotationId);
        List<QuotationItemDTO> items = quotationDAO.getQuotationItemsByQuotationId(quotationId);
        
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
            document.add(new Paragraph("Quotation ID: " + quotation.getQId()));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            document.add(new Paragraph("Date: " + (quotation.getDate() != null ? quotation.getDate().toLocalDate().format(dateFormatter) : "N/A")));

            // Add basic project and QS info for simple format
            try {
                Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
                if (project != null) {
                    document.add(new Paragraph("Project: " + project.getName()));
                    document.add(new Paragraph("Location: " + project.getLocation()));
                }
                
                UserDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
                if (qsEmployee != null) {
                    document.add(new Paragraph("QS: " + qsEmployee.getName() + " (" + qsEmployee.getEmail() + ")"));
                }
            } catch (Exception e) {
                // Continue without additional info if retrieval fails
                System.err.println("Could not retrieve additional information for simple quotation: " + quotationId);
            }
            
            document.add(new Paragraph(" "));
            
            // Simple table
            PdfPTable table = new PdfPTable(4);
            table.addCell("Name");
            table.addCell("Amount");
            table.addCell("Quantity");
            table.addCell("Total");
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (QuotationItemDTO item : items) {
                BigDecimal itemTotal = item.getAmount().multiply(new BigDecimal(item.getQuantity()));
                table.addCell(item.getName());
                table.addCell(df.format(item.getAmount()));
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(df.format(itemTotal));
                totalAmount = totalAmount.add(itemTotal);
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
            throw new RuntimeException("Error generating simple quotation PDF", e);
        }
    }
    
    @Override
    public byte[] generateDetailedQuotationPdf(Integer quotationId) {
        QuotationDTO quotation = quotationDAO.getQuotationById(quotationId);
        List<QuotationItemDTO> items = quotationDAO.getQuotationItemsByQuotationId(quotationId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Company Header
            Font companyFont = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD);
            Paragraph companyName = new Paragraph("StructuraX", companyFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            
            // Detailed header with company info
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("DETAILED QUOTATION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Document information
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("DOCUMENT INFORMATION", sectionFont));
            document.add(new Paragraph("Document Type: Quotation"));
            document.add(new Paragraph("Quotation Reference: " + quotation.getQId()));
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            document.add(new Paragraph("Date: " + (quotation.getDate() != null ? quotation.getDate().toLocalDate().format(dateFormatter) : "N/A")));
            document.add(new Paragraph("Deadline: " + (quotation.getDeadline() != null ? quotation.getDeadline().toLocalDate().format(dateFormatter) : "N/A")));

            document.add(new Paragraph("Status: " + quotation.getStatus()));
            if (quotation.getDescription() != null) {
                document.add(new Paragraph("Description: " + quotation.getDescription()));
            }
            document.add(new Paragraph(" "));
            
            // Project Information
            try {
                Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
                if (project != null) {
                    document.add(new Paragraph("PROJECT DETAILS", sectionFont));
                    document.add(new Paragraph("Project Name: " + project.getName()));
                    document.add(new Paragraph("Project ID: " + quotation.getProjectId()));
                    document.add(new Paragraph("Project Location: " + project.getLocation()));
                    document.add(new Paragraph("Project Category: " + project.getCategory()));
                    document.add(new Paragraph("Project Status: " + project.getStatus()));
                    if (project.getDescription() != null) {
                        document.add(new Paragraph("Project Description: " + project.getDescription()));
                    }
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                // If project info can't be retrieved, continue without it
                System.err.println("Could not retrieve project information for quotation: " + quotationId);
            }
            
            // QS Information
            try {
                UserDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
                if (qsEmployee != null) {
                    document.add(new Paragraph("QUANTITY SURVEYOR DETAILS", sectionFont));
                    document.add(new Paragraph("QS ID: " + quotation.getQsId()));
                    document.add(new Paragraph("QS Name: " + qsEmployee.getName()));
                    document.add(new Paragraph("QS Email: " + qsEmployee.getEmail()));
                    document.add(new Paragraph("QS Phone: " + qsEmployee.getPhoneNumber()));
                    document.add(new Paragraph("QS Office Address: " + qsEmployee.getAddress()));
                    document.add(new Paragraph("QS Type: " + qsEmployee.getType()));
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                // If QS info can't be retrieved, continue without it
                System.err.println("Could not retrieve QS information for quotation: " + quotationId);
            }
            
            // Detailed Suppliers Information
            try {
                List<QuotationResponseWithSupplierDTO> supplierResponses = 
                    quotationResponseService.getQuotationResponsesWithSupplierByQuotationId(quotationId);
                
                if (supplierResponses != null && !supplierResponses.isEmpty()) {
                    document.add(new Paragraph("SUPPLIER DETAILS & RESPONSES", sectionFont));
                    
                    // Create detailed suppliers table
                    PdfPTable suppliersTable = new PdfPTable(7);
                    suppliersTable.setWidthPercentage(100);
                    suppliersTable.setSpacingBefore(10);
                    
                    float[] supplierColumnWidths = {1.5f, 1.8f, 1.5f, 1.5f, 1.2f, 1f, 1.5f};
                    suppliersTable.setWidths(supplierColumnWidths);
                    
                    // Detailed supplier table headers
                    Font supplierHeaderFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
                    suppliersTable.addCell(new Paragraph("Supplier Name", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Email", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Phone", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Address", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Total Amount", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Status", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Delivery Date", supplierHeaderFont));
                    
                    // Add detailed supplier data
                    DecimalFormat supplierDf = new DecimalFormat("#,##0.00");
                    DateTimeFormatter supplierDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    
                    for (QuotationResponseWithSupplierDTO supplier : supplierResponses) {
                        suppliersTable.addCell(supplier.getSupplierName() != null ? supplier.getSupplierName() : "N/A");
                        suppliersTable.addCell(supplier.getSupplierEmail() != null ? supplier.getSupplierEmail() : "N/A");
                        suppliersTable.addCell(supplier.getSupplierPhone() != null ? supplier.getSupplierPhone() : "N/A");
                        suppliersTable.addCell(supplier.getSupplierAddress() != null ? supplier.getSupplierAddress() : "N/A");
                        suppliersTable.addCell(supplier.getTotalAmount() != null ? supplierDf.format(supplier.getTotalAmount()) : "N/A");
                        suppliersTable.addCell(supplier.getStatus() != null ? supplier.getStatus() : "N/A");
                        suppliersTable.addCell(supplier.getDeliveryDate() != null ? supplier.getDeliveryDate().format(supplierDateFormatter) : "N/A");
                    }
                    
                    document.add(suppliersTable);
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                // If supplier info can't be retrieved, continue without it
                System.err.println("Could not retrieve supplier information for quotation: " + quotationId);
            }
            
            // Detailed items table
            document.add(new Paragraph("ITEMIZED BREAKDOWN", sectionFont));
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            
            float[] columnWidths = {0.5f, 2f, 2f, 1.5f, 1f, 1.5f};
            table.setWidths(columnWidths);
            
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            table.addCell(new Paragraph("S.No", headerFont));
            table.addCell(new Paragraph("Item Name", headerFont));
            table.addCell(new Paragraph("Description", headerFont));
            table.addCell(new Paragraph("Amount", headerFont));
            table.addCell(new Paragraph("Quantity", headerFont));
            table.addCell(new Paragraph("Line Total", headerFont));
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            BigDecimal totalAmount = BigDecimal.ZERO;
            int serialNo = 1;
            
            for (QuotationItemDTO item : items) {
                BigDecimal itemTotal = item.getAmount().multiply(new BigDecimal(item.getQuantity()));
                table.addCell(String.valueOf(serialNo++));
                table.addCell(item.getName());
                table.addCell(item.getDescription() != null ? item.getDescription() : "");
                table.addCell(df.format(item.getAmount()));
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(df.format(itemTotal));
                totalAmount = totalAmount.add(itemTotal);
            }
            
            // Summary rows
            table.addCell("");
            table.addCell(new Paragraph("SUB TOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            document.add(table);
            
            // Summary section
            document.add(new Paragraph(" "));
            document.add(new Paragraph("QUOTATION SUMMARY", sectionFont));
            document.add(new Paragraph("Total Number of Items: " + items.size()));
            document.add(new Paragraph("Total Quotation Value: " + df.format(totalAmount)));
            
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating detailed quotation PDF", e);
        }
    }
}
