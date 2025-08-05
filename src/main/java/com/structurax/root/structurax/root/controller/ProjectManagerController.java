package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.ProjectManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @GetMapping("/visits")
    public ResponseEntity<List<SiteVisitLogDTO>> getAllVisits() {
        return ResponseEntity.ok(ProjectManagerService.getAllVisitLogs());
    }

    @GetMapping("/visits/{id}")
    public ResponseEntity<SiteVisitLogDTO> getVisitById(@PathVariable Integer id) {
        return ResponseEntity.ok(ProjectManagerService.getVisitLogById(id));
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

    @GetMapping("/request")
    public ResponseEntity<List<VisitRequestDTO>> getAllVisitRequests (){
        return ResponseEntity.ok(ProjectManagerService.getAllVisitRequests());
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

    @GetMapping("/requestSiteResources/{pm_id}")
    public ResponseEntity<List<RequestSiteResourceDTO>> getRequestSiteResourcesByPmId(
            @PathVariable("pm_id") String pmId) {
        List<RequestSiteResourceDTO> list = ProjectManagerService.getRequestSiteResourcesByPmId(pmId);
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
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








}