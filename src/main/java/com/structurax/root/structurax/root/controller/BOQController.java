package com.structurax.root.structurax.root.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/boq")
public class BOQController {
    @Autowired
    private BOQService boqService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBOQWithItems(@RequestBody com.structurax.root.structurax.root.dto.BOQWithItemsDTO boqWithItems) {
        String boqId = boqService.createBOQ(boqWithItems.getBoq());
        if (boqId != null) {
            if (boqWithItems.getItems() != null) {
                for (com.structurax.root.structurax.root.dto.BOQitemDTO item : boqWithItems.getItems()) {
                    item.setBoqId(boqId);
                    boqService.addBOQItem(item);
                }
            }
            return ResponseEntity.ok(boqId);
        } else {
            return new ResponseEntity<>("Failed to create BOQ", HttpStatus.INTERNAL_SERVER_ERROR);
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

    @GetMapping(value = "/{boqId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BOQitemDTO>> getBOQItems(@PathVariable String boqId) {
        List<BOQitemDTO> items = boqService.getBOQItemsByBOQId(boqId);
        return ResponseEntity.ok(items);
    }

    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateBOQWithItems(@RequestBody com.structurax.root.structurax.root.dto.BOQWithItemsDTO boqWithItems) {
        boolean updated = boqService.updateBOQWithItems(boqWithItems);
        if (updated) {
            return ResponseEntity.ok("BOQ updated successfully");
        } else {
            return new ResponseEntity<>("Failed to update BOQ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
