package com.structurax.root.structurax.root.service;

/**
 * Service interface for generating Purchase Order PDF documents
 */
public interface PurchaseOrderPDFService {
    
    /**
     * Generate purchase order PDF for a given order ID
     * 
     * @param orderId The ID of the purchase order
     * @return byte array containing the PDF content
     */
    byte[] generatePurchaseOrderPdf(Integer orderId);
    
    /**
     * Generate a simple purchase order PDF with minimal formatting
     * 
     * @param orderId The ID of the purchase order
     * @return byte array containing the PDF content
     */
    byte[] generateSimplePurchaseOrderPdf(Integer orderId);
    
    /**
     * Generate a detailed purchase order PDF with comprehensive information
     * 
     * @param orderId The ID of the purchase order
     * @return byte array containing the PDF content
     */
    byte[] generateDetailedPurchaseOrderPdf(Integer orderId);
}