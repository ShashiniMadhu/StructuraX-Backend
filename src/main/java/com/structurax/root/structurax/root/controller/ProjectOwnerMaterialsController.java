package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.ProjectOwnerMaterialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-owner/materials")
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectOwnerMaterialsController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerMaterialsController.class);
    private final ProjectOwnerMaterialsService materialsService;

    public ProjectOwnerMaterialsController(ProjectOwnerMaterialsService materialsService) {
        this.materialsService = materialsService;
    }



    @GetMapping("/{projectId}")
    public ResponseEntity<Map<String, Object>> getMaterialsByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching materials for project_id={}", projectId);
            List<ProjectMaterialDTO> materials = materialsService.getMaterialsByProjectId(projectId);
            response.put("success", true);
            response.put("materials", materials);
            response.put("totalCount", materials.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching materials: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching materials: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{projectId}/summary")
    public ResponseEntity<Map<String, Object>> getMaterialSummary(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching material summary for project_id={}", projectId);
            MaterialSummaryDTO summary = materialsService.getMaterialSummary(projectId);
            response.put("success", true);
            response.put("summary", summary);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching material summary: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching material summary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== SITE VISIT ENDPOINTS ==========

    @PostMapping("/site-visits")
    public ResponseEntity<Map<String, Object>> createSiteVisit(@RequestBody SiteVisitDTO siteVisitDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Creating site visit for project_id={}", siteVisitDTO.getProjectId());
            SiteVisitDTO created = materialsService.createSiteVisitRequest(siteVisitDTO);
            response.put("success", true);
            response.put("siteVisit", created);
            response.put("message", "Site visit request submitted successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating site visit: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating site visit: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/site-visits/{projectId}")
    public ResponseEntity<Map<String, Object>> getSiteVisitsByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching site visits for project_id={}", projectId);
            List<SiteVisitDTO> siteVisits = materialsService.getSiteVisits(projectId);
            response.put("success", true);
            response.put("siteVisits", siteVisits);
            response.put("totalCount", siteVisits.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching site visits: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching site visits: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/site-visit/{visitId}")
    public ResponseEntity<Map<String, Object>> getSiteVisitById(@PathVariable Integer visitId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching site visit by visit_id={}", visitId);
            SiteVisitDTO siteVisit = materialsService.getSiteVisitById(visitId);
            if (siteVisit == null) {
                response.put("success", false);
                response.put("message", "Site visit not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("success", true);
            response.put("siteVisit", siteVisit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching site visit: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching site visit: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/site-visit/{visitId}/status")
    public ResponseEntity<Map<String, Object>> updateSiteVisitStatus(
            @PathVariable Integer visitId,
            @RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating site visit status for visit_id={} to {}", visitId, status);
            materialsService.updateSiteVisitStatus(visitId, status);
            response.put("success", true);
            response.put("message", "Site visit status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating site visit status: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating site visit status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}