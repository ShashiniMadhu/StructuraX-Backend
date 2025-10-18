package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.ProjectOwnerDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.ProjectOwnerMaterialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectOwnerMaterialsServiceImpl implements ProjectOwnerMaterialsService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerMaterialsServiceImpl.class);
    private final ProjectOwnerDAO projectOwnerDAO;

    public ProjectOwnerMaterialsServiceImpl(ProjectOwnerDAO projectOwnerDAO) {
        this.projectOwnerDAO = projectOwnerDAO;
    }

    @Override
    public List<ProjectMaterialDTO> getMaterialsByProjectId(String projectId) {
        logger.info("Service: Fetching materials for project_id={}", projectId);
        return projectOwnerDAO.getMaterialsByProjectId(projectId);
    }

    @Override
    public MaterialSummaryDTO getMaterialSummary(String projectId) {
        logger.info("Service: Fetching material summary for project_id={}", projectId);
        return projectOwnerDAO.getMaterialSummaryByProjectId(projectId);
    }

    @Override
    public SiteVisitDTO createSiteVisitRequest(SiteVisitDTO siteVisitDTO) {
        logger.info("Service: Creating site visit request for project_id={}", siteVisitDTO.getProjectId());

        // Set default status if not provided
        if (siteVisitDTO.getStatus() == null || siteVisitDTO.getStatus().isEmpty()) {
            siteVisitDTO.setStatus("Requested");
        }

        return projectOwnerDAO.createSiteVisit(siteVisitDTO);
    }

    @Override
    public List<SiteVisitDTO> getSiteVisits(String projectId) {
        logger.info("Service: Fetching site visits for project_id={}", projectId);
        return projectOwnerDAO.getSiteVisitsByProjectId(projectId);
    }

    @Override
    public SiteVisitDTO getSiteVisitById(Integer visitId) {
        logger.info("Service: Fetching site visit by visit_id={}", visitId);
        return projectOwnerDAO.getSiteVisitById(visitId);
    }

    @Override
    public void updateSiteVisitStatus(Integer visitId, String status) {
        logger.info("Service: Updating site visit status for visit_id={} to {}", visitId, status);
        projectOwnerDAO.updateSiteVisitStatus(visitId, status);
    }

    // ========== PAYMENT METHODS ==========

    @Override
    public PaymentSummaryDTO getPaymentSummary(String projectId) {
        logger.info("Service: Fetching payment summary for project_id={}", projectId);
        return projectOwnerDAO.getPaymentSummaryByProjectId(projectId);
    }

    @Override
    public List<InstallmentDTO> getPaymentHistory(String projectId) {
        logger.info("Service: Fetching payment history for project_id={}", projectId);
        return projectOwnerDAO.getPaymentHistoryByProjectId(projectId);
    }

    @Override
    public List<InstallmentDTO> getUpcomingPayments(String projectId) {
        logger.info("Service: Fetching upcoming payments for project_id={}", projectId);
        return projectOwnerDAO.getUpcomingPaymentsByProjectId(projectId);
    }

    @Override
    public PaymentReceiptDTO uploadPaymentReceipt(PaymentReceiptDTO receiptDTO) {
        logger.info("Service: Uploading payment receipt for project_id={}", receiptDTO.getProjectId());

        // Set default status if not provided
        if (receiptDTO.getStatus() == null || receiptDTO.getStatus().isEmpty()) {
            receiptDTO.setStatus("Pending");
        }

        return projectOwnerDAO.uploadPaymentReceipt(receiptDTO);
    }

    @Override
    public List<PaymentReceiptDTO> getPaymentReceipts(String projectId) {
        logger.info("Service: Fetching payment receipts for project_id={}", projectId);
        return projectOwnerDAO.getPaymentReceiptsByProjectId(projectId);
    }
}