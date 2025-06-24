package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FinancialOfficerService {

    List<ProjectDTO> getAllProjects();

    ProjectDTO getProjectById(Integer id);

    PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO getPaymentPlanById(Integer id);

    PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO deletePaymentPlanById(Integer id);




}
