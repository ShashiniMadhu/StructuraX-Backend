package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.QuotationDAO;
import com.structurax.root.structurax.root.dao.QuotationResponseDAO;
import com.structurax.root.structurax.root.dto.QuotationDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.service.SupplierQuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierQuotationServiceImpl implements SupplierQuotationService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierQuotationServiceImpl.class);

    @Autowired
    private QuotationDAO quotationDAO;

    @Autowired
    private QuotationResponseDAO quotationResponseDAO;

    @Override
    public List<QuotationDTO> getQuotationsBySupplierId(Integer supplierId) {
        logger.info("Retrieving quotations for supplier ID: {}", supplierId);

        if (supplierId == null || supplierId <= 0) {
            logger.error("Validation failed: Valid supplier ID is required");
            throw new IllegalArgumentException("Valid supplier ID is required");
        }

        try {
            // Use the QuotationDAO method directly instead of the problematic stream logic
            List<QuotationDTO> quotations = quotationDAO.getQuotationsBySupplierId(supplierId);
            logger.info("Successfully retrieved {} quotations for supplier ID: {}", quotations.size(), supplierId);
            return quotations;
        } catch (Exception e) {
            logger.error("Error retrieving quotations for supplier ID {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quotations for supplier: " + e.getMessage());
        }
    }

    @Override
    public QuotationDTO getQuotationById(Integer quotationId) {
        logger.info("Retrieving quotation with ID: {}", quotationId);

        if (quotationId == null || quotationId <= 0) {
            logger.error("Validation failed: Valid quotation ID is required");
            throw new IllegalArgumentException("Valid quotation ID is required");
        }

        try {
            QuotationDTO quotation = quotationDAO.getQuotationById(quotationId);
            if (quotation == null) {
                logger.warn("Quotation not found with ID: {}", quotationId);
                throw new RuntimeException("Quotation not found with ID: " + quotationId);
            }
            logger.info("Successfully retrieved quotation with ID: {}", quotationId);
            return quotation;
        } catch (Exception e) {
            logger.error("Error retrieving quotation with ID {}: {}", quotationId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quotation: " + e.getMessage());
        }
    }

    @Override
    public QuotationResponseDTO respondToQuotation(QuotationResponseDTO responseDTO) {
        logger.info("Creating response for quotation ID: {}", responseDTO.getQId());

        // Validate required fields
        if (responseDTO.getQId() == null || responseDTO.getQId() <= 0) {
            logger.error("Validation failed: Valid quotation ID is required");
            throw new IllegalArgumentException("Valid quotation ID is required");
        }
        if (responseDTO.getSupplierId() == null || responseDTO.getSupplierId() <= 0) {
            logger.error("Validation failed: Valid supplier ID is required");
            throw new IllegalArgumentException("Valid supplier ID is required");
        }
        if (responseDTO.getTotalAmount() == null || responseDTO.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            logger.error("Validation failed: Valid total amount is required");
            throw new IllegalArgumentException("Valid total amount is required");
        }

        try {
            // Set default status if not provided
            if (responseDTO.getStatus() == null || responseDTO.getStatus().trim().isEmpty()) {
                responseDTO.setStatus("SUBMITTED");
            }

            // Set response date if not provided
            if (responseDTO.getResponseDate() == null) {
                responseDTO.setResponseDate(new java.sql.Date(System.currentTimeMillis()));
            }

            Integer responseId = quotationResponseDAO.insertQuotationResponse(responseDTO);
            responseDTO.setResponseId(responseId);

            logger.info("Successfully created quotation response with ID: {}", responseId);
            return responseDTO;
        } catch (Exception e) {
            logger.error("Error creating quotation response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create quotation response: " + e.getMessage());
        }
    }

    @Override
    public QuotationResponseDTO getQuotationResponse(Integer quotationId) {
        logger.info("Getting response for quotation ID: {}", quotationId);

        if (quotationId == null || quotationId <= 0) {
            logger.error("Validation failed: Valid quotation ID is required");
            throw new IllegalArgumentException("Valid quotation ID is required");
        }

        try {
            List<QuotationResponseDTO> responses = quotationResponseDAO.getQuotationResponsesByQuotationId(quotationId);
            if (responses.isEmpty()) {
                logger.warn("No response found for quotation ID: {}", quotationId);
                return null;
            }

            // Return the first response (or you might want to return the latest one)
            QuotationResponseDTO response = responses.get(0);
            logger.info("Successfully retrieved response for quotation ID: {}", quotationId);
            return response;
        } catch (Exception e) {
            logger.error("Error retrieving response for quotation ID {}: {}", quotationId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quotation response: " + e.getMessage());
        }
    }

    @Override
    public List<QuotationDTO> getAllQuotationRequests() {
        logger.info("Retrieving all quotation requests");

        try {
            List<QuotationDTO> quotations = quotationDAO.getAllQuotations();
            logger.info("Successfully retrieved {} quotation requests", quotations.size());
            return quotations;
        } catch (Exception e) {
            logger.error("Error retrieving all quotation requests: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quotation requests: " + e.getMessage());
        }
    }

    @Override
    public QuotationResponseDTO updateQuotationResponse(QuotationResponseDTO responseDTO) {
        logger.info("Updating quotation response with ID: {}", responseDTO.getResponseId());

        if (responseDTO.getResponseId() == null || responseDTO.getResponseId() <= 0) {
            logger.error("Validation failed: Valid response ID is required");
            throw new IllegalArgumentException("Valid response ID is required");
        }

        try {
            boolean updated = quotationResponseDAO.updateQuotationResponse(responseDTO);
            if (!updated) {
                logger.warn("Failed to update quotation response with ID: {}", responseDTO.getResponseId());
                throw new RuntimeException("Failed to update quotation response");
            }

            logger.info("Successfully updated quotation response with ID: {}", responseDTO.getResponseId());
            return responseDTO;
        } catch (Exception e) {
            logger.error("Error updating quotation response with ID {}: {}", responseDTO.getResponseId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update quotation response: " + e.getMessage());
        }
    }

    @Override
    public List<QuotationResponseDTO> getQuotationResponsesBySupplierId(Integer supplierId) {
        logger.info("Retrieving quotation responses for supplier ID: {}", supplierId);

        if (supplierId == null || supplierId <= 0) {
            logger.error("Validation failed: Valid supplier ID is required");
            throw new IllegalArgumentException("Valid supplier ID is required");
        }

        try {
            List<QuotationResponseDTO> responses = quotationResponseDAO.getQuotationResponsesBySupplierId(supplierId);
            logger.info("Successfully retrieved {} quotation responses for supplier ID: {}", responses.size(), supplierId);
            return responses;
        } catch (Exception e) {
            logger.error("Error retrieving quotation responses for supplier ID {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch quotation responses for supplier: " + e.getMessage());
        }
    }
}
