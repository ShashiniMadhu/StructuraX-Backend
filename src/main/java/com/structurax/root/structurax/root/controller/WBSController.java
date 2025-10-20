package com.structurax.root.structurax.root.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.WBSDTO;
import com.structurax.root.structurax.root.service.WBSService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/wbs")
public class WBSController {
    
    @Autowired
    private WBSService wbsService;
    
    /**
     * Create a new WBS task
     * POST /wbs/create
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createWBSTask(@RequestBody WBSDTO wbs) {
        try {
            // Validate WBS data
            if (wbs == null) {
                return new ResponseEntity<>("WBS data is missing", HttpStatus.BAD_REQUEST);
            }
            
            if (wbs.getProjectId() == null || wbs.getProjectId().trim().isEmpty()) {
                return new ResponseEntity<>("Project ID is required", HttpStatus.BAD_REQUEST);
            }
            
            if (wbs.getName() == null || wbs.getName().trim().isEmpty()) {
                return new ResponseEntity<>("Task name is required", HttpStatus.BAD_REQUEST);
            }
            
            if (wbs.getStatus() == null || wbs.getStatus().trim().isEmpty()) {
                return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
            }
            
            int taskId = wbsService.createWBSTask(wbs);
            return ResponseEntity.ok("WBS task created successfully with ID: " + taskId);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create WBS task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Bulk create multiple WBS tasks at once
     * POST /wbs/bulk-create
     */
    @PostMapping(value = "/bulk-create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBulkWBSTasks(@RequestBody List<WBSDTO> wbsTasks) {
        try {
            // Validate WBS tasks list
            if (wbsTasks == null || wbsTasks.isEmpty()) {
                return new ResponseEntity<>("WBS tasks list cannot be null or empty", HttpStatus.BAD_REQUEST);
            }
            
            // Validate each task
            for (int i = 0; i < wbsTasks.size(); i++) {
                WBSDTO wbs = wbsTasks.get(i);
                if (wbs.getProjectId() == null || wbs.getProjectId().trim().isEmpty()) {
                    return new ResponseEntity<>("Project ID is required for task at index " + i, HttpStatus.BAD_REQUEST);
                }
                if (wbs.getName() == null || wbs.getName().trim().isEmpty()) {
                    return new ResponseEntity<>("Task name is required for task at index " + i, HttpStatus.BAD_REQUEST);
                }
                if (wbs.getStatus() == null || wbs.getStatus().trim().isEmpty()) {
                    return new ResponseEntity<>("Status is required for task at index " + i, HttpStatus.BAD_REQUEST);
                }
            }
            
            List<Integer> taskIds = wbsService.createBulkWBSTasks(wbsTasks);
            
            // Create response with task IDs
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "WBS tasks created successfully");
            response.put("count", taskIds.size());
            response.put("taskIds", taskIds);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create bulk WBS tasks: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get all WBS tasks for a project
     * GET /wbs/project/{projectId}
     */
    @GetMapping(value = "/project/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWBSByProjectId(@PathVariable String projectId) {
        try {
            List<WBSDTO> wbsTasks = wbsService.getWBSByProjectId(projectId);
            return ResponseEntity.ok(wbsTasks);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch WBS tasks: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get a single WBS task by task ID
     * GET /wbs/{taskId}
     */
    @GetMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWBSByTaskId(@PathVariable int taskId) {
        try {
            WBSDTO wbs = wbsService.getWBSByTaskId(taskId);
            if (wbs != null) {
                return ResponseEntity.ok(wbs);
            } else {
                return new ResponseEntity<>("WBS task not found", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch WBS task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get child tasks of a parent task
     * GET /wbs/children/{parentId}
     */
    @GetMapping(value = "/children/{parentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getChildTasks(@PathVariable int parentId) {
        try {
            List<WBSDTO> childTasks = wbsService.getChildTasks(parentId);
            return ResponseEntity.ok(childTasks);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to fetch child tasks: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update a WBS task (all fields)
     * PUT /wbs/update
     */
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateWBSTask(@RequestBody WBSDTO wbs) {
        try {
            // Validate WBS data
            if (wbs == null) {
                return new ResponseEntity<>("WBS data is missing", HttpStatus.BAD_REQUEST);
            }
            
            if (wbs.getTaskId() <= 0) {
                return new ResponseEntity<>("Task ID is required for update", HttpStatus.BAD_REQUEST);
            }
            
            if (wbs.getProjectId() == null || wbs.getProjectId().trim().isEmpty()) {
                return new ResponseEntity<>("Project ID is required", HttpStatus.BAD_REQUEST);
            }
            
            if (wbs.getName() == null || wbs.getName().trim().isEmpty()) {
                return new ResponseEntity<>("Task name is required", HttpStatus.BAD_REQUEST);
            }
            
            if (wbs.getStatus() == null || wbs.getStatus().trim().isEmpty()) {
                return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
            }
            
            boolean updated = wbsService.updateWBSTask(wbs);
            
            if (updated) {
                return ResponseEntity.ok("WBS task updated successfully");
            } else {
                return new ResponseEntity<>("Failed to update WBS task", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update WBS task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Update only the milestone flag of a WBS task
     * PATCH /wbs/{taskId}/milestone
     */
    @PatchMapping(value = "/{taskId}/milestone")
    public ResponseEntity<String> updateWBSMilestone(
            @PathVariable int taskId, 
            @RequestParam boolean milestone) {
        try {
            boolean updated = wbsService.updateWBSMilestone(taskId, milestone);
            
            if (updated) {
                return ResponseEntity.ok("WBS milestone updated successfully");
            } else {
                return new ResponseEntity<>("Failed to update WBS milestone", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update WBS milestone: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete a single WBS task by task ID
     * DELETE /wbs/{taskId}
     */
    @DeleteMapping(value = "/{taskId}")
    public ResponseEntity<String> deleteWBSTask(@PathVariable int taskId) {
        try {
            boolean deleted = wbsService.deleteWBSTask(taskId);
            
            if (deleted) {
                return ResponseEntity.ok("WBS task deleted successfully");
            } else {
                return new ResponseEntity<>("WBS task not found or already deleted", HttpStatus.NOT_FOUND);
            }
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete WBS task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete complete WBS for a project (all tasks)
     * DELETE /wbs/project/{projectId}
     */
    @DeleteMapping(value = "/project/{projectId}")
    public ResponseEntity<String> deleteCompleteWBS(@PathVariable String projectId) {
        try {
            int deletedCount = wbsService.deleteCompleteWBS(projectId);
            
            if (deletedCount > 0) {
                return ResponseEntity.ok("Complete WBS deleted successfully. " + deletedCount + " task(s) deleted.");
            } else {
                return new ResponseEntity<>("No WBS tasks found for this project", HttpStatus.NOT_FOUND);
            }
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete complete WBS: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
