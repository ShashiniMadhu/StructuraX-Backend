package com.structurax.root.structurax.root.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.DailyUpdateDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientDTO;
import com.structurax.root.structurax.root.dto.SiteVisitWithParticipantsDTO;
import com.structurax.root.structurax.root.service.FinancialOfficerService;
import com.structurax.root.structurax.root.service.QSProjectService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/qs/projects")
public class QSProjectController {
    
    @Autowired
    private QSProjectService qsProjectService;
    
    @Autowired
    private FinancialOfficerService financialOfficerService;
    
    /**
     * Endpoint to get all projects with client details and images.
     * This returns project information including client ID, name, and project images.
     * 
     * @return ResponseEntity with list of projects with client information
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProjectWithClientDTO>> getAllProjects() {
        try {
            List<ProjectWithClientDTO> projects = qsProjectService.getAllProjects();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to get projects related to a specific employee ID with client details and images.
     * This returns project information including client ID, name, and project images
     * for all projects where the employee is assigned as QS, PM, or SS.
     * 
     * @param employeeId the employee ID (can be QS, PM, or SS)
     * @return ResponseEntity with list of projects assigned to the employee
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ProjectWithClientDTO>> getProjectsByEmployeeId(@PathVariable String employeeId) {
        try {
            List<ProjectWithClientDTO> projects = qsProjectService.getProjectsByEmployeeId(employeeId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to get design file details for a specific project.
     * This returns all design files associated with a project including design link,
     * type, status, priority, and other design-related information.
     * 
     * @param projectId the project ID
     * @return ResponseEntity with list of design files for the project
     */
    @GetMapping("/design/{projectId}")
    public ResponseEntity<List<DesignDTO>> getDesignFilesByProjectId(@PathVariable String projectId) {
        try {
            List<DesignDTO> designs = qsProjectService.getDesignFilesByProjectId(projectId);
            return ResponseEntity.ok(designs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to get daily updates for a specific project.
     * This returns all daily updates associated with a project including date,
     * note, employee ID, and employee name.
     * 
     * @param projectId the project ID
     * @return ResponseEntity with list of daily updates for the project
     */
    @GetMapping("/daily-updates/{projectId}")
    public ResponseEntity<List<DailyUpdateDTO>> getDailyUpdatesByProjectId(@PathVariable String projectId) {
        try {
            List<DailyUpdateDTO> dailyUpdates = qsProjectService.getDailyUpdatesByProjectId(projectId);
            return ResponseEntity.ok(dailyUpdates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to get site visits with participants for a specific project.
     * This returns all site visits associated with a project including visit date,
     * description, status, and list of participating employees with their details.
     * 
     * @param projectId the project ID
     * @return ResponseEntity with list of site visits with participants for the project
     */
    @GetMapping("/site-visits/{projectId}")
    public ResponseEntity<List<SiteVisitWithParticipantsDTO>> getSiteVisitsByProjectId(@PathVariable String projectId) {
        try {
            List<SiteVisitWithParticipantsDTO> siteVisits = qsProjectService.getSiteVisitsByProjectId(projectId);
            return ResponseEntity.ok(siteVisits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Endpoint to get payment plan with installment details for a specific project.
     * This returns the payment plan associated with a project including payment plan ID,
     * created date, total amount, start date, end date, number of installments,
     * and a list of all installments with their details (installment ID, amount, due date, 
     * status, and paid date).
     * 
     * @param projectId the project ID
     * @return ResponseEntity with payment plan and installments for the project
     */
    @GetMapping("/payment-plan/{projectId}")
    public ResponseEntity<PaymentPlanDTO> getPaymentPlanByProjectId(@PathVariable String projectId) {
        try {
            PaymentPlanDTO paymentPlan = financialOfficerService.getPaymentPlanByProjectId(projectId);
            if (paymentPlan == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(paymentPlan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
