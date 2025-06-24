package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;

import java.util.List;

public interface FinancialOfficerDAO {

    List<ProjectDTO> getAllProjects();

    PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO getPaymentPlanById(Integer id);

    PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO deletePaymentPlanById(Integer id);

    ProjectDTO getProjectById(Integer id);
}
