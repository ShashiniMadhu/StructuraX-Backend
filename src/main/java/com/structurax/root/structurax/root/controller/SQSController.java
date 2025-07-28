package com.structurax.root.structurax.root.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.service.SQSService;

import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin("http://localhost:5173/")
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
}
