package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.LaborAttendanceDTO;
import com.structurax.root.structurax.root.dto.PaymentPlanDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.service.SiteSupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/site_supervisor")
public class SiteSupervisorController {

    @Autowired
    SiteSupervisorService siteSupervisorService;

    @PostMapping
    public ResponseEntity<List<LaborAttendanceDTO>> createLaborAttendanceBatch(
            @RequestBody List<LaborAttendanceDTO> laborAttendanceList) {
        List<LaborAttendanceDTO> savedAttendance = siteSupervisorService.createLaborAttendance(laborAttendanceList);
        return ResponseEntity.ok(savedAttendance);
    }





    @GetMapping()
    public ResponseEntity<List<LaborAttendanceDTO>> getAllLaborAttendance(){
        List<LaborAttendanceDTO> laborAttendance = siteSupervisorService.getAllLaborAttendance();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(laborAttendance, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<List<LaborAttendanceDTO>> updateLaborAttendance(@RequestBody List<LaborAttendanceDTO> laborAttendanceDTO){
        siteSupervisorService.updateLaborAttendance(laborAttendanceDTO);
        return new ResponseEntity<>(laborAttendanceDTO,HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<List<LaborAttendanceDTO>> deleteLaborAttendance(
            @RequestParam("projectId") Integer projectId,
            @RequestParam("date") String dateStr) {

        java.sql.Date date = java.sql.Date.valueOf(dateStr); // Converts string to java.sql.Date
        List<LaborAttendanceDTO> deleted = siteSupervisorService.deleteLaborAttendanceRecord(projectId, date);
        return ResponseEntity.ok(deleted);
    }





}
