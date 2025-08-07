package com.structurax.root.structurax.root.service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
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
import com.structurax.root.structurax.root.dao.BOQDAO;
import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithProjectDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;

@Service
public class BOQPDFService {
    @Autowired
    private BOQDAO boqDAO;

    public byte[] generateBoqPdf(String boqId) {
        BOQDTO boq = boqDAO.getBOQById(boqId);
        List<BOQitemDTO> items = boqDAO.getBOQItemsByBOQId(boqId);
        
        // Get enhanced BOQ data with project information
        List<BOQWithProjectDTO> boqWithProjectList = boqDAO.getAllBOQsWithProjectInfo();
        BOQWithProjectDTO boqWithProject = boqWithProjectList.stream()
            .filter(b -> b.getBoq().getBoqId().equals(boqId))
            .findFirst()
            .orElse(null);
            
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Header
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("BILL OF QUANTITIES (BOQ)", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // BOQ Information Section
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("BOQ INFORMATION", sectionFont));
            document.add(new Paragraph("BOQ ID: " + boq.getBoqId()));
            document.add(new Paragraph("Date: " + boq.getDate()));
            document.add(new Paragraph("Status: " + boq.getStatus()));
            document.add(new Paragraph(" "));
            
            // Project Information Section (if available)
            if (boqWithProject != null) {
                document.add(new Paragraph("PROJECT INFORMATION", sectionFont));
                document.add(new Paragraph("Project Name: " + (boqWithProject.getProjectName() != null ? boqWithProject.getProjectName() : "N/A")));
                document.add(new Paragraph("Project Location: " + (boqWithProject.getProjectLocation() != null ? boqWithProject.getProjectLocation() : "N/A")));
                document.add(new Paragraph("Project Category: " + (boqWithProject.getProjectCategory() != null ? boqWithProject.getProjectCategory() : "N/A")));
                document.add(new Paragraph("Quantity Surveyor: " + (boqWithProject.getQsName() != null ? boqWithProject.getQsName() : "N/A")));
                document.add(new Paragraph(" "));
            }
            
            // BOQ Items Table
            document.add(new Paragraph("BOQ ITEMS", sectionFont));
            PdfPTable table = new PdfPTable(5); // Removed Item ID from display
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Set column widths
            float[] columnWidths = {3f, 1f, 1f, 1f, 1.5f};
            table.setWidths(columnWidths);
            
            // Table headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            PdfPTable headerTable = new PdfPTable(5);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(columnWidths);
            
            table.addCell(new Paragraph("Item Description", headerFont));
            table.addCell(new Paragraph("Rate", headerFont));
            table.addCell(new Paragraph("Unit", headerFont));
            table.addCell(new Paragraph("Quantity", headerFont));
            table.addCell(new Paragraph("Amount", headerFont));
            
            // Add items to table
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            
            for (BOQitemDTO item : items) {
                table.addCell(item.getItemDescription());
                table.addCell(df.format(item.getRate()));
                table.addCell(item.getUnit());
                table.addCell(df.format(item.getQuantity()));
                table.addCell(df.format(item.getAmount()));
                totalAmount += item.getAmount();
            }
            
            // Add total row
            table.addCell(new Paragraph("TOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
    
    /**
     * Generate a simple BOQ PDF with minimal information
     */
    public byte[] generateSimpleBoqPdf(String boqId) {
        BOQDTO boq = boqDAO.getBOQById(boqId);
        List<BOQitemDTO> items = boqDAO.getBOQItemsByBOQId(boqId);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Simple header
            document.add(new Paragraph("BOQ ID: " + boq.getBoqId()));
            document.add(new Paragraph("Date: " + boq.getDate()));
            document.add(new Paragraph(" "));
            
            // Simple table
            PdfPTable table = new PdfPTable(4);
            table.addCell("Description");
            table.addCell("Quantity");
            table.addCell("Rate");
            table.addCell("Amount");
            
            for (BOQitemDTO item : items) {
                table.addCell(item.getItemDescription());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getRate()));
                table.addCell(String.valueOf(item.getAmount()));
            }
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating simple PDF", e);
        }
    }
    
