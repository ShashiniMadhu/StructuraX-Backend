package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.util.Date;
import java.util.List;

public interface FinancialOfficerDAO {

    List<ProjectDTO> getAllProjects();

    ProjectDTO getProjectById(String id);



    //PaymentPlanDTO createPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO getPaymentPlanById(Integer id);

   // PaymentPlanDTO updatePaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO deletePaymentPlanById(String id);






    /* installment CRUD */
    //InstallmentDTO createInstallment(InstallmentDTO installmentDTO);

    List<InstallmentDTO> getInstallmentsByPaymentPlanId(Integer id);

    //InstallmentDTO getInstallmentById(Integer id);

    //InstallmentDTO updateInstallment(InstallmentDTO installmentDTO);

    //List<InstallmentDTO> deleteInstallmentsByPaymentPlanId(Integer id);

    //InstallmentDTO deleteInstallmentById(Integer id);

    PaymentPlanDTO getPaymentPlanByProjectId(String id);

    PaymentPlanDTO createFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);

    PaymentPlanDTO updateFullPaymentPlan(PaymentPlanDTO paymentPlanDTO);


    /* labor salary */
    List<LaborAttendanceDTO> getLaborAttendanceByProjectId(String projectId, Date date);

    LaborAttendanceDTO getAttendanceById(int attendanceId);

    LaborSalaryDTO insertSalary(LaborSalaryDTO laborSalaryDTO);

    LaborSalaryDTO getSalaryRecordById(int salaryId);

    LaborSalaryDTO deleteSalaryRecordById(int attendanceId);

    List<LaborSalaryDTO> updateSalaryRecord(List<LaborSalaryDTO> laborSalaryDTO);
}
