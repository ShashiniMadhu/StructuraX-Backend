package com.structurax.root.structurax.root.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.FinancialOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
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

    @GetMapping("labor_payment")
    public ResponseEntity<List<LaborPaymentDTO>> getAllLaborPayments(){
        List<LaborPaymentDTO> paymentDTOS = financialOfficerService.getAllLaborPayments();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(paymentDTOS, HttpStatus.OK);
    }




}
