package com.structurax.root.structurax.root.service;

/**
 * Service interface for generating Invoice PDF documents
 */
public interface InvoicePDFService {
    
    /**
     * Generate invoice PDF based on a purchase order
     * Treats purchase orders as invoices for billing purposes
     * 
     * @param orderId The ID of the purchase order
     * @return byte array containing the PDF content
     */
    byte[] generateInvoicePdf(Integer orderId);
    
    /**
     * Generate a simple invoice PDF with minimal formatting
     * 
     * @param orderId The ID of the purchase order
     * @return byte array containing the PDF content
     */
    byte[] generateSimpleInvoicePdf(Integer orderId);
    
    /**
     * Generate a detailed invoice PDF with comprehensive information
     * 
     * @param orderId The ID of the purchase order
     * @return byte array containing the PDF content
     */
    byte[] generateDetailedInvoicePdf(Integer orderId);
}