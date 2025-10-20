package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.ProjectManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/project_manager")
@CrossOrigin("http://localhost:5173")

public class ProjectManagerController {

    @Autowired
    private ProjectManagerService ProjectManagerService;

    @PostMapping("/add_visit")
    public String createVisit(@RequestBody SiteVisitLogDTO dto) {
        ProjectManagerService.createVisitLog(dto);
        return "New Visit Log Added Successfully";
    }

    @GetMapping("/site-visits/{pm_id}")
    public ResponseEntity<List<SiteVisitLogDTO>> getSiteVisitsByPmId(@PathVariable("pm_id") String pmId) {
        List<SiteVisitLogDTO> siteVisits = ProjectManagerService.getSiteVisitLogsByPmId(pmId);
        if (siteVisits.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(siteVisits);
    }

    @PutMapping("/visits/{id}")
    public ResponseEntity<String> updateVisit(@PathVariable Integer id, @RequestBody SiteVisitLogDTO dto) {
        dto.setId(id);
        boolean updated = ProjectManagerService.updateVisitLog(dto);

        if (updated) {
            return ResponseEntity.ok("Visit log updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/request/{pm_id}")
    public ResponseEntity<List<VisitRequestDTO>> getAllVisitRequests (@PathVariable("pm_id") String pmId){
        List<VisitRequestDTO> allRequests = ProjectManagerService.getAllVisitRequests(pmId);
        if (allRequests.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allRequests);
    }

    @PutMapping("/request/{id}/accept")
    public ResponseEntity<String> acceptVisitRequest(@PathVariable Integer id){
        VisitRequestDTO dto = new VisitRequestDTO();
        dto.setId(id);
        dto.setStatus("completed");

        boolean updated = ProjectManagerService.updateVisitRequest(dto);
        if (updated) {
            return ResponseEntity.ok("Visit request accepted and marked as completed");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/request/{id}/reject")
    public ResponseEntity<String> rejectVisitRequest(@PathVariable Integer id){
        VisitRequestDTO dto = new VisitRequestDTO();
        dto.setId(id);
        dto.setStatus("cancelled");

        boolean updated = ProjectManagerService.updateVisitRequest(dto);
        if (updated) {
            return ResponseEntity.ok("Visit request rejected and marked as cancelled");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/projects/{pm_id}")
    public ResponseEntity<List<ProjectInitiateDTO>> getMyOngoingProjects(@PathVariable String pm_id){
        return ResponseEntity.ok(ProjectManagerService.getOngoingProjectsByPmId(pm_id));
    }

    @GetMapping("/pending-resources/{pm_id}")
    public ResponseEntity<Map<String, Object>> getPendingResources(@PathVariable("pm_id") String pmId) {
        List<RequestSiteResourceDTO> pendingRequests = ProjectManagerService.getPendingRequestsByPmId(pmId);
        Map<String, Object> response = new HashMap<>();

        for (RequestSiteResourceDTO request : pendingRequests) {
            List<SiteResourcesDTO> resources = ProjectManagerService.getSiteResourcesByRequestId(request.getRequestId());

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("request_details", request);
            requestData.put("resources", resources);

            response.put("request_" + request.getRequestId(), requestData);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/requestSiteResources/{id}/accept")
    public ResponseEntity<String> acceptRequestSiteResource(@PathVariable("id") Integer id) {
        boolean ok = ProjectManagerService.approveRequestSiteResource(id);
        return ResponseEntity.ok("Request " + id + " approved.");
    }

    @PutMapping("/requestSiteResources/{id}/reject")
    public ResponseEntity<String> rejectRequestSiteResource(@PathVariable("id") Integer id) {
        boolean ok = ProjectManagerService.rejectRequestSiteResource(id);
        return ResponseEntity.ok("Request " + id + " rejected.");
    }

    @GetMapping("/todo/{employeeId}")
    public ResponseEntity<List<TodoDTO>> getTodosByEmployeeId(@PathVariable String employeeId) {
        return ResponseEntity.ok(ProjectManagerService.getTodosByEmployeeId(employeeId));
    }

    @PostMapping("/todo/{employeeId}")
    public ResponseEntity<TodoDTO> createTodo(@PathVariable String employeeId,@RequestBody TodoDTO todo) {
        todo.setEmployeeId(employeeId);
        TodoDTO created = ProjectManagerService.createTodo(todo);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/todo/{taskId}")
    public ResponseEntity<String> updateTodo(@PathVariable Integer taskId,@RequestBody TodoDTO todo) {
        todo.setTaskId(taskId);
        return ProjectManagerService.updateTodo(todo) ? ResponseEntity.ok("Todo updated") : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/todo/{taskId}")
    public ResponseEntity<String> deleteTodo(@PathVariable Integer taskId) {
        return ProjectManagerService.deleteTodo(taskId) ? ResponseEntity.ok("Todo deleted") : ResponseEntity.notFound().build();
    }

    @GetMapping("/daily-updates/{pm_id}")
    public ResponseEntity<List<DailyUpdatesDTO>> getDailyUpdatesByPmId(@PathVariable("pm_id") String pmId) {
        List<DailyUpdatesDTO> updates = ProjectManagerService.getDailyUpdatesByPmId(pmId);
        if (updates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(updates);
    }

    @GetMapping("/null-location-projects/{pm_id}")
    public ResponseEntity<List<ProjectInitiateDTO>> getNullLocationProjectsByPmId(@PathVariable("pm_id") String pmId) {
        List<ProjectInitiateDTO> projects = ProjectManagerService.getNullLocationProjectsByPmId(pmId);
        if (projects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/update-location/{project_id}")
    public ResponseEntity<String> updateProjectLocation(@PathVariable("project_id") String projectId, @RequestParam String location) {
        boolean updated = ProjectManagerService.updateProjectLocation(projectId, location);
        if (updated) {
            return ResponseEntity.ok("Project location updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to update project location");
        }
    }

    @PostMapping("/add-project-material")
    public ResponseEntity<String> insertProjectMaterial(@RequestBody ProjectMaterialsDTO projectMaterials) {
        boolean inserted = ProjectManagerService.insertProjectMaterials(projectMaterials);
        if (inserted) {
            return ResponseEntity.ok("Project material inserted successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to insert project material");
        }
    }

    @GetMapping("/projects/{pm_id}/ongoing")
    public ResponseEntity<List<ProjectInitiateDTO>> getOngoingProjects(@PathVariable("pm_id") String pmId) {
        List<ProjectInitiateDTO> ongoingProjects = ProjectManagerService.getOngoingProjectsByPmId(pmId);
        return ResponseEntity.ok(ongoingProjects);
    }

    @GetMapping("/projects/{pm_id}/completed")
    public ResponseEntity<List<ProjectInitiateDTO>> getCompletedProjects(@PathVariable("pm_id") String pmId) {
        List<ProjectInitiateDTO> completedProjects = ProjectManagerService.getCompletedProjectsByPmId(pmId);
        return ResponseEntity.ok(completedProjects);
    }

    @GetMapping("/design-link/{project_id}")
    public ResponseEntity<List<DesignDTO>> getDesignLink(@PathVariable("project_id") String projectId) {
        List<DesignDTO> designLink = ProjectManagerService.getDesignLink(projectId);
        return ResponseEntity.ok(designLink);
    }

    @GetMapping("/wbs/{project_id}")
    public ResponseEntity<List<WBSDTO>> getWBSByProjectId(@PathVariable("project_id") String projectId) {
        List<WBSDTO> wbsList = ProjectManagerService.getWBSByProjectId(projectId);
        return ResponseEntity.ok(wbsList);
    }

    @GetMapping("/boq/{project_id}")
    public ResponseEntity<List<BOQitemDTO>> getBOQItems(@PathVariable("project_id") String projectId) {
        List<BOQitemDTO> boqItems = ProjectManagerService.getBOQItemsByProjectId(projectId);
        return ResponseEntity.ok(boqItems);
    }

    @GetMapping("/payment/{project_id}")
    public ResponseEntity<List<PaymentDTO>> getPaymentByProjectId(@PathVariable("project_id") String projectId) {
        List<PaymentDTO> payments = ProjectManagerService.getPaymentByProjectId(projectId);
        if (payments != null && !payments.isEmpty()) {
            return ResponseEntity.ok(payments);
        }
        // Return empty list instead of 404
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/materials/{project_id}")
    public ResponseEntity<List<ProjectMaterialsDTO>> getProjectMaterials(@PathVariable("project_id") String projectId){
        List<ProjectMaterialsDTO> materials = ProjectManagerService.getProjectMaterialsByProjectId(projectId);
        return ResponseEntity.ok(materials);
    }


    @GetMapping("/projects/ongoing/ids")
    public ResponseEntity<List<String>> getOngoingProjectIds() {
        List<String> ongoingProjectIds = ProjectManagerService.getOngoingProjectIds();
        return ResponseEntity.ok(ongoingProjectIds);
    }

}

