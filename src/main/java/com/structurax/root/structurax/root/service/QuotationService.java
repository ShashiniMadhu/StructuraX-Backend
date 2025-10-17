package com.structurax.root.structurax.root.service;

import java.util.List;
import java.util.Map;

import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationSupplierDTO;
import com.structurax.root.structurax.root.dto.QuotationWithItemsDTO;

public interface QuotationService {
    // Create quotation with items and suppliers
    Integer createQuotation(QuotationDTO quotation, List<QuotationItemDTO> items, List<Integer> supplierIds);
    
    // Create quotation with items
    Integer createQuotation(QuotationDTO quotation, List<QuotationItemDTO> items);
    
    // Create individual quotation and item
    Integer createQuotation(QuotationDTO quotation);
    void addQuotationItem(QuotationItemDTO item);
    void addQuotationSupplier(Integer qId, Integer supplierId);
    
    // Read operations
    QuotationDTO getQuotationById(Integer qId);
    List<QuotationItemDTO> getQuotationItemsByQuotationId(Integer qId);
    List<QuotationSupplierDTO> getQuotationSuppliersByQuotationId(Integer qId);
    List<QuotationDTO> getAllQuotations();
    List<QuotationDTO> getQuotationsByQsId(String qsId);
    
    // Combined operations for easier data handling
    QuotationWithItemsDTO getQuotationWithItems(Integer qId);
    List<QuotationWithItemsDTO> getAllQuotationsWithItems();
    List<QuotationWithItemsDTO> getQuotationsWithItemsByQsId(String qsId);
    
    // Update operations
    boolean updateQuotation(QuotationDTO quotation);
    boolean updateQuotationWithItems(QuotationDTO quotation, List<QuotationItemDTO> items);
    boolean updateQuotationWithItemsAndSuppliers(QuotationDTO quotation, List<QuotationItemDTO> items, List<Integer> supplierIds);
    boolean updateQuotationStatus(Integer qId, String status);
    
    // Delete operations
    boolean deleteQuotation(Integer qId);
    boolean deleteQuotationItem(Integer itemId);
    
    // Close quotation if no responses or all rejected
    boolean closeQuotationIfNoResponsesOrAllRejected(Integer qId);
    
    // Batch process all quotations to close eligible ones
    Map<String, Object> closeAllEligibleQuotations();
}