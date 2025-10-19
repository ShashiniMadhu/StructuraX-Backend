package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Service
public interface FinancialOfficerService {

    List<ProjectDTO> getAllProjects();

    ProjectDTO getProjectById(String id);

    PaymentPlanDTO getPaymentPlanById(Integer id);

    /* full payment plan including installments*/
    List<InstallmentDTO> getInstallmentsByPaymentPlanId(Integer id);

    PaymentPlanDTO getPaymentPlanByProjectId(String id);

    PaymentPlanDTO createFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO deletePaymentPlanById(String id);

    PaymentPlanDTO updateFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);


    /* Labor payments */
    LaborPaymentDTO createLaborPayment(LaborPaymentDTO paymentDTO);
    List<LaborPaymentDTO> getAllLaborPayments();
    LaborPaymentDTO updateLaborPaymentRecord(LaborPaymentDTO laborPaymentDTO);
    LaborPaymentDTO getPaymentRecordById(int paymentId);
    LaborPaymentDTO deletePaymentRecordById(int paymentId);

    /* Order payments */
    List<PurchaseOrderDTO> getAllOrders();
    PurchaseOrderDTO updateOrdersPaymentStatus(PurchaseOrderDTO orderDTO);

    // petty cash
    PettyCashDTO insertPettyCash(PettyCashDTO pettyCashDTO);
    Boolean updatePettyCash(PettyCashDTO pettyCashDTO) throws SQLException;
    Boolean deletePettyCash(int pettyCashId);
    List<PettyCashDTO> getAllPettyCash();



   // PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    //PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO);

    //InstallmentDTO createInstallment(InstallmentDTO installmentDTO);

    //InstallmentDTO getInstallmentById(Integer id);

    //InstallmentDTO updateInstallment(InstallmentDTO installmentDTO);

    //List<InstallmentDTO> deleteInstallmentsByPaymentPlanId(Integer id);

    //InstallmentDTO deleteInstallmentById(Integer id);


    /* labor salary */
    List<LaborAttendanceDTO> getLaborAttendanceByProjectId(String projectId, Date date);

    LaborAttendanceDTO getAttendanceById(int attendanceId);

    LaborSalaryDTO insertSalary(LaborSalaryDTO laborSalaryDTO);

    LaborSalaryDTO getSalaryRecordById(int salaryId);

    List<LaborSalaryDTO> updateSalaryRecord(List<LaborSalaryDTO> laborSalaryDTO);

    LaborSalaryDTO deleteSalaryRecordById(int salaryId);









}
