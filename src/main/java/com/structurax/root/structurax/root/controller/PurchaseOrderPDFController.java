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

import com.structurax.root.structurax.root.service.PurchaseOrderPDFService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/purchase-order-pdf")
public class PurchaseOrderPDFController {
    
    @Autowired
    private PurchaseOrderPDFService purchaseOrderPDFService;

    /**
     * Download Purchase Order PDF with enhanced formatting (default)
     */
    @GetMapping("/{orderId}/download")
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
     * @param orderId Purchase Order ID
     * @param format Format type: "simple", "detailed", "standard" (default)
     */
    @GetMapping("/{orderId}/download/format")
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
     * Preview Purchase Order PDF in browser (inline display)
     */
    @GetMapping("/{orderId}/preview")
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

    /**
     * Download Purchase Order PDF for a specific project
     */
    @GetMapping("/project/{projectId}/download")
    public ResponseEntity<byte[]> downloadPurchaseOrdersByProject(
            @PathVariable("projectId") String projectId,
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            // This would require implementing a service to get all purchase orders by project
            // and combine them into a single PDF or ZIP file
            // For now, return error indicating feature not implemented
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body("Project-based purchase order download feature not yet implemented".getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating project purchase order PDFs: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Download Purchase Order PDF for a specific supplier
     */
    @GetMapping("/supplier/{supplierId}/download")
    public ResponseEntity<byte[]> downloadPurchaseOrdersBySupplier(
            @PathVariable("supplierId") Integer supplierId,
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            // This would require implementing a service to get all purchase orders by supplier
            // and combine them into a single PDF or ZIP file
            // For now, return error indicating feature not implemented
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body("Supplier-based purchase order download feature not yet implemented".getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating supplier purchase order PDFs: " + e.getMessage()).getBytes());
        }
    }

    /**
     * Bulk download multiple purchase orders as ZIP file
     */
    @GetMapping("/bulk/download")
    public ResponseEntity<byte[]> downloadBulkPurchaseOrders(
            @RequestParam("orderIds") String orderIds,
            @RequestParam(value = "type", defaultValue = "standard") String format) {
        
        try {
            // This would require implementing a ZIP service
            // For now, return error indicating feature not implemented
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body("Bulk purchase order download feature not yet implemented".getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating bulk purchase order PDFs: " + e.getMessage()).getBytes());
        }
    }
}