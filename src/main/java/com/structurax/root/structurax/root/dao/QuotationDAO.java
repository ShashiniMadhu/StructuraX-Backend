package com.structurax.root.structurax.root.dao;

import java.util.List;

import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationSupplierDTO;

public interface QuotationDAO {
    // Create quotation
    Integer insertQuotation(QuotationDTO quotation);
    void insertQuotationItem(QuotationItemDTO item);
    void insertQuotationSupplier(QuotationSupplierDTO quotationSupplier);
    
    // Read operations
    QuotationDTO getQuotationById(Integer qId);
    List<QuotationItemDTO> getQuotationItemsByQuotationId(Integer qId);
    List<QuotationItemDTO> getEnhancedQuotationItemsByQuotationId(Integer qId);
    List<QuotationSupplierDTO> getQuotationSuppliersByQuotationId(Integer qId);
    List<QuotationDTO> getAllQuotations();
    List<QuotationDTO> getQuotationsByQsId(String qsId);
    List<QuotationDTO> getQuotationsBySupplierId(Integer supplierId);

    // Update operations
    boolean updateQuotation(QuotationDTO quotation);
    boolean updateQuotationItem(QuotationItemDTO item);
    boolean updateQuotationStatus(Integer qId, String status);
    
    // Delete operations
    boolean deleteQuotation(Integer qId);
    boolean deleteQuotationItem(Integer itemId);
    void deleteQuotationItemsByQuotationId(Integer qId);
    void deleteQuotationSuppliersByQuotationId(Integer qId);
}