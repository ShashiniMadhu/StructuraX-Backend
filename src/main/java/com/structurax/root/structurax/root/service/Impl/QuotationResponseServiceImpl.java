package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.QuotationResponseDAO;
import com.structurax.root.structurax.root.dto.QuotationResponseDTO;
import com.structurax.root.structurax.root.dto.QuotationResponseWithSupplierDTO;
import com.structurax.root.structurax.root.service.QuotationResponseService;

@Service
public class QuotationResponseServiceImpl implements QuotationResponseService {
    
    @Autowired
    private QuotationResponseDAO quotationResponseDAO;

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
}
