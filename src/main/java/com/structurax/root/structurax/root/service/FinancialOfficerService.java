package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;
import org.springframework.stereotype.Service;

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




   // PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    //PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO);

    //InstallmentDTO createInstallment(InstallmentDTO installmentDTO);

    //InstallmentDTO getInstallmentById(Integer id);

    //InstallmentDTO updateInstallment(InstallmentDTO installmentDTO);

    //List<InstallmentDTO> deleteInstallmentsByPaymentPlanId(Integer id);

    //InstallmentDTO deleteInstallmentById(Integer id);


    /* labor payment */
    List<LaborAttendanceDTO> getLaborAttendanceByProjectId(String projectId, Date date);

    LaborAttendanceDTO getAttendanceById(int attendanceId);

    LaborSalaryDTO insertSalary(LaborSalaryDTO laborSalaryDTO);

    LaborSalaryDTO getSalaryRecordById(int salaryId);

    List<LaborSalaryDTO> updateSalaryRecord(List<LaborSalaryDTO> laborSalaryDTO);

    LaborSalaryDTO deleteSalaryRecordById(int salaryId);









}
