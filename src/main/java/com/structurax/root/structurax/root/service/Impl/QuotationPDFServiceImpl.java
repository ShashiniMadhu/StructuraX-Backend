package com.structurax.root.structurax.root.service.Impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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
import com.structurax.root.structurax.root.dao.QuotationDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;
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
    
    @Override
    public byte[] generateQuotationPdf(Integer quotationId) {
        QuotationDTO quotation = quotationDAO.getQuotationById(quotationId);
        List<QuotationItemDTO> items = quotationDAO.getQuotationItemsByQuotationId(quotationId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Company Header
            Font companyFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Paragraph companyName = new Paragraph("StructuraX", companyFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            
            // Document Header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("QUOTATION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Quotation Information Section
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("QUOTATION INFORMATION", sectionFont));
            document.add(new Paragraph("Quotation ID: " + quotation.getQId()));
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            document.add(new Paragraph("Date: " + (quotation.getDate() != null ? quotation.getDate().format(dateFormatter) : "N/A")));
            document.add(new Paragraph("Deadline: " + (quotation.getDeadline() != null ? quotation.getDeadline().format(dateFormatter) : "N/A")));
            
            document.add(new Paragraph("Status: " + quotation.getStatus()));
            if (quotation.getDescription() != null) {
                document.add(new Paragraph("Description: " + quotation.getDescription()));
            }
            document.add(new Paragraph(" "));
            
            // Project Information
            try {
                Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
                if (project != null) {
                    document.add(new Paragraph("PROJECT INFORMATION", sectionFont));
                    document.add(new Paragraph("Project Name: " + project.getName()));
                    document.add(new Paragraph("Project Location: " + project.getLocation()));
                    document.add(new Paragraph("Project Category: " + project.getCategory()));
                    document.add(new Paragraph("Project Status: " + project.getStatus()));
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                // If project info can't be retrieved, continue without it
                System.err.println("Could not retrieve project information for quotation: " + quotationId);
            }
            
            // QS Information
            try {
                EmployeeDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
                if (qsEmployee != null) {
                    document.add(new Paragraph("QUANTITY SURVEYOR INFORMATION", sectionFont));
                    document.add(new Paragraph("QS Name: " + qsEmployee.getName()));
                    document.add(new Paragraph("QS Email: " + qsEmployee.getEmail()));
                    document.add(new Paragraph("QS Phone: " + qsEmployee.getPhoneNumber()));
                    document.add(new Paragraph("QS Office Address: " + qsEmployee.getAddress()));
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                // If QS info can't be retrieved, continue without it
                System.err.println("Could not retrieve QS information for quotation: " + quotationId);
            }
            
            // Selected Suppliers Information
            try {
                List<QuotationResponseWithSupplierDTO> supplierResponses = 
                    quotationResponseService.getQuotationResponsesWithSupplierByQuotationId(quotationId);
                
                if (supplierResponses != null && !supplierResponses.isEmpty()) {
                    document.add(new Paragraph("PARTICIPATING SUPPLIERS", sectionFont));
                    
                    // Create suppliers table
                    PdfPTable suppliersTable = new PdfPTable(4);
                    suppliersTable.setWidthPercentage(100);
                    suppliersTable.setSpacingBefore(10);
                    
                    float[] supplierColumnWidths = {2f, 2f, 2f, 1.5f};
                    suppliersTable.setWidths(supplierColumnWidths);
                    
                    // Supplier table headers
                    Font supplierHeaderFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                    suppliersTable.addCell(new Paragraph("Supplier Name", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Contact Email", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Phone Number", supplierHeaderFont));
                    suppliersTable.addCell(new Paragraph("Status", supplierHeaderFont));
                    
                    // Add supplier data
                    for (QuotationResponseWithSupplierDTO supplier : supplierResponses) {
                        suppliersTable.addCell(supplier.getSupplierName() != null ? supplier.getSupplierName() : "N/A");
                        suppliersTable.addCell(supplier.getSupplierEmail() != null ? supplier.getSupplierEmail() : "N/A");
                        suppliersTable.addCell(supplier.getSupplierPhone() != null ? supplier.getSupplierPhone() : "N/A");
                        suppliersTable.addCell(supplier.getStatus() != null ? supplier.getStatus() : "N/A");
                    }
                    
                    document.add(suppliersTable);
                    document.add(new Paragraph(" "));
                }
            } catch (Exception e) {
                // If supplier info can't be retrieved, continue without it
                System.err.println("Could not retrieve supplier information for quotation: " + quotationId);
            }
            
            // Quotation Items Table
            document.add(new Paragraph("QUOTATION ITEMS", sectionFont));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Set column widths
            float[] columnWidths = {4f, 1.5f, 1f, 1.5f};
            table.setWidths(columnWidths);
            
            // Table headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            table.addCell(new Paragraph("Item Name", headerFont));
            table.addCell(new Paragraph("Amount", headerFont));
            table.addCell(new Paragraph("Quantity", headerFont));
            table.addCell(new Paragraph("Total", headerFont));
            
            // Add items to table
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
            
            // Add total row
            table.addCell(new Paragraph("TOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating quotation PDF", e);
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
            document.add(new Paragraph("Date: " + (quotation.getDate() != null ? quotation.getDate().format(dateFormatter) : "N/A")));
            
            // Add basic project and QS info for simple format
            try {
                Project1DTO project = sqsService.getProjectById(quotation.getProjectId());
                if (project != null) {
                    document.add(new Paragraph("Project: " + project.getName()));
                    document.add(new Paragraph("Location: " + project.getLocation()));
                }
                
                EmployeeDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
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
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            document.add(new Paragraph("Date: " + (quotation.getDate() != null ? quotation.getDate().format(dateFormatter) : "N/A")));
            document.add(new Paragraph("Deadline: " + (quotation.getDeadline() != null ? quotation.getDeadline().format(dateFormatter) : "N/A")));
            
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
                EmployeeDTO qsEmployee = adminService.getEmployeeById(quotation.getQsId());
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
