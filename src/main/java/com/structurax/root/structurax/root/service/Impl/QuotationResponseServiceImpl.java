package com.structurax.root.structurax.root.service.Impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.structurax.root.structurax.root.dao.PurchaseOrderDAO;
import com.structurax.root.structurax.root.dao.QuotationResponseDAO;
import com.structurax.root.structurax.root.dto.OrderItemDTO;
import com.structurax.root.structurax.root.dto.PurchaseOrderDTO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationItemDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;
import com.structurax.root.structurax.root.service.QuotationResponseService;
import com.structurax.root.structurax.root.service.QuotationService;

@Service
public class QuotationResponseServiceImpl implements QuotationResponseService {
    
    @Autowired
    private QuotationResponseDAO quotationResponseDAO;
    
    @Autowired
    private PurchaseOrderDAO purchaseOrderDAO;
    
    @Autowired
    private QuotationService quotationService;

    @Override
    public Integer createQuotationResponse(QuotationResponseDTO response) {
        return quotationResponseDAO.insertQuotationResponse(response);
    }

    @Override
    public QuotationResponseDTO getQuotationResponseById(Integer responseId) {
        return quotationResponseDAO.getQuotationResponseById(responseId);
    }

    @Override
    public List<QuotationResponseDTO> getQuotationResponsesByQuotationId(Integer qId) {
        return quotationResponseDAO.getQuotationResponsesByQuotationId(qId);
    }

    @Override
    public List<QuotationResponseDTO> getQuotationResponsesBySupplierId(Integer supplierId) {
        return quotationResponseDAO.getQuotationResponsesBySupplierId(supplierId);
    }

    @Override
    public List<QuotationResponseDTO> getAllQuotationResponses() {
        return quotationResponseDAO.getAllQuotationResponses();
    }

    @Override
    public QuotationResponseWithSupplierDTO getQuotationResponseWithSupplierById(Integer responseId) {
        return quotationResponseDAO.getQuotationResponseWithSupplierById(responseId);
    }

    @Override
    public List<QuotationResponseWithSupplierDTO> getQuotationResponsesWithSupplierByQuotationId(Integer qId) {
        return quotationResponseDAO.getQuotationResponsesWithSupplierByQuotationId(qId);
    }

    @Override
    public List<QuotationResponseWithSupplierDTO> getQuotationResponsesWithSupplierBySupplierId(Integer supplierId) {
        return quotationResponseDAO.getQuotationResponsesWithSupplierBySupplierId(supplierId);
    }

    @Override
    public List<QuotationResponseWithSupplierDTO> getAllQuotationResponsesWithSupplier() {
        return quotationResponseDAO.getAllQuotationResponsesWithSupplier();
    }

    @Override
    public boolean updateQuotationResponse(QuotationResponseDTO response) {
        return quotationResponseDAO.updateQuotationResponse(response);
    }

    @Override
    public boolean updateQuotationResponseStatus(Integer responseId, String status) {
        return quotationResponseDAO.updateQuotationResponseStatus(responseId, status);
    }

    @Override
    public boolean deleteQuotationResponse(Integer responseId) {
        return quotationResponseDAO.deleteQuotationResponse(responseId);
    }

    @Override
    public void deleteQuotationResponsesByQuotationId(Integer qId) {
        quotationResponseDAO.deleteQuotationResponsesByQuotationId(qId);
    }

    @Override
    @Transactional
    public Integer createPurchaseOrderFromResponse(Integer responseId, LocalDate estimatedDeliveryDate, 
                                                  String paymentStatus, Boolean orderStatus) {
        // Get the quotation response details
        QuotationResponseWithSupplierDTO response = quotationResponseDAO.getQuotationResponseWithSupplierById(responseId);
        
        if (response == null) {
            throw new RuntimeException("Quotation response not found with ID: " + responseId);
        }
        
        // Get quotation details to fetch project ID
        QuotationDTO quotation = quotationService.getQuotationById(response.getQId());
        if (quotation == null) {
            throw new RuntimeException("Quotation not found with ID: " + response.getQId());
        }
        
        // Validate that the project exists
        if (quotation.getProjectId() == null || quotation.getProjectId().isEmpty()) {
            throw new RuntimeException("Project ID is null or empty in quotation: " + response.getQId());
        }
        
        // Validate that the supplier exists
        if (response.getSupplierId() == null) {
            throw new RuntimeException("Supplier ID is null in quotation response: " + responseId);
        }
        
        // Use the delivery date from quotation response if estimated delivery date is not provided
        LocalDate finalDeliveryDate = estimatedDeliveryDate;
        if (finalDeliveryDate == null) {
            finalDeliveryDate = response.getDeliveryDate();
        }
        
        // Ensure we have a delivery date (this should not be null as per your requirement)
        if (finalDeliveryDate == null) {
            throw new RuntimeException("Delivery date is required. No delivery date found in request or quotation response.");
        }
        
        // Create purchase order DTO
        PurchaseOrderDTO purchaseOrder = new PurchaseOrderDTO();
        purchaseOrder.setProjectId(quotation.getProjectId());
        purchaseOrder.setSupplierId(response.getSupplierId());
        purchaseOrder.setResponseId(responseId);
        purchaseOrder.setPaymentStatus(paymentStatus != null ? paymentStatus : "pending");
        purchaseOrder.setEstimatedDeliveryDate(finalDeliveryDate);
        purchaseOrder.setOrderDate(LocalDate.now());
        purchaseOrder.setOrderStatus(orderStatus != null ? orderStatus : false);
        
        // Insert purchase order
        Integer purchaseOrderId = purchaseOrderDAO.insertPurchaseOrder(purchaseOrder);
        
        if (purchaseOrderId == null) {
            throw new RuntimeException("Failed to create purchase order");
        }
        
        // Get quotation items and convert them to order items
        List<QuotationItemDTO> quotationItems = 
            quotationService.getQuotationItemsByQuotationId(response.getQId());
        
        if (quotationItems != null && !quotationItems.isEmpty()) {
            List<OrderItemDTO> orderItems = new java.util.ArrayList<>();
            
            for (QuotationItemDTO quotationItem : quotationItems) {
                OrderItemDTO orderItem = new OrderItemDTO();
                orderItem.setOrderId(purchaseOrderId);
                // Now item_id references quotation_item.item_id
                orderItem.setItemId(quotationItem.getItemId());
                // Use quotation item name in description if description is empty
                String description = quotationItem.getDescription();
                if (description == null || description.trim().isEmpty()) {
                    description = quotationItem.getName();
                }
                orderItem.setDescription(description);
                orderItem.setUnitPrice(quotationItem.getAmount()); // Using amount as unit price
                orderItem.setQuantity(quotationItem.getQuantity());
                orderItems.add(orderItem);
            }
            
            // Insert order items
            boolean itemsInserted = purchaseOrderDAO.insertPurchaseOrderItems(purchaseOrderId, orderItems);
            if (!itemsInserted) {
                throw new RuntimeException("Failed to insert purchase order items");
            }
        }
        
        // Update quotation response status to "accepted" or "purchased"
        quotationResponseDAO.updateQuotationResponseStatus(responseId, "accepted");
        
        return purchaseOrderId;
    }
}
