package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;

import java.util.List;

public interface SupplierQuotationService {

    // Get quotations assigned to a specific supplier
    List<QuotationDTO> getQuotationsBySupplierId(Integer supplierId);

    // Get quotation details by ID
    QuotationDTO getQuotationById(Integer quotationId);

    // Create a response to a quotation
    QuotationResponseDTO respondToQuotation(QuotationResponseDTO responseDTO);

    // Get quotation response by quotation ID
    QuotationResponseDTO getQuotationResponse(Integer quotationId);

    // Get all quotation requests (for displaying in supplier dashboard)
    List<QuotationDTO> getAllQuotationRequests();

    // Update quotation response
    QuotationResponseDTO updateQuotationResponse(QuotationResponseDTO responseDTO);

    // Get quotation responses by supplier ID
    List<QuotationResponseDTO> getQuotationResponsesBySupplierId(Integer supplierId);
}