    /**
     * Generate a detailed BOQ PDF with project information and calculations
     */
    public byte[] generateDetailedBoqPdf(String boqId) {
        BOQDTO boq = boqDAO.getBOQById(boqId);
        List<BOQitemDTO> items = boqDAO.getBOQItemsByBOQId(boqId);
        
        // Get enhanced BOQ data with project information
        List<BOQWithProjectDTO> boqWithProjectList = boqDAO.getAllBOQsWithProjectInfo();
        BOQWithProjectDTO boqWithProject = boqWithProjectList.stream()
            .filter(b -> b.getBoq().getBoqId().equals(boqId))
            .findFirst()
            .orElse(null);
            
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Detailed header with company info
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("DETAILED BILL OF QUANTITIES", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Document information
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("DOCUMENT INFORMATION", sectionFont));
            document.add(new Paragraph("Document Type: Bill of Quantities"));
            document.add(new Paragraph("BOQ Reference: " + boq.getBoqId()));
            document.add(new Paragraph("Preparation Date: " + boq.getDate()));
            document.add(new Paragraph("Document Status: " + boq.getStatus()));
            document.add(new Paragraph(" "));
            
            // Project details
            if (boqWithProject != null) {
                document.add(new Paragraph("PROJECT DETAILS", sectionFont));
                document.add(new Paragraph("Project Name: " + (boqWithProject.getProjectName() != null ? boqWithProject.getProjectName() : "N/A")));
                document.add(new Paragraph("Location: " + (boqWithProject.getProjectLocation() != null ? boqWithProject.getProjectLocation() : "N/A")));
                document.add(new Paragraph("Category: " + (boqWithProject.getProjectCategory() != null ? boqWithProject.getProjectCategory() : "N/A")));
                document.add(new Paragraph("Quantity Surveyor: " + (boqWithProject.getQsName() != null ? boqWithProject.getQsName() : "N/A")));
                document.add(new Paragraph(" "));
            }
            
            // Detailed items table
            document.add(new Paragraph("ITEMIZED BREAKDOWN", sectionFont));
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            
            float[] columnWidths = {0.5f, 3f, 1f, 1f, 1f, 1.5f, 0.8f};
            table.setWidths(columnWidths);
            
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            table.addCell(new Paragraph("S.No", headerFont));
            table.addCell(new Paragraph("Description", headerFont));
            table.addCell(new Paragraph("Unit", headerFont));
            table.addCell(new Paragraph("Qty", headerFont));
            table.addCell(new Paragraph("Rate", headerFont));
            table.addCell(new Paragraph("Amount", headerFont));
            table.addCell(new Paragraph("Item ID", headerFont));
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            int serialNo = 1;
            
            for (BOQitemDTO item : items) {
                table.addCell(String.valueOf(serialNo++));
                table.addCell(item.getItemDescription());
                table.addCell(item.getUnit());
                table.addCell(df.format(item.getQuantity()));
                table.addCell(df.format(item.getRate()));
                table.addCell(df.format(item.getAmount()));
                table.addCell(item.getItemId());
                totalAmount += item.getAmount();
            }
            
            // Summary rows
            table.addCell("");
            table.addCell(new Paragraph("SUB TOTAL", headerFont));
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell(new Paragraph(df.format(totalAmount), headerFont));
            table.addCell("");
            
            document.add(table);
            
            // Summary section
            document.add(new Paragraph(" "));
            document.add(new Paragraph("SUMMARY", sectionFont));
            document.add(new Paragraph("Total Number of Items: " + items.size()));
            document.add(new Paragraph("Total Project Value: " + df.format(totalAmount)));
            
            document.close();
            return out.toByteArray();
        } catch (DocumentException | java.io.IOException e) {
            throw new RuntimeException("Error generating detailed PDF", e);
        }
    }
}
