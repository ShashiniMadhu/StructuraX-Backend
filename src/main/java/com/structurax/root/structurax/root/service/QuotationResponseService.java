package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;

public interface QuotationResponseService {
    // Create quotation response
    Integer createQuotationResponse(QuotationResponseDTO response);
    
    // Read operations
    QuotationResponseDTO getQuotationResponseById(Integer responseId);
    List<QuotationResponseDTO> getQuotationResponsesByQuotationId(Integer qId);
    List<QuotationResponseDTO> getQuotationResponsesBySupplierId(Integer supplierId);
    List<QuotationResponseDTO> getAllQuotationResponses();
    
    // Read operations with supplier details
    QuotationResponseWithSupplierDTO getQuotationResponseWithSupplierById(Integer responseId);
    List<QuotationResponseWithSupplierDTO> getQuotationResponsesWithSupplierByQuotationId(Integer qId);
    List<QuotationResponseWithSupplierDTO> getQuotationResponsesWithSupplierBySupplierId(Integer supplierId);
    List<QuotationResponseWithSupplierDTO> getAllQuotationResponsesWithSupplier();
    
    // Update operations
    boolean updateQuotationResponse(QuotationResponseDTO response);
    boolean updateQuotationResponseStatus(Integer responseId, String status);
    
    // Delete operations
    boolean deleteQuotationResponse(Integer responseId);
    void deleteQuotationResponsesByQuotationId(Integer qId);
}
