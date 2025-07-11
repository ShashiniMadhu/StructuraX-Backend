package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.*;
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


    /*@GetMapping
    public ResponseEntity<List<LaborAttendanceDTO>> getAttendanceByProjectIdAndDate(
            @RequestParam("projectId") Integer projectId,
            @RequestParam("date") String dateStr // accept date as yyyy-MM-dd
    ) {
        java.sql.Date date = java.sql.Date.valueOf(dateStr);
        List<LaborAttendanceDTO> attendanceList = siteSupervisorService.getAttendanceByProjectIdAndDate(projectId, date);
        return ResponseEntity.ok(attendanceList);
    }*/




    @GetMapping()
    public ResponseEntity<List<LaborAttendanceDTO>> getAllLaborAttendance(){
        List<LaborAttendanceDTO> laborAttendance = siteSupervisorService.getAllLaborAttendance();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(laborAttendance, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<LaborAttendanceDTO> updateLaborAttendance(@PathVariable Integer id,@RequestBody LaborAttendanceDTO laborAttendanceDTO){
        laborAttendanceDTO.setId(id);
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


    //material request
    @GetMapping("/materials/{id}")
    public ResponseEntity<List<MaterialDTO>> getMaterialsByRequestId(@PathVariable Integer id){
        List<MaterialDTO> materials = siteSupervisorService.getMaterialsByRequestId(id);
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    @GetMapping("/material_requests")
    public ResponseEntity<List<RequestDTO>> getAllMaterialRequests(){
        List<RequestDTO> requestDTOS = siteSupervisorService.getAllMaterialRequests();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

    @GetMapping("/tool_requests")
    public ResponseEntity<List<RequestDTO>> getAllToolRequests(){
        List<RequestDTO> requestDTOS = siteSupervisorService.getAllToolRequests();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

    /*@GetMapping("/requests/{id}")
    public ResponseEntity<RequestDTO> getRequestById(@PathVariable Integer id){
        RequestDTO request = siteSupervisorService.getRequestById(id);
        return new ResponseEntity<>(request,HttpStatus.OK);
    }*/

    @PostMapping("/material_request")
    public ResponseEntity<RequestDTO> createMaterialRequest(@RequestBody RequestDTO requestDTO){
        siteSupervisorService.createMaterialRequest(requestDTO);
        ///System.out.println("Received projectId: " + paymentPlanDTO.getProjectId());
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }






}
