package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.service.FinancialOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

   /*@GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getProjects() {
        System.out.println("Endpoint hit"); // Check this in console
        return ResponseEntity.ok(financialOfficerService.getAllProjects());
    }*/

    @GetMapping("/project/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Integer id){
        ProjectDTO project = financialOfficerService.getProjectById(id);
        return new ResponseEntity<>(project,HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<PaymentPlanDTO> createPaymentPlan(@RequestBody PaymentPlanDTO paymentPlanDTO){
        financialOfficerService.createPaymentPlan(paymentPlanDTO);
        return new ResponseEntity<>(paymentPlanDTO, HttpStatus.OK);
    }

    @GetMapping("/payment_plan/{id}")
    public ResponseEntity<PaymentPlanDTO> getPaymentPlanById(@PathVariable Integer id){
        PaymentPlanDTO paymentPlan = financialOfficerService.getPaymentPlanById(id);
        return new ResponseEntity<>(paymentPlan,HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<PaymentPlanDTO> updatePaymentPlan(@RequestBody PaymentPlanDTO paymentPlanDTO){
        financialOfficerService.updatePaymentPlan(paymentPlanDTO);
        return new ResponseEntity<>(paymentPlanDTO,HttpStatus.OK);
    }

    @DeleteMapping("/payment_plan/{id}")
    public ResponseEntity<PaymentPlanDTO> deletePaymentPlanById(@PathVariable Integer id){
        PaymentPlanDTO paymentPlan = financialOfficerService.deletePaymentPlanById(id);
        return new ResponseEntity<>(paymentPlan, HttpStatus.OK);
    }
}
