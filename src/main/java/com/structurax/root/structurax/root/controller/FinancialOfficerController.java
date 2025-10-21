package com.structurax.root.structurax.root.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.FinancialOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/financial_officer")
public class FinancialOfficerController {

    @Autowired
    FinancialOfficerService financialOfficerService;

    @GetMapping()
    public ResponseEntity<List<ProjectDTO>> getAllProjects(){
        List<ProjectDTO> projectDTOS = financialOfficerService.getAllProjects();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(projectDTOS, HttpStatus.OK);
    }


    @GetMapping("/project/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable String id){
        ProjectDTO project = financialOfficerService.getProjectById(id);
        return new ResponseEntity<>(project,HttpStatus.OK);
    }

    @GetMapping("/payment_plan/installments/{id}")
    public ResponseEntity<List<InstallmentDTO>> getInstallmentsByPaymentPlanId(@PathVariable Integer id){
        List<InstallmentDTO> installments = financialOfficerService.getInstallmentsByPaymentPlanId(id);
        return new ResponseEntity<>(installments, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<PaymentPlanDTO> createFullPaymentPlan(@RequestBody PaymentPlanDTO paymentPlanDTO){
        financialOfficerService.createFullPaymentPlan(paymentPlanDTO);
        System.out.println("Received projectId: " + paymentPlanDTO.getProjectId());
        return new ResponseEntity<>(paymentPlanDTO, HttpStatus.OK);
    }

    @GetMapping("/payment_plan/{id}")
    public ResponseEntity<PaymentPlanDTO> getPaymentPlanById(@PathVariable Integer id){
        PaymentPlanDTO paymentPlan = financialOfficerService.getPaymentPlanById(id);
        return new ResponseEntity<>(paymentPlan,HttpStatus.OK);
    }

    @GetMapping("/payment_plan/full/{id}")
    public ResponseEntity<PaymentPlanDTO> getPaymentPlanByProjectId(@PathVariable String id){
        PaymentPlanDTO paymentPlan = financialOfficerService.getPaymentPlanByProjectId(id);
        return new ResponseEntity<>(paymentPlan,HttpStatus.OK);
    }


    @PutMapping("payment_plan/full")
    public ResponseEntity<PaymentPlanDTO> updateFullPaymentPlan(@RequestBody PaymentPlanDTO paymentPlanDTO){
        financialOfficerService.updateFullPaymentPlan(paymentPlanDTO);
        return new ResponseEntity<>(paymentPlanDTO,HttpStatus.OK);
    }




    /*@PutMapping()
    public ResponseEntity<PaymentPlanDTO> updatePaymentPlan(@RequestBody PaymentPlanDTO paymentPlanDTO){
        financialOfficerService.updatePaymentPlan(paymentPlanDTO);
        return new ResponseEntity<>(paymentPlanDTO,HttpStatus.OK);
    }


    @PostMapping("/installment")
    public ResponseEntity<InstallmentDTO> createInstallment(@RequestBody InstallmentDTO installmentDTO){
        financialOfficerService.createInstallment(installmentDTO);
        return new ResponseEntity<>(installmentDTO, HttpStatus.OK);
    }



    @GetMapping("/installment/{id}")
    public ResponseEntity<InstallmentDTO> getInstallmentById(@PathVariable Integer id){
        InstallmentDTO installmentDTO = financialOfficerService.getInstallmentById(id);
        return new ResponseEntity<>(installmentDTO,HttpStatus.OK);
    }

    @PutMapping("/installment")
    public ResponseEntity<InstallmentDTO> updateInstallment(@RequestBody InstallmentDTO installmentDTO){
        financialOfficerService.updateInstallment(installmentDTO);
        return new ResponseEntity<>(installmentDTO,HttpStatus.OK);
    }

    @DeleteMapping("/payment_plan/installments/{id}")
    public ResponseEntity<List<InstallmentDTO>> deleteInstallmentsByPaymentPlanId(@PathVariable Integer id){
        List<InstallmentDTO> installments = financialOfficerService.deleteInstallmentsByPaymentPlanId(id);
        return new ResponseEntity<>(installments, HttpStatus.OK);
    }

    @DeleteMapping("/payment_plan/installment/{id}")
    public ResponseEntity<InstallmentDTO> deleteInstallmentById(@PathVariable Integer id){
        InstallmentDTO installmentDTO = financialOfficerService.deleteInstallmentById(id);
        return new ResponseEntity<>(installmentDTO, HttpStatus.OK);
    }*/


    @DeleteMapping("/payment_plan/{projectId}")
    public ResponseEntity<PaymentPlanDTO> deletePaymentPlanByProjectId(@PathVariable String projectId) {
        System.out.println("DELETE called for projectId: " + projectId);
        try {
            PaymentPlanDTO deletedPlan = financialOfficerService.deletePaymentPlanById(projectId);
            return new ResponseEntity<>(deletedPlan, HttpStatus.OK);
        } catch (RuntimeException ex) {
            System.out.println("Delete failed: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    /*------labor salary-----*/
    @GetMapping("/attendance/{projectId}/date")
    public ResponseEntity<List<LaborAttendanceDTO>> getLaborAttendanceByProjectId(
            @PathVariable String projectId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {

        List<LaborAttendanceDTO> attendance = financialOfficerService.getLaborAttendanceByProjectId(projectId, date);
        if (attendance == null || attendance.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attendance);
    }


    @GetMapping("/attendance/{id}")
    public ResponseEntity<LaborAttendanceDTO> getAttendanceById(@PathVariable int id){
        LaborAttendanceDTO attendance = financialOfficerService.getAttendanceById(id);
        return new ResponseEntity<>(attendance,HttpStatus.OK);
    }


    @PostMapping("/labor_salary")
    public ResponseEntity<?> insertLaborSalary(@RequestBody Object body) {
        try {
            if (body instanceof List<?>) {
                List<?> list = (List<?>) body;
                for (Object obj : list) {
                    LaborSalaryDTO dto = new ObjectMapper().convertValue(obj, LaborSalaryDTO.class);
                    financialOfficerService.insertSalary(dto);
                }
                return ResponseEntity.ok("Multiple salaries inserted successfully");
            } else {
                LaborSalaryDTO dto = new ObjectMapper().convertValue(body, LaborSalaryDTO.class);
                financialOfficerService.insertSalary(dto);
                return ResponseEntity.ok("Single salary inserted successfully");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inserting salaries: " + e.getMessage());
        }
    }


    @DeleteMapping("/labor_salary/{salaryId}")
    public ResponseEntity<LaborSalaryDTO> deleteSalaryRecordById(@PathVariable int salaryId){
        LaborSalaryDTO salaryRecord = financialOfficerService.deleteSalaryRecordById(salaryId);
        return new ResponseEntity<>(salaryRecord, HttpStatus.OK);
    }

    @PutMapping("/labor_salary")
    public ResponseEntity<List<LaborSalaryDTO>> updateSalaryRecord(@RequestBody List<LaborSalaryDTO> laborSalaryDTOList) {
        List<LaborSalaryDTO> updatedList = financialOfficerService.updateSalaryRecord(laborSalaryDTOList);
        return new ResponseEntity<>(updatedList, HttpStatus.OK);
    }

    @PostMapping("/labor_payment")
    public ResponseEntity<LaborPaymentDTO> createLaborPayment(@ModelAttribute LaborPaymentDTO paymentDTO){
        LaborPaymentDTO laborPaymentDTO = financialOfficerService.createLaborPayment(paymentDTO);
        return new ResponseEntity<>(laborPaymentDTO,HttpStatus.OK);
    }

    @GetMapping("/labor_payment")
    public ResponseEntity<List<LaborPaymentDTO>> getAllLaborPayments(){
        List<LaborPaymentDTO> paymentDTOS = financialOfficerService.getAllLaborPayments();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(paymentDTOS, HttpStatus.OK);
    }

    @PutMapping("/labor_payment")
    public ResponseEntity<LaborPaymentDTO> updateLaborPaymentRecord(@ModelAttribute LaborPaymentDTO laborPaymentDTO){
        LaborPaymentDTO paymentDTO = financialOfficerService.updateLaborPaymentRecord(laborPaymentDTO);
        return new ResponseEntity<>(paymentDTO,HttpStatus.OK);
    }

    @DeleteMapping("/labor_payment/{paymentId}")
    public ResponseEntity<LaborPaymentDTO> deletePaymentRecordById(@PathVariable int paymentId){
        LaborPaymentDTO paymentRecord = financialOfficerService.deletePaymentRecordById(paymentId);
        return new ResponseEntity<>(paymentRecord, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<PurchaseOrderDTO>> getAllOrders(){
        List<PurchaseOrderDTO> orderDTOS = financialOfficerService.getAllOrders();
        return new ResponseEntity<>(orderDTOS, HttpStatus.OK);
    }

    @PutMapping("/orders")
    public ResponseEntity<PurchaseOrderDTO> updateOrderPaymentStatus(@RequestBody PurchaseOrderDTO orderDTO){
        PurchaseOrderDTO orderDTO1 = financialOfficerService.updateOrdersPaymentStatus(orderDTO);
        return new ResponseEntity<>(orderDTO1,HttpStatus.OK);
    }

    @PostMapping("/petty_cash")
    public ResponseEntity<PettyCashDTO> insertPettyCash(@RequestBody PettyCashDTO pettyCashDTO){
        PettyCashDTO pettyCashDTO1 = financialOfficerService.insertPettyCash(pettyCashDTO);
        return new ResponseEntity<>(pettyCashDTO1,HttpStatus.OK);
    }

    @PutMapping("/petty_cash")
    public ResponseEntity<?> updatePettyCash(@RequestBody PettyCashDTO pettyCashDTO) {
        try {
            Boolean updated = financialOfficerService.updatePettyCash(pettyCashDTO);

            if (Boolean.TRUE.equals(updated)) {
                // success: return a message or the updated object
                return ResponseEntity.ok(Map.of(
                        "message", "Petty cash updated successfully",
                        "pettyCashId", pettyCashDTO.getPettyCashId()
                ));
            } else {
                // business rule prevented update -> return a message + 403 (Forbidden)
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Cannot update petty cash: expense records already exist"));
            }

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating petty cash"));
        }
    }


    @DeleteMapping("/petty_cash/{pettyCashId}")
    public ResponseEntity<?> deletePettyCash(@PathVariable int pettyCashId) {
        try {
            Boolean deleted = financialOfficerService.deletePettyCash(pettyCashId);

            if (Boolean.TRUE.equals(deleted)) {
                // Success
                return ResponseEntity.ok(Map.of(
                        "message", "Petty cash deleted successfully",
                        "pettyCashId", pettyCashId
                ));
            } else {
                // Not allowed due to existing expense records
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Cannot delete petty cash: expense records already exist"));
            }

        } catch (Exception e) {
            // Unexpected error
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting petty cash"));
        }
    }


    @GetMapping("/petty_cash")
    public ResponseEntity<List<PettyCashDTO>> getAllPettyCash(){
        List<PettyCashDTO> pettyCashDTOList = financialOfficerService.getAllPettyCash();
        return new ResponseEntity<>(pettyCashDTOList, HttpStatus.OK);
    }

    @GetMapping("/payment_confirmation")
    public ResponseEntity<List<PaymentConfirmationDTO>> getAllConfirmations() {
        List<PaymentConfirmationDTO> confirmations = financialOfficerService.getAllConfirmations();
        return new ResponseEntity<>(confirmations, HttpStatus.OK);
    }

    // Get confirmations by project
    @GetMapping("/payment_confirmation/{projectId}")
    public ResponseEntity<List<PaymentConfirmationDTO>> getConfirmationsByProject(@PathVariable String projectId) {
        List<PaymentConfirmationDTO> confirmations = financialOfficerService.getConfirmationsByProject(projectId);
        return new ResponseEntity<>(confirmations, HttpStatus.OK);
    }

    // Create a new confirmation
    @PostMapping("/payment_confirmation")
    public ResponseEntity<PaymentConfirmationDTO> createConfirmation(@RequestBody PaymentConfirmationDTO dto) {
        PaymentConfirmationDTO created = financialOfficerService.insertConfirmation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Update a confirmation
    @PutMapping("/payment_confirmation")
    public ResponseEntity<PaymentConfirmationDTO> updateConfirmation(@RequestBody PaymentConfirmationDTO dto) {
        PaymentConfirmationDTO updated = financialOfficerService.updateConfirmation(dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Delete a confirmation
    @DeleteMapping("/payment_confirmation/{confirmationId}")
    public ResponseEntity<Void> deleteConfirmation(@PathVariable int confirmationId) {
        financialOfficerService.deleteConfirmation(confirmationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getAllPayments(){
        List<PaymentDTO> paymentDTOS = financialOfficerService.getAllPayments();
        return new ResponseEntity<>(paymentDTOS, HttpStatus.OK);
    }

    @GetMapping("/expenses/{projectId}")
    public ResponseEntity<BigDecimal> getProjectExpenses(@PathVariable String projectId) {
        if (projectId == null || projectId.isEmpty()) {
            return ResponseEntity.badRequest().body(BigDecimal.ZERO);
        }

        BigDecimal totalExpenses = financialOfficerService.calculateProjectExpenses(projectId);
        return ResponseEntity.ok(totalExpenses);
    }




}
