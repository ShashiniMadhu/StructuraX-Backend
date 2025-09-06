package com.structurax.root.structurax.root.service;

/**
 * Service interface for generating Quotation PDF documents
 */
public interface QuotationPDFService {
    
    /**
     * Generate quotation PDF for a given quotation ID
     * 
     * @param quotationId The ID of the quotation
     * @return byte array containing the PDF content
     */
    byte[] generateQuotationPdf(Integer quotationId);
    
    /**
     * Generate a simple quotation PDF with minimal formatting
     * 
     * @param quotationId The ID of the quotation
     * @return byte array containing the PDF content
     */
    byte[] generateSimpleQuotationPdf(Integer quotationId);
    
    /**
     * Generate a detailed quotation PDF with comprehensive information
     * 
     * @param quotationId The ID of the quotation
     * @return byte array containing the PDF content
     */
    byte[] generateDetailedQuotationPdf(Integer quotationId);
}