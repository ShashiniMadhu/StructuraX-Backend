package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface ProjectOwnerDAO {

    // Material Methods
    List<ProjectMaterialDTO> getMaterialsByProjectId(String projectId);
    MaterialSummaryDTO getMaterialSummaryByProjectId(String projectId);

    // Site Visit Methods
    SiteVisitDTO createSiteVisit(SiteVisitDTO siteVisitDTO);
    List<SiteVisitDTO> getSiteVisitsByProjectId(String projectId);
    SiteVisitDTO getSiteVisitById(Integer visitId);
    void updateSiteVisitStatus(Integer visitId, String status);

    // Payment Methods
    PaymentSummaryDTO getPaymentSummaryByProjectId(String projectId);
    List<InstallmentDTO> getPaymentHistoryByProjectId(String projectId);
    List<InstallmentDTO> getUpcomingPaymentsByProjectId(String projectId);
    PaymentReceiptDTO uploadPaymentReceipt(PaymentReceiptDTO receiptDTO);
    List<PaymentReceiptDTO> getPaymentReceiptsByProjectId(String projectId);
}