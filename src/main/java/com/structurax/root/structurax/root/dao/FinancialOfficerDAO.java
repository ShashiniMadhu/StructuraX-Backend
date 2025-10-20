package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.math.BigDecimal;
import java.sql.SQLException;
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


    /* Labor payments */
    LaborPaymentDTO createLaborPayment(LaborPaymentDTO paymentDTO);
    List<LaborPaymentDTO> getAllLaborPayments();
    LaborPaymentDTO updateLaborPaymentRecord(LaborPaymentDTO laborPaymentDTO);
    LaborPaymentDTO getLaborPaymentRecordById(int paymentId );
    LaborPaymentDTO deletePaymentRecordById(int paymentId);

    /* order payments*/
    List<PurchaseOrderDTO> getAllOrders();
    PurchaseOrderDTO updateOrderPaymentStatus(PurchaseOrderDTO orderDTO);

    /* Project payments done by client */

    /* Petty cash */
    PettyCashDTO insertPettyCash(PettyCashDTO pettyCashDTO);
    Boolean updatePettyCash(PettyCashDTO pettyCashDTO) throws SQLException;
    Boolean deletePettyCash(int pettyCashId);
    List<PettyCashDTO> getAllPettyCash();

    // payment confirmation
    List<PaymentConfirmationDTO> getAllConfirmations();

    List<PaymentConfirmationDTO> getConfirmationsByProject(String projectId);

    PaymentConfirmationDTO insertConfirmation(PaymentConfirmationDTO dto);

    PaymentConfirmationDTO updateConfirmation(PaymentConfirmationDTO dto);

    void deleteConfirmation(int confirmationId);

    List<PaymentDTO> getAllPayments();

    // project expenses
    BigDecimal calculateProjectExpenses(String projectId);


}
