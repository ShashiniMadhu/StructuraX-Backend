package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.InstallmentDTO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FinancialOfficerService {

    List<ProjectDTO> getAllProjects();

    ProjectDTO getProjectById(Integer id);

    PaymentPlanDTO getPaymentPlanById(Integer id);

    /* full payment plan including installments*/
    List<InstallmentDTO> getInstallmentsByPaymentPlanId(Integer id);

    PaymentPlanDTO getPaymentPlanByProjectId(Integer id);

    PaymentPlanDTO createFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO deletePaymentPlanById(Integer id);

    PaymentPlanDTO updateFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);




   // PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    //PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO);

    //InstallmentDTO createInstallment(InstallmentDTO installmentDTO);

    //InstallmentDTO getInstallmentById(Integer id);

    //InstallmentDTO updateInstallment(InstallmentDTO installmentDTO);

    //List<InstallmentDTO> deleteInstallmentsByPaymentPlanId(Integer id);

    //InstallmentDTO deleteInstallmentById(Integer id);









}
