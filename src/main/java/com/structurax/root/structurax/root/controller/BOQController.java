package com.structurax.root.structurax.root.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;
import com.structurax.root.structurax.root.service.BOQService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/boq")
public class BOQController {
    @Autowired
    private BOQService boqService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBOQWithItems(@RequestBody com.structurax.root.structurax.root.dto.BOQWithItemsDTO boqWithItems) {
        try {
            // Validate BOQ data
            if (boqWithItems == null || boqWithItems.getBoq() == null) {
                return new ResponseEntity<>("BOQ data is missing", HttpStatus.BAD_REQUEST);
            }
            
            BOQDTO boq = boqWithItems.getBoq();
            
            // Validate required fields
            if (boq.getProjectId() == null || boq.getProjectId().trim().isEmpty()) {
                return new ResponseEntity<>("Project ID is required", HttpStatus.BAD_REQUEST);
            }
            
            if (boq.getQsId() == null || boq.getQsId().trim().isEmpty()) {
                return new ResponseEntity<>("QS ID is required (can be QS Officer or Senior QS Officer ID)", HttpStatus.BAD_REQUEST);
            }
            
            if (boq.getDate() == null) {
                return new ResponseEntity<>("Date is required", HttpStatus.BAD_REQUEST);
            }
            
            if (boq.getStatus() == null) {
                return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
            }
            
            // Create BOQ
            String boqId = boqService.createBOQ(boq);
            
            if (boqId != null) {
                // Add items if provided
                if (boqWithItems.getItems() != null && !boqWithItems.getItems().isEmpty()) {
                    for (com.structurax.root.structurax.root.dto.BOQitemDTO item : boqWithItems.getItems()) {
                        item.setBoqId(boqId);
                        boqService.addBOQItem(item);
                    }
                }
                return ResponseEntity.ok(boqId);
            } else {
                return new ResponseEntity<>("Failed to create BOQ", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save BOQ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/add_item", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addBOQItem(@RequestBody BOQitemDTO item) {
        try {
            boqService.addBOQItem(item);
            return ResponseEntity.ok("BOQ item added successfully");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add BOQ item: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/get/{boqId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BOQDTO> getBOQ(@PathVariable String boqId) {
        BOQDTO boq = boqService.getBOQById(boqId);
        if (boq != null) {
            return ResponseEntity.ok(boq);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/project/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBOQByProjectId(@PathVariable String projectId) {
        try {
            com.structurax.root.structurax.root.dto.BOQWithItemsDTO boqWithItems = boqService.getBOQWithItemsByProjectId(projectId);
            if (boqWithItems != null) {
                return ResponseEntity.ok(boqWithItems);
            } else {
                return new ResponseEntity<>("No BOQ found for project ID: " + projectId, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to retrieve BOQ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{boqId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BOQitemDTO>> getBOQItems(@PathVariable String boqId) {
        List<BOQitemDTO> items = boqService.getBOQItemsByBOQId(boqId);
        return ResponseEntity.ok(items);
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateBOQWithItems(@RequestBody com.structurax.root.structurax.root.dto.BOQWithItemsDTO boqWithItems) {
        try {
            // Validate BOQ data
            if (boqWithItems == null || boqWithItems.getBoq() == null) {
                return new ResponseEntity<>("BOQ data is missing", HttpStatus.BAD_REQUEST);
            }
            
            BOQDTO boq = boqWithItems.getBoq();
            
            // Validate required fields
            if (boq.getBoqId() == null || boq.getBoqId().trim().isEmpty()) {
                return new ResponseEntity<>("BOQ ID is required for update", HttpStatus.BAD_REQUEST);
            }
            
            if (boq.getProjectId() == null || boq.getProjectId().trim().isEmpty()) {
                return new ResponseEntity<>("Project ID is required", HttpStatus.BAD_REQUEST);
            }
            
            if (boq.getQsId() == null || boq.getQsId().trim().isEmpty()) {
                return new ResponseEntity<>("QS ID is required (can be QS Officer or Senior QS Officer ID)", HttpStatus.BAD_REQUEST);
            }
            
            if (boq.getDate() == null) {
                return new ResponseEntity<>("Date is required", HttpStatus.BAD_REQUEST);
            }
            
            if (boq.getStatus() == null) {
                return new ResponseEntity<>("Status is required", HttpStatus.BAD_REQUEST);
            }
            
            boolean updated = boqService.updateBOQWithItems(boqWithItems);
            
            if (updated) {
                return ResponseEntity.ok("BOQ updated successfully");
            } else {
                return new ResponseEntity<>("Failed to update BOQ", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update BOQ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
