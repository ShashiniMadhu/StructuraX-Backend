package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface ProjectOwnerMaterialsService {

    // Material Methods
    List<ProjectMaterialDTO> getMaterialsByProjectId(String projectId);
    MaterialSummaryDTO getMaterialSummary(String projectId);

    // Site Visit Methods
    SiteVisitDTO createSiteVisitRequest(SiteVisitDTO siteVisitDTO);
    List<SiteVisitDTO> getSiteVisits(String projectId);
    SiteVisitDTO getSiteVisitById(Integer visitId);
    void updateSiteVisitStatus(Integer visitId, String status);

    // Payment Methods
    PaymentSummaryDTO getPaymentSummary(String projectId);
    List<InstallmentDTO> getPaymentHistory(String projectId);
    List<InstallmentDTO> getUpcomingPayments(String projectId);
    PaymentReceiptDTO uploadPaymentReceipt(PaymentReceiptDTO receiptDTO);
    List<PaymentReceiptDTO> getPaymentReceipts(String projectId);
}