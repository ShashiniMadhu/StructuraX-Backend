package com.structurax.root.structurax.root.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.structurax.root.structurax.root.dao.QuotationDAO;
import com.structurax.root.structurax.root.dao.QuotationResponseDAO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.dto.QuotationSupplierDTO;
import com.structurax.root.structurax.root.dto.QuotationWithItemsDTO;
import com.structurax.root.structurax.root.service.QuotationService;

@Service
public class QuotationServiceImpl implements QuotationService {
    
    @Autowired
    private QuotationDAO quotationDAO;
    
    @Autowired
    private QuotationResponseDAO quotationResponseDAO;

    @Override
    @Transactional
    public Integer createQuotation(QuotationDTO quotation, List<QuotationItemDTO> items, List<Integer> supplierIds) {
        // Create the quotation first
        Integer qId = quotationDAO.insertQuotation(quotation);
        
        // Add items if quotation was created successfully
        if (qId != null && items != null && !items.isEmpty()) {
            for (QuotationItemDTO item : items) {
                item.setQId(qId);
                quotationDAO.insertQuotationItem(item);
            }
        }
        
        // Add suppliers if quotation was created successfully
        if (qId != null && supplierIds != null && !supplierIds.isEmpty()) {
            for (Integer supplierId : supplierIds) {
                QuotationSupplierDTO quotationSupplier = new QuotationSupplierDTO(qId, supplierId);
                quotationDAO.insertQuotationSupplier(quotationSupplier);
            }
        }
        
        return qId;
    }

    @Override
    @Transactional
    public Integer createQuotation(QuotationDTO quotation, List<QuotationItemDTO> items) {
        // Create the quotation first
        Integer qId = quotationDAO.insertQuotation(quotation);
        
        // Add items if quotation was created successfully
        if (qId != null && items != null && !items.isEmpty()) {
            for (QuotationItemDTO item : items) {
                item.setQId(qId);
                quotationDAO.insertQuotationItem(item);
            }
        }
        
        return qId;
    }

    @Override
    public Integer createQuotation(QuotationDTO quotation) {
        return quotationDAO.insertQuotation(quotation);
    }

    @Override
    public void addQuotationItem(QuotationItemDTO item) {
        quotationDAO.insertQuotationItem(item);
    }

    @Override
    public void addQuotationSupplier(Integer qId, Integer supplierId) {
        QuotationSupplierDTO quotationSupplier = new QuotationSupplierDTO(qId, supplierId);
        quotationDAO.insertQuotationSupplier(quotationSupplier);
    }

    @Override
    public QuotationDTO getQuotationById(Integer qId) {
        return quotationDAO.getQuotationById(qId);
    }

    @Override
    public List<QuotationItemDTO> getQuotationItemsByQuotationId(Integer qId) {
        return quotationDAO.getQuotationItemsByQuotationId(qId);
    }

    @Override
    public List<QuotationSupplierDTO> getQuotationSuppliersByQuotationId(Integer qId) {
        return quotationDAO.getQuotationSuppliersByQuotationId(qId);
    }

    @Override
    public List<QuotationDTO> getAllQuotations() {
        return quotationDAO.getAllQuotations();
    }

    @Override
    public List<QuotationDTO> getQuotationsByQsId(String qsId) {
        return quotationDAO.getQuotationsByQsId(qsId);
    }

    @Override
    public QuotationWithItemsDTO getQuotationWithItems(Integer qId) {
        QuotationDTO quotation = quotationDAO.getQuotationById(qId);
        if (quotation != null) {
            List<QuotationItemDTO> items = quotationDAO.getQuotationItemsByQuotationId(qId);
            return new QuotationWithItemsDTO(quotation, items);
        }
        return null;
    }

    @Override
    public List<QuotationWithItemsDTO> getAllQuotationsWithItems() {
        List<QuotationDTO> quotations = quotationDAO.getAllQuotations();
        List<QuotationWithItemsDTO> quotationWithItemsList = new java.util.ArrayList<>();
        
        for (QuotationDTO quotation : quotations) {
            List<QuotationItemDTO> items = quotationDAO.getQuotationItemsByQuotationId(quotation.getQId());
            QuotationWithItemsDTO quotationWithItems = new QuotationWithItemsDTO(quotation, items);
            quotationWithItemsList.add(quotationWithItems);
        }
        
        return quotationWithItemsList;
    }

    @Override
    public List<QuotationWithItemsDTO> getQuotationsWithItemsByQsId(String qsId) {
        List<QuotationDTO> quotations = quotationDAO.getQuotationsByQsId(qsId);
        List<QuotationWithItemsDTO> quotationWithItemsList = new java.util.ArrayList<>();
        
        for (QuotationDTO quotation : quotations) {
            List<QuotationItemDTO> items = quotationDAO.getQuotationItemsByQuotationId(quotation.getQId());
            QuotationWithItemsDTO quotationWithItems = new QuotationWithItemsDTO(quotation, items);
            quotationWithItemsList.add(quotationWithItems);
        }
        
        return quotationWithItemsList;
    }

    @Override
    @Transactional
    public boolean updateQuotation(QuotationDTO quotation) {
        return quotationDAO.updateQuotation(quotation);
    }

