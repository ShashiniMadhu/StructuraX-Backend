package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.FinancialOfficerDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.FinancialOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
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
    public LaborPaymentDTO createLaborPayment(LaborPaymentDTO paymentDTO) {
        LaborPaymentDTO payment = financialOfficerDAO.createLaborPayment(paymentDTO);
        return paymentDTO;
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


    @Override
    public List<LaborPaymentDTO> getAllLaborPayments() {
        return financialOfficerDAO.getAllLaborPayments();
    }

    @Override
    public LaborPaymentDTO updateLaborPaymentRecord(LaborPaymentDTO laborPaymentDTO) {
        return  financialOfficerDAO.updateLaborPaymentRecord(laborPaymentDTO);
    }

    @Override
    public LaborPaymentDTO getPaymentRecordById(int paymentId) {
        return financialOfficerDAO.getLaborPaymentRecordById(paymentId);
    }

    @Override
    public LaborPaymentDTO deletePaymentRecordById(int paymentId) {
        return  financialOfficerDAO.deletePaymentRecordById(paymentId);
    }

    @Override
    public List<PurchaseOrderDTO> getAllOrders() {
        return financialOfficerDAO.getAllOrders();
    }

    @Override
    public PurchaseOrderDTO updateOrdersPaymentStatus(PurchaseOrderDTO orderDTO) {
        return financialOfficerDAO.updateOrderPaymentStatus(orderDTO);
    }

    @Override
    public PettyCashDTO insertPettyCash(PettyCashDTO pettyCashDTO) {
        return financialOfficerDAO.insertPettyCash(pettyCashDTO);
    }

    @Override
    public Boolean updatePettyCash(PettyCashDTO pettyCashDTO) throws SQLException {
        return financialOfficerDAO.updatePettyCash(pettyCashDTO);
    }

    @Override
    public Boolean deletePettyCash(int pettyCashId) {
        return financialOfficerDAO.deletePettyCash(pettyCashId);
    }

    @Override
    public List<PettyCashDTO> getAllPettyCash() {
        return financialOfficerDAO.getAllPettyCash();
    }



    @Override
    public List<PaymentConfirmationDTO> getAllConfirmations() {
        return financialOfficerDAO.getAllConfirmations();
    }

    @Override
    public List<PaymentConfirmationDTO> getConfirmationsByProject(String projectId) {
        return financialOfficerDAO.getConfirmationsByProject(projectId);
    }

    @Override
    public PaymentConfirmationDTO insertConfirmation(PaymentConfirmationDTO dto) {
        return financialOfficerDAO.insertConfirmation(dto);
    }

    @Override
    public PaymentConfirmationDTO updateConfirmation(PaymentConfirmationDTO dto) {
        return financialOfficerDAO.updateConfirmation(dto);
    }

    @Override
    public void deleteConfirmation(int confirmationId) {
        return;
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return financialOfficerDAO.getAllPayments();
    }

    @Override
    public BigDecimal calculateProjectExpenses(String projectId) {
        return financialOfficerDAO.calculateProjectExpenses(projectId);
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
