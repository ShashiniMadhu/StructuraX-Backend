package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.FinancialOfficerDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.service.FinancialOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialOfficerServiceImpl implements FinancialOfficerService {

    @Autowired
    private FinancialOfficerDAO financialOfficerDAO;


    @Override
    public List<ProjectDTO> getAllProjects() {
        return financialOfficerDAO.getAllProjects();
    }

    @Override
    public ProjectDTO getProjectById(Integer id) {
        return financialOfficerDAO.getProjectById(id);
    }

    @Override
    public PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        PaymentPlanDTO paymentPlanDTO1=financialOfficerDAO.createPaymentPlan(paymentPlanDTO);
        return paymentPlanDTO1;
    }

    @Override
    public PaymentPlanDTO getPaymentPlanById(Integer id) {
        return financialOfficerDAO.getPaymentPlanById(id);
    }

    @Override
    public PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        return financialOfficerDAO.updatePaymentPlan(paymentPlanDTO);
    }

    @Override
    public PaymentPlanDTO deletePaymentPlanById(Integer id) {
        return financialOfficerDAO.deletePaymentPlanById(id);
    }

}