    @Override
    @Transactional
    public boolean updateQuotationWithItems(QuotationDTO quotation, List<QuotationItemDTO> items) {
        try {
            // Update the quotation
            boolean quotationUpdated = quotationDAO.updateQuotation(quotation);
            
            if (quotationUpdated) {
                // Delete existing items
                quotationDAO.deleteQuotationItemsByQuotationId(quotation.getQId());
                
                // Add new items
                if (items != null) {
                    for (QuotationItemDTO item : items) {
                        item.setQId(quotation.getQId());
                        quotationDAO.insertQuotationItem(item);
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateQuotationWithItemsAndSuppliers(QuotationDTO quotation, List<QuotationItemDTO> items, List<Integer> supplierIds) {
        try {
            // Update the quotation
            boolean quotationUpdated = quotationDAO.updateQuotation(quotation);
            
            if (quotationUpdated) {
                // Delete existing items and suppliers
                quotationDAO.deleteQuotationItemsByQuotationId(quotation.getQId());
                quotationDAO.deleteQuotationSuppliersByQuotationId(quotation.getQId());
                
                // Add new items
                if (items != null) {
                    for (QuotationItemDTO item : items) {
                        item.setQId(quotation.getQId());
                        quotationDAO.insertQuotationItem(item);
                    }
                }
                
                // Add new suppliers
                if (supplierIds != null) {
                    for (Integer supplierId : supplierIds) {
                        QuotationSupplierDTO quotationSupplier = new QuotationSupplierDTO(quotation.getQId(), supplierId);
                        quotationDAO.insertQuotationSupplier(quotationSupplier);
                    }
                }
                
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateQuotationStatus(Integer qId, String status) {
        return quotationDAO.updateQuotationStatus(qId, status);
    }

    @Override
    @Transactional
    public boolean deleteQuotation(Integer qId) {
        return quotationDAO.deleteQuotation(qId);
    }

    @Override
    public boolean deleteQuotationItem(Integer itemId) {
        return quotationDAO.deleteQuotationItem(itemId);
    }

    @Override
    public boolean closeQuotationIfNoResponsesOrAllRejected(Integer qId) {
        try {
            // Get the quotation to check if it exists and its current status
            QuotationDTO quotation = quotationDAO.getQuotationById(qId);
            if (quotation == null) {
                return false; // Quotation not found
            }
            
            // Check if quotation is already closed
            if ("closed".equalsIgnoreCase(quotation.getStatus())) {
                return true; // Already closed
            }
            
            // Get all quotation responses for this quotation
            List<QuotationResponseDTO> responses = quotationResponseDAO.getQuotationResponsesByQuotationId(qId);
            
            boolean shouldClose = false;
            
            if (responses == null || responses.isEmpty()) {
                // No responses at all - should close
                shouldClose = true;
            } else {
                // Check if all responses are rejected
                boolean allRejected = true;
                for (QuotationResponseDTO response : responses) {
                    if (!"rejected".equalsIgnoreCase(response.getStatus())) {
                        allRejected = false;
                        break;
                    }
                }
                shouldClose = allRejected;
            }
            
            // If should close, update the quotation status to "closed"
            if (shouldClose) {
                return quotationDAO.updateQuotationStatus(qId, "closed");
            }
            
            return false; // No need to close
            
        } catch (Exception e) {
            // Log the error in a real application
            return false;
        }
    }

    @Override
    public Map<String, Object> closeAllEligibleQuotations() {
        Map<String, Object> result = new HashMap<>();
        List<Integer> closedQuotationIds = new java.util.ArrayList<>();
        List<Integer> failedQuotationIds = new java.util.ArrayList<>();
        List<String> errors = new java.util.ArrayList<>();
        
        try {
            // Get all quotations
            List<QuotationDTO> allQuotations = quotationDAO.getAllQuotations();
            
            int processedCount = 0;
            int closedCount = 0;
            int alreadyClosedCount = 0;
            int skippedCount = 0;
            
            for (QuotationDTO quotation : allQuotations) {
                processedCount++;
                
                try {
                    // Skip if already closed
                    if ("closed".equalsIgnoreCase(quotation.getStatus())) {
                        alreadyClosedCount++;
                        continue;
                    }
                    
                    // Check if should close
                    List<QuotationResponseDTO> responses = 
                        quotationResponseDAO.getQuotationResponsesByQuotationId(quotation.getQId());
                    
                    boolean shouldClose = false;
                    
                    if (responses == null || responses.isEmpty()) {
                        // No responses - should close
                        shouldClose = true;
                    } else {
                        // Check if all responses are rejected
                        boolean allRejected = true;
                        for (QuotationResponseDTO response : responses) {
                            if (!"rejected".equalsIgnoreCase(response.getStatus())) {
                                allRejected = false;
                                break;
                            }
                        }
                        shouldClose = allRejected;
                    }
                    
                    if (shouldClose) {
                        boolean closed = quotationDAO.updateQuotationStatus(quotation.getQId(), "closed");
                        if (closed) {
                            closedQuotationIds.add(quotation.getQId());
                            closedCount++;
                        } else {
                            failedQuotationIds.add(quotation.getQId());
                            errors.add("Failed to update status for quotation ID: " + quotation.getQId());
                        }
                    } else {
                        skippedCount++;
                    }
                    
                } catch (Exception e) {
                    failedQuotationIds.add(quotation.getQId());
                    errors.add("Error processing quotation ID " + quotation.getQId() + ": " + e.getMessage());
                }
            }
            
            result.put("success", true);
            result.put("totalProcessed", processedCount);
            result.put("closed", closedCount);
            result.put("alreadyClosed", alreadyClosedCount);
            result.put("skipped", skippedCount);
            result.put("failed", failedQuotationIds.size());
            result.put("closedQuotationIds", closedQuotationIds);
            result.put("failedQuotationIds", failedQuotationIds);
            result.put("errors", errors);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error in batch processing: " + e.getMessage());
            result.put("errors", errors);
        }
        
        return result;
    }
}