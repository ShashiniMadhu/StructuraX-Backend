package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import com.structurax.root.structurax.root.dto.VisitRequestDTO;
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


}