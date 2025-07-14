package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.VisitLogDTO;
import com.structurax.root.structurax.root.service.VisitLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visit-logs")
public class VisitLogController {

    @Autowired
    private VisitLogService visitLogService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping
    public ResponseEntity<VisitLogDTO> createVisit(@RequestBody VisitLogDTO dto) {
        VisitLogDTO created = visitLogService.createVisitLog(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<VisitLogDTO>> getAllVisits() {
        return ResponseEntity.ok(visitLogService.getAllVisitLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitLogDTO> getVisitById(@PathVariable Integer id) {
        return ResponseEntity.ok(visitLogService.getVisitLogById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<VisitLogDTO> deleteVisit(@PathVariable Integer id) {
        return ResponseEntity.ok(visitLogService.deleteVisitLogById(id));
    }
}