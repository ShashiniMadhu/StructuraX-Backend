package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.WBSDTO;
import com.structurax.root.structurax.root.service.WBSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project-owner/wbs")
public class ProjectOwnerWBSController {
    

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerWBSController.class);
    private final WBSService wbsService;

    public ProjectOwnerWBSController(WBSService wbsService) {
        this.wbsService = wbsService;
    }

    /**
     * Get all WBS tasks for a specific project
     * GET /api/project-owner/wbs/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getWBSByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching WBS tasks for project_id={}", projectId);
            List<WBSDTO> wbsTasks = wbsService.getWBSByProjectId(projectId);
            response.put("success", true);
            response.put("wbsTasks", wbsTasks);
            response.put("totalCount", wbsTasks.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching WBS tasks: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching WBS tasks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get a single WBS task by task ID
     * GET /api/project-owner/wbs/{taskId}
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> getWBSByTaskId(@PathVariable Integer taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching WBS task by task_id={}", taskId);
            WBSDTO wbsTask = wbsService.getWBSByTaskId(taskId);

            if (wbsTask != null) {
                response.put("success", true);
                response.put("wbsTask", wbsTask);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "WBS task not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error fetching WBS task: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching WBS task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get root level WBS tasks for a project (tasks with no parent)
     * GET /api/project-owner/wbs/project/{projectId}/root
     */
    @GetMapping("/project/{projectId}/root")
    public ResponseEntity<Map<String, Object>> getRootWBSTasks(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching root WBS tasks for project_id={}", projectId);
            List<WBSDTO> wbsTasks = wbsService.getRootWBSTasks(projectId);
            response.put("success", true);
            response.put("wbsTasks", wbsTasks);
            response.put("totalCount", wbsTasks.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching root WBS tasks: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching root WBS tasks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get child WBS tasks for a parent task
     * GET /api/project-owner/wbs/parent/{parentId}
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<Map<String, Object>> getWBSByParent(@PathVariable Integer parentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching child WBS tasks for parent_id={}", parentId);
            List<WBSDTO> wbsTasks = wbsService.getWBSByParentId(parentId);
            response.put("success", true);
            response.put("wbsTasks", wbsTasks);
            response.put("totalCount", wbsTasks.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching child WBS tasks: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching child WBS tasks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Create a new WBS task
     * POST /api/project-owner/wbs
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createWBS(@RequestBody WBSDTO wbsDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Creating new WBS task for project_id={}", wbsDTO.getProjectId());
            WBSDTO createdTask = wbsService.createWBS(wbsDTO);

            response.put("success", true);
            response.put("message", "WBS task created successfully");
            response.put("wbsTask", createdTask);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error creating WBS task: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating WBS task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update WBS task
     * PUT /api/project-owner/wbs/{taskId}
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> updateWBS(
            @PathVariable Integer taskId,
            @RequestBody WBSDTO wbsDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating WBS task with task_id={}", taskId);
            wbsDTO.setTaskId(taskId);
            wbsService.updateWBS(wbsDTO);

            response.put("success", true);
            response.put("message", "WBS task updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating WBS task: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating WBS task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a WBS task
     * DELETE /api/project-owner/wbs/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Map<String, Object>> deleteWBS(@PathVariable Integer taskId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Deleting WBS task with task_id={}", taskId);
            wbsService.deleteWBS(taskId);

            response.put("success", true);
            response.put("message", "WBS task deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting WBS task: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error deleting WBS task: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
