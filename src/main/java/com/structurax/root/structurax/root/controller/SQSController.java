package com.structurax.root.structurax.root.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.BOQWithProjectDTO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.service.SQSService;

import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(value = "/sqs")
public class SQSController {

    @Autowired
    private SQSService sqsService;

    @PutMapping(value = "/assign_qs/{pid}/{eid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assign_qs(@PathVariable @Pattern(regexp = "^EMP_\\d{3}$") String eid, @PathVariable @Pattern(regexp = "^PRJ_\\d{3}$") String pid) {
        try {
            Project1DTO project = sqsService.getProjectById(pid);
            if (project == null) {
                return new ResponseEntity<>("Project not found", HttpStatus.NOT_FOUND);
            }

            boolean updated = sqsService.assignQsToProject(pid, eid);
            if (!updated) {
                return new ResponseEntity<>("Failed to assign QS to project", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return ResponseEntity.ok("QS assigned to project successfully.");
        } catch (Exception e) {
            return new ResponseEntity<>("Error assigning QS: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Endpoint to get projects where qs_id is null or empty, returning project name, category, and client_id.
     */
    @RequestMapping(value = "/projects_without_qs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProjectsWithoutQs() {
        try {
            java.util.List<com.structurax.root.structurax.root.dao.Impl.SQSDAOImpl.ProjectInfo> projects = sqsService.getProjectsWithoutQs();
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching projects: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Endpoint to get all QS Officers from the employee table.
     */
    @RequestMapping(value = "/get_qs_officers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQSOfficers() {
        try {
            java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> officers = sqsService.getQSOfficers();
            return ResponseEntity.ok(officers);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching QS Officers: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // BOQ Management Endpoints for SQS

    /**
     * Get all BOQs in the system with their items
     */
    @GetMapping(value = "/boqs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllBOQs() {
        try {
            List<BOQWithProjectDTO> boqs = sqsService.getAllBOQsWithProjectInfo();
            return ResponseEntity.ok(boqs);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching BOQs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a specific BOQ by ID with its items
     */
    @GetMapping(value = "/boqs/{boqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBOQById(@PathVariable String boqId) {
        try {
            BOQWithItemsDTO boq = sqsService.getBOQWithItemsById(boqId);
            if (boq != null) {
                return ResponseEntity.ok(boq);
            } else {
                return new ResponseEntity<>("BOQ not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching BOQ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update BOQ with items (SQS can edit any BOQ)
     */
    @PutMapping(value = "/boqs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBOQWithItems(@RequestBody BOQWithItemsDTO boqWithItems) {
        try {
            boolean updated = sqsService.updateBOQWithItems(boqWithItems);
            if (updated) {
                return ResponseEntity.ok("BOQ updated successfully");
            } else {
                return new ResponseEntity<>("Failed to update BOQ", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating BOQ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update BOQ status (approve/reject)
     */
    @PutMapping(value = "/boqs/{boqId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBOQStatus(@PathVariable String boqId, @RequestParam String status) {
        try {
            BOQDTO.Status boqStatus;
            try {
                boqStatus = BOQDTO.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Invalid status. Valid values are: DRAFT, APPROVED, FINAL", HttpStatus.BAD_REQUEST);
            }
            
            boolean updated = sqsService.updateBOQStatus(boqId, boqStatus);
            if (updated) {
                return ResponseEntity.ok("BOQ status updated successfully to " + status.toUpperCase());
            } else {
                return new ResponseEntity<>("Failed to update BOQ status", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating BOQ status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
