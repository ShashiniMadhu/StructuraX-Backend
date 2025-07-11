package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.InstallmentDTO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;

import java.util.List;

public interface FinancialOfficerDAO {

    List<ProjectDTO> getAllProjects();

    ProjectDTO getProjectById(Integer id);


    //PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO getPaymentPlanById(Integer id);

   // PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO deletePaymentPlanById(Integer id);




    /* installment CRUD */
    //InstallmentDTO createInstallment(InstallmentDTO installmentDTO);

    List<InstallmentDTO> getInstallmentsByPaymentPlanId(Integer id);

    //InstallmentDTO getInstallmentById(Integer id);

    //InstallmentDTO updateInstallment(InstallmentDTO installmentDTO);

    //List<InstallmentDTO> deleteInstallmentsByPaymentPlanId(Integer id);

    //InstallmentDTO deleteInstallmentById(Integer id);

    PaymentPlanDTO getPaymentPlanByProjectId(Integer id);

    PaymentPlanDTO createFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO updateFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);
}
