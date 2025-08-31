package com.structurax.root.structurax.root.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.service.InvoicePDFService;
import com.structurax.root.structurax.root.service.PurchaseOrderPDFService;
import com.structurax.root.structurax.root.service.QuotationPDFService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/pdf")
public class QuotationPDFController {
    
    @Autowired
    private QuotationPDFService quotationPDFService;
    
    @Autowired
    private InvoicePDFService invoicePDFService;
    
    @Autowired
    private PurchaseOrderPDFService purchaseOrderPDFService;

    // ==================== QUOTATION PDF ENDPOINTS ====================

    /**
     * Download Quotation PDF with enhanced formatting (default)
     */
    @GetMapping("/quotation/{quotationId}/download")
    public ResponseEntity<byte[]> downloadQuotationPdf(@PathVariable("quotationId") Integer quotationId) {
        try {
            byte[] pdfBytes = quotationPDFService.generateQuotationPdf(quotationId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "quotation_" + quotationId + "_standard.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating quotation PDF: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * Download Quotation PDF with different format options
     */
    @GetMapping("/quotation/{quotationId}/download/format")
    public ResponseEntity<byte[]> downloadQuotationPdfWithFormat(
            @PathVariable("quotationId") Integer quotationId, 
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            byte[] pdfBytes;
            String filename;
            
            switch (format.toLowerCase()) {
                case "simple":
                    pdfBytes = quotationPDFService.generateSimpleQuotationPdf(quotationId);
                    filename = "quotation_" + quotationId + "_simple.pdf";
                    break;
                case "detailed":
                    pdfBytes = quotationPDFService.generateDetailedQuotationPdf(quotationId);
                    filename = "quotation_" + quotationId + "_detailed.pdf";
                    break;
                case "standard":
                default:
                    pdfBytes = quotationPDFService.generateQuotationPdf(quotationId);
                    filename = "quotation_" + quotationId + "_standard.pdf";
                    break;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating quotation PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Preview Quotation PDF in browser
     */
    @GetMapping("/quotation/{quotationId}/preview")
    public ResponseEntity<byte[]> previewQuotationPdf(
            @PathVariable("quotationId") Integer quotationId,
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            byte[] pdfBytes;
            
            switch (format.toLowerCase()) {
                case "simple":
                    pdfBytes = quotationPDFService.generateSimpleQuotationPdf(quotationId);
                    break;
                case "detailed":
                    pdfBytes = quotationPDFService.generateDetailedQuotationPdf(quotationId);
                    break;
                case "standard":
                default:
                    pdfBytes = quotationPDFService.generateQuotationPdf(quotationId);
                    break;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "quotation_" + quotationId + "_preview.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating quotation PDF preview: " + e.getMessage()).getBytes());
        }
    }

    // ==================== INVOICE PDF ENDPOINTS ====================

    /**
     * Download Invoice PDF (generated from purchase order)
     */
    @GetMapping("/invoice/{orderId}/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable("orderId") Integer orderId) {
        try {
            byte[] pdfBytes = invoicePDFService.generateInvoicePdf(orderId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_INV-" + orderId + "_standard.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating invoice PDF: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * Download Invoice PDF with different format options
     */
    @GetMapping("/invoice/{orderId}/download/format")
    public ResponseEntity<byte[]> downloadInvoicePdfWithFormat(
            @PathVariable("orderId") Integer orderId, 
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            byte[] pdfBytes;
            String filename;
            
            switch (format.toLowerCase()) {
                case "simple":
                    pdfBytes = invoicePDFService.generateSimpleInvoicePdf(orderId);
                    filename = "invoice_INV-" + orderId + "_simple.pdf";
                    break;
                case "detailed":
                    pdfBytes = invoicePDFService.generateDetailedInvoicePdf(orderId);
                    filename = "invoice_INV-" + orderId + "_detailed.pdf";
                    break;
                case "standard":
                default:
                    pdfBytes = invoicePDFService.generateInvoicePdf(orderId);
                    filename = "invoice_INV-" + orderId + "_standard.pdf";
                    break;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Add detailed error logging
            System.err.println("Invoice PDF generation error for order ID: " + orderId + ", Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating invoice PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Preview Invoice PDF in browser
     */
    @GetMapping("/invoice/{orderId}/preview")
    public ResponseEntity<byte[]> previewInvoicePdf(
            @PathVariable("orderId") Integer orderId,
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            byte[] pdfBytes;
            
            switch (format.toLowerCase()) {
                case "simple":
                    pdfBytes = invoicePDFService.generateSimpleInvoicePdf(orderId);
                    break;
                case "detailed":
                    pdfBytes = invoicePDFService.generateDetailedInvoicePdf(orderId);
                    break;
                case "standard":
                default:
                    pdfBytes = invoicePDFService.generateInvoicePdf(orderId);
                    break;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "invoice_INV-" + orderId + "_preview.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating invoice PDF preview: " + e.getMessage()).getBytes());
        }
    }

    // ==================== PURCHASE ORDER PDF ENDPOINTS ====================

    /**
     * Download Purchase Order PDF
     */
    @GetMapping("/purchase-order/{orderId}/download")
    public ResponseEntity<byte[]> downloadPurchaseOrderPdf(@PathVariable("orderId") Integer orderId) {
        try {
            byte[] pdfBytes = purchaseOrderPDFService.generatePurchaseOrderPdf(orderId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "purchase_order_" + orderId + "_standard.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating purchase order PDF: " + e.getMessage()).getBytes());
        }
    }
    
    /**
     * Download Purchase Order PDF with different format options
     */
    @GetMapping("/purchase-order/{orderId}/download/format")
    public ResponseEntity<byte[]> downloadPurchaseOrderPdfWithFormat(
            @PathVariable("orderId") Integer orderId, 
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            byte[] pdfBytes;
            String filename;
            
            switch (format.toLowerCase()) {
                case "simple":
                    pdfBytes = purchaseOrderPDFService.generateSimplePurchaseOrderPdf(orderId);
                    filename = "purchase_order_" + orderId + "_simple.pdf";
                    break;
                case "detailed":
                    pdfBytes = purchaseOrderPDFService.generateDetailedPurchaseOrderPdf(orderId);
                    filename = "purchase_order_" + orderId + "_detailed.pdf";
                    break;
                case "standard":
                default:
                    pdfBytes = purchaseOrderPDFService.generatePurchaseOrderPdf(orderId);
                    filename = "purchase_order_" + orderId + "_standard.pdf";
                    break;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating purchase order PDF: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Preview Purchase Order PDF in browser
     */
    @GetMapping("/purchase-order/{orderId}/preview")
    public ResponseEntity<byte[]> previewPurchaseOrderPdf(
            @PathVariable("orderId") Integer orderId,
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            byte[] pdfBytes;
            
            switch (format.toLowerCase()) {
                case "simple":
                    pdfBytes = purchaseOrderPDFService.generateSimplePurchaseOrderPdf(orderId);
                    break;
                case "detailed":
                    pdfBytes = purchaseOrderPDFService.generateDetailedPurchaseOrderPdf(orderId);
                    break;
                case "standard":
                default:
                    pdfBytes = purchaseOrderPDFService.generatePurchaseOrderPdf(orderId);
                    break;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "purchase_order_" + orderId + "_preview.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating purchase order PDF preview: " + e.getMessage()).getBytes());
        }
    }

    // ==================== UTILITY ENDPOINTS ====================

    /**
     * Get available PDF format types
     */
    @GetMapping("/formats")
    public ResponseEntity<String[]> getAvailableFormats() {
        String[] formats = {"simple", "standard", "detailed"};
        return ResponseEntity.ok(formats);
    }

    /**
     * Health check endpoint for PDF services
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("PDF services are running properly");
    }
}