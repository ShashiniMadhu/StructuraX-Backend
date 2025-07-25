package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.FinancialOfficerDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.FinancialOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public ProjectDTO getProjectById(String id) {
        return financialOfficerDAO.getProjectById(id);
    }



    @Override
    public PaymentPlanDTO getPaymentPlanById(Integer id) {
        PaymentPlanDTO plan = financialOfficerDAO.getPaymentPlanById(id); // fetch plan
        if (plan != null) {
            List<InstallmentDTO> installments = financialOfficerDAO.getInstallmentsByPaymentPlanId(id); // fetch installments
            plan.setInstallments(installments); // attach
        }
        return plan;
    }

    @Override
    public List<InstallmentDTO> getInstallmentsByPaymentPlanId(Integer id) {
        return financialOfficerDAO.getInstallmentsByPaymentPlanId(id);
    }

    @Override
    public PaymentPlanDTO getPaymentPlanByProjectId(String id) {
        return financialOfficerDAO.getPaymentPlanByProjectId(id);
    }

    @Override
    public PaymentPlanDTO createFullPaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        PaymentPlanDTO payment=financialOfficerDAO.createFullPaymentPlan(paymentPlanDTO);
        return payment;
    }

    @Override
    public PaymentPlanDTO deletePaymentPlanById(String id) {
        return financialOfficerDAO.deletePaymentPlanById(id);
    }

    @Override
    public PaymentPlanDTO updateFullPaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        return financialOfficerDAO.updateFullPaymentPlan(paymentPlanDTO);
    }

    @Override
    public List<LaborAttendanceDTO> getLaborAttendanceByProjectId(String projectId, Date date) {
        return financialOfficerDAO.getLaborAttendanceByProjectId(projectId,date);
    }

    @Override
    public LaborAttendanceDTO getAttendanceById(int attendanceId) {
        return  financialOfficerDAO.getAttendanceById(attendanceId);
    }

    @Override
    public LaborSalaryDTO insertSalary(LaborSalaryDTO laborSalaryDTO) {
        LaborSalaryDTO laborSalary=financialOfficerDAO.insertSalary(laborSalaryDTO);
        return laborSalary;
    }

    @Override
    public LaborSalaryDTO getSalaryRecordById(int salaryId) {
        return financialOfficerDAO.getSalaryRecordById(salaryId);
    }

    @Override
    public List<LaborSalaryDTO> updateSalaryRecord(List<LaborSalaryDTO> laborSalaryDTO) {
        return financialOfficerDAO.updateSalaryRecord(laborSalaryDTO);
    }

    @Override
    public LaborSalaryDTO deleteSalaryRecordById(int salaryId) {
        return financialOfficerDAO.deleteSalaryRecordById(salaryId);
    }








    /*@Override
    public PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO) {
        return financialOfficerDAO.updatePaymentPlan(paymentPlanDTO);
    }*/


    /*@Override
    public InstallmentDTO createInstallment(InstallmentDTO installmentDTO) {
        InstallmentDTO installment=financialOfficerDAO.createInstallment(installmentDTO);
        return installment;
    }*/



    /*@Override
    public InstallmentDTO getInstallmentById(Integer id) {
        return financialOfficerDAO.getInstallmentById(id);
    }

    @Override
    public InstallmentDTO updateInstallment(InstallmentDTO installmentDTO) {
        return financialOfficerDAO.updateInstallment(installmentDTO);
    }

    @Override
    public List<InstallmentDTO> deleteInstallmentsByPaymentPlanId(Integer id) {
        return financialOfficerDAO.deleteInstallmentsByPaymentPlanId(id);
    }

    @Override
    public InstallmentDTO deleteInstallmentById(Integer id) {
        return financialOfficerDAO.deleteInstallmentById(id);
    }
*/





}
