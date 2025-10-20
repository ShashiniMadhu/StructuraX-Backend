package com.structurax.root.structurax.root.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
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
import com.structurax.root.structurax.root.dao.BOQDAO;
import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithProjectDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;

@Service
public class BOQPDFService {

    @Autowired
    private BOQDAO boqDAO;

    // Define brand colors and fonts once to be reused
    private static final BaseColor COLOR_GOLD = new BaseColor(253, 186, 18); // #FDBA12
    private static final BaseColor COLOR_DARK_TEXT = new BaseColor(34, 34, 34); // #222222
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, COLOR_DARK_TEXT);
    private static final Font FONT_TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    private static final Font FONT_BODY = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_DARK_TEXT);
    private static final Font FONT_BODY_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_DARK_TEXT);

    /**
     * Generates a professionally styled and branded Bill of Quantities PDF.
     */
    public byte[] generateBrandedBoqPdf(String boqId) {
        // 1. Fetch all necessary data
        BOQDTO boq = boqDAO.getBOQById(boqId);
        List<BOQitemDTO> items = boqDAO.getBOQItemsByBOQId(boqId);
        BOQWithProjectDTO boqWithProject = boqDAO.getAllBOQsWithProjectInfo().stream()
                .filter(b -> b.getBoq().getBoqId().equals(boqId))
                .findFirst()
                .orElse(null);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 90, 50); // L, R, Top, Bottom margins
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // 2. Attach the custom styler for headers and footers
            writer.setPageEvent(new BoqPdfStyler());
            document.open();

            // 3. Add content using helper methods
            addDocumentTitle(document, "BILL OF QUANTITIES");
            addProjectInfoSection(document, boq, boqWithProject);
            addItemsTable(document, items);

            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            // It's good practice to log the exception here
            throw new RuntimeException("Error generating branded BOQ PDF", e);
        }
    }

    /**
     * Generates enhanced BOQ PDF (default method)
     */
    public byte[] generateBoqPdf(String boqId) {
        return generateBrandedBoqPdf(boqId);
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
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("BILL OF QUANTITIES", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("BOQ ID: " + boq.getBoqId()));
            document.add(new Paragraph("Date: " + (boq.getDate() != null ? boq.getDate().toString() : "N/A")));
            document.add(new Paragraph(" "));
            
            // Simple table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            table.addCell("Description");
            table.addCell("Unit");
            table.addCell("Quantity");
            table.addCell("Rate");
            table.addCell("Amount");
            
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double totalAmount = 0.0;
            
            for (BOQitemDTO item : items) {
                table.addCell(item.getItemDescription());
                table.addCell(item.getUnit());
                table.addCell(df.format(item.getQuantity()));
                table.addCell(df.format(item.getRate()));
                table.addCell(df.format(item.getAmount()));
                totalAmount += item.getAmount();
            }
            
            // Total row
            table.addCell("TOTAL");
            table.addCell("");
            table.addCell("");
            table.addCell("");
            table.addCell(df.format(totalAmount));
            
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
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
        BOQWithProjectDTO boqWithProject = boqDAO.getAllBOQsWithProjectInfo().stream()
            .filter(b -> b.getBoq().getBoqId().equals(boqId))
            .findFirst()
            .orElse(null);
            
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Detailed header with company info
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("DETAILED BILL OF QUANTITIES", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Document information
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(new Paragraph("DOCUMENT INFORMATION", sectionFont));
            document.add(new Paragraph("Document Type: Bill of Quantities"));
            document.add(new Paragraph("BOQ Reference: " + boq.getBoqId()));
            document.add(new Paragraph("Preparation Date: " + (boq.getDate() != null ? boq.getDate().toString() : "N/A")));
            document.add(new Paragraph("Document Status: " + (boq.getStatus() != null ? boq.getStatus().toString() : "N/A")));
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
            
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
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
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error generating detailed PDF", e);
        }
    }

    // --- Private Helper Methods for Clean Code ---

    private void addDocumentTitle(Document document, String title) throws DocumentException {
        Paragraph titlePara = new Paragraph(title, FONT_TITLE);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(25f);
        document.add(titlePara);
    }

    private void addProjectInfoSection(Document document, BOQDTO boq, BOQWithProjectDTO projectInfo) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4); // 4 columns for balanced layout
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20f);

        // Add cells using a helper for styling
        infoTable.addCell(createLabelCell("Project Name:"));
        infoTable.addCell(createValueCell(projectInfo != null ? projectInfo.getProjectName() : "N/A"));
        infoTable.addCell(createLabelCell("BOQ Reference #:"));
        infoTable.addCell(createValueCell(boq.getBoqId()));

        infoTable.addCell(createLabelCell("Project Location:"));
        infoTable.addCell(createValueCell(projectInfo != null ? projectInfo.getProjectLocation() : "N/A"));
        infoTable.addCell(createLabelCell("Date Issued:"));
        infoTable.addCell(createValueCell(boq.getDate() != null ? boq.getDate().toString() : "N/A"));
        
        infoTable.addCell(createLabelCell("Quantity Surveyor:"));
        infoTable.addCell(createValueCell(projectInfo != null ? projectInfo.getQsName() : "N/A"));
        infoTable.addCell(createLabelCell("Status:"));
        infoTable.addCell(createValueCell(boq.getStatus() != null ? boq.getStatus().toString() : "N/A"));

        document.add(infoTable);
    }
    
    private void addItemsTable(Document document, List<BOQitemDTO> items) throws DocumentException {
        float[] columnWidths = {0.7f, 4.5f, 1f, 1.2f, 1.2f, 1.5f};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setHeaderRows(1); // This repeats the header on new pages

        // Header cells
        addHeaderCell(table, "No.");
        addHeaderCell(table, "Item Description");
        addHeaderCell(table, "Unit");
        addHeaderCell(table, "Quantity");
        addHeaderCell(table, "Rate");
        addHeaderCell(table, "Amount");

        DecimalFormat df = new DecimalFormat("#,##0.00");
        double totalAmount = 0.0;
        int serialNo = 1;

        // Data rows
        for (BOQitemDTO item : items) {
            table.addCell(createDataCell(String.valueOf(serialNo++), Element.ALIGN_CENTER));
            table.addCell(createDataCell(item.getItemDescription(), Element.ALIGN_LEFT));
            table.addCell(createDataCell(item.getUnit(), Element.ALIGN_CENTER));
            table.addCell(createDataCell(df.format(item.getQuantity()), Element.ALIGN_RIGHT));
            table.addCell(createDataCell(df.format(item.getRate()), Element.ALIGN_RIGHT));
            table.addCell(createDataCell(df.format(item.getAmount()), Element.ALIGN_RIGHT));
            totalAmount += item.getAmount();
        }

        // Total row
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("GRAND TOTAL", FONT_BODY_BOLD));
        totalLabelCell.setColspan(5);
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
    
    // --- Cell Creation Helpers for Styling ---

    private PdfPCell createLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_BODY_BOLD));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell createValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_BODY));
        cell.setBorder(Rectangle.NO_BORDER);
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


    // --- Inner Class for Page Headers and Footers ---

    static class BoqPdfStyler extends PdfPageEventHelper {
        private final Font FONT_FOOTER = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
        private final Font FONT_HEADER_BRAND = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_DARK_TEXT);

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            try {
                // Header
                PdfPTable headerTable = new PdfPTable(2);
                headerTable.setWidthPercentage(100);
                headerTable.setTotalWidth(document.right() - document.left());

                // Left side: Logo
                Phrase logoPhrase = new Phrase();
                logoPhrase.add(new Chunk("Structura", FONT_HEADER_BRAND));
                logoPhrase.add(new Chunk("X", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_GOLD)));
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

                // Write table to the absolute position on the page
                headerTable.writeSelectedRows(0, -1, document.leftMargin(), document.top() + 45, writer.getDirectContent());

            } catch (Exception e) {
                // handle exception
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // Footer with page number
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(String.format("Page %d", writer.getPageNumber()), FONT_FOOTER),
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}