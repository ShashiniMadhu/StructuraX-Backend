package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import com.structurax.root.structurax.root.service.ProjectManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/project_manager")
public class ProjectManagerController {

    @Autowired
    private ProjectManagerService ProjectManagerService;

    @PostMapping("/visit_logs")
    public ResponseEntity<SiteVisitLogDTO> createVisit(@RequestBody SiteVisitLogDTO dto) {
        SiteVisitLogDTO created = ProjectManagerService.createVisitLog(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<SiteVisitLogDTO>> getAllVisits() {
        return ResponseEntity.ok(ProjectManagerService.getAllVisitLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiteVisitLogDTO> getVisitById(@PathVariable Integer id) {
        return ResponseEntity.ok(ProjectManagerService.getVisitLogById(id));
    }
}