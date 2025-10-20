package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.SiteSupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/projects/{id}")
    public ResponseEntity<List<ProjectDTO>> getProjectsBySsId(@PathVariable String id){
        List<ProjectDTO> projects = siteSupervisorService.getProjectsBySsId(id);
        return new ResponseEntity<>(projects, HttpStatus.OK);
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
            @RequestParam("projectId") String projectId,
            @RequestParam("date") String dateStr) {

        java.sql.Date date = java.sql.Date.valueOf(dateStr); // Converts string to java.sql.Date
        List<LaborAttendanceDTO> deleted = siteSupervisorService.deleteLaborAttendanceRecord(projectId, date);
        return ResponseEntity.ok(deleted);
    }


    //material and tool (site resources) request--------------------------------------------------
    @GetMapping("/materials/{id}")
    public ResponseEntity<List<SiteResourceDTO>> getMaterialsByRequestId(@PathVariable Integer id){
        List<SiteResourceDTO> materials = siteSupervisorService.getMaterialsByRequestId(id);
        return new ResponseEntity<>(materials, HttpStatus.OK);
    }

    @GetMapping("/material_requests")
    public ResponseEntity<List<RequestSiteResourcesDTO>> getAllMaterialRequests(){
        List<RequestSiteResourcesDTO> requestDTOS = siteSupervisorService.getAllMaterialRequests();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

    @GetMapping("/tool_requests")
    public ResponseEntity<List<RequestSiteResourcesDTO>> getAllToolRequests(){
        List<RequestSiteResourcesDTO> requestDTOS = siteSupervisorService.getAllToolRequests();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

    @GetMapping("/labor_requests")
    public ResponseEntity<List<RequestSiteResourcesDTO>> getAllLaborRequests(){
        List<RequestSiteResourcesDTO> requestDTOS = siteSupervisorService.getAllLaborRequests();
        System.out.println("Endpoint hit");
        return new ResponseEntity<>(requestDTOS, HttpStatus.OK);
    }

    /*@GetMapping("/requests/{id}")
    public ResponseEntity<RequestDTO> getRequestById(@PathVariable Integer id){
        RequestDTO request = siteSupervisorService.getRequestById(id);
        return new ResponseEntity<>(request,HttpStatus.OK);
    }*/

    @PostMapping("/material_request")
    public ResponseEntity<RequestSiteResourcesDTO> createMaterialRequest(@RequestBody RequestSiteResourcesDTO requestDTO){
        siteSupervisorService.createMaterialRequest(requestDTO);
        ///System.out.println("Received projectId: " + paymentPlanDTO.getProjectId());
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);
    }


    //to-do creation----------------------------------------------------------------------
    @PostMapping("/to_do")
    public ResponseEntity<TodoDTO> createToDo(@RequestBody TodoDTO todoDTO){
        siteSupervisorService.createToDo(todoDTO);
        ///System.out.println("Received projectId: " + paymentPlanDTO.getProjectId());
        return new ResponseEntity<>(todoDTO, HttpStatus.OK);
    }

    @GetMapping("/todo/sp/{id}")
    public ResponseEntity<List<TodoDTO>> getToDoBySpId(@PathVariable String id){
        List<TodoDTO> todos = siteSupervisorService.getToDoBySpId(id);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @PutMapping("/todo/update")
    public ResponseEntity<String> updateTodo(@RequestBody TodoDTO todoDTO) {
        boolean updated = siteSupervisorService.updateTodo(todoDTO);

        if (updated) {
            return ResponseEntity.ok("Task updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
    }

    @DeleteMapping("/todo/{id}")
    public ResponseEntity<String> deleteTodoTask(@PathVariable("id") int taskId) {
        boolean deleted = siteSupervisorService.deleteToDoTask(taskId);

        if (deleted) {
            return ResponseEntity.ok("Task deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
    }

    @PutMapping("request/update")
    public ResponseEntity<RequestSiteResourcesDTO> updateRequest(@RequestBody RequestSiteResourcesDTO requestSiteResourcesDTO){
        siteSupervisorService.updateRequest(requestSiteResourcesDTO);
        return new ResponseEntity<>(requestSiteResourcesDTO,HttpStatus.OK);
    }

    @DeleteMapping("/request/{requestId}")
    public ResponseEntity<RequestSiteResourcesDTO> deleteRequest(@PathVariable int requestId){
        RequestSiteResourcesDTO request = siteSupervisorService.deleteRequest(requestId);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PostMapping("/daily_updates")
    public ResponseEntity<DailyUpdatesDTO> insertDailyUpdates(@RequestBody DailyUpdatesDTO dailyUpdatesDTO){
        siteSupervisorService.insertDailyUpdates(dailyUpdatesDTO);
        ///System.out.println("Received projectId: " + paymentPlanDTO.getProjectId());
        return new ResponseEntity<>(dailyUpdatesDTO, HttpStatus.OK);
    }

    @GetMapping("/daily_updates/{ssId}")
    public ResponseEntity<List<DailyUpdatesDTO>> getAllDailyUpdatesBySsId(@PathVariable String ssId){
        List<DailyUpdatesDTO> updatesDTOS = siteSupervisorService.getAllDailyUpdatesBySsId(ssId);
        return new ResponseEntity<>(updatesDTOS, HttpStatus.OK);
    }

    @PutMapping("daily_updates")
    public ResponseEntity<DailyUpdatesDTO> updateDailyUpdates(@RequestBody DailyUpdatesDTO dailyUpdatesDTO){
        siteSupervisorService.updateDailyUpdates(dailyUpdatesDTO);
        return new ResponseEntity<>(dailyUpdatesDTO,HttpStatus.OK);
    }

    @DeleteMapping("/daily_updates/{updateId}")
    public ResponseEntity<String> deleteDailyUpdate(@PathVariable int updateId) {
        boolean deleted = siteSupervisorService.deleteDailyUpdate(updateId);
        if (deleted) {
            return ResponseEntity.ok("Deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Update not found");
        }
    }

    @PutMapping("/wbs")
    public ResponseEntity<WBSDTO> updateWbsStatus(@RequestBody WBSDTO wbsdto){
        siteSupervisorService.updateWbsStatus(wbsdto);
        return new ResponseEntity<>(wbsdto,HttpStatus.OK);
    }

    @GetMapping("/wbs/{ssId}")
    public ResponseEntity<List<WBSDTO>> getAllWbsBySsId(@PathVariable String ssId){
        List<WBSDTO> wbsdtos = siteSupervisorService.getAllWbsBySsId(ssId);
        return new ResponseEntity<>(wbsdtos, HttpStatus.OK);
    }

    @PostMapping("/petty_cash_record")
    public ResponseEntity<PettyCashRecordDTO> insertPettyCashRecord(@RequestBody PettyCashRecordDTO recordDTO){
        siteSupervisorService.insertPettyCashRecord(recordDTO);
        return new ResponseEntity<>(recordDTO, HttpStatus.OK);
    }


    @GetMapping("/petty_cash/{ssId}")
    public ResponseEntity<List<PettyCashDTO>> getAllPettyCashBySsId(@PathVariable String ssId){
        List<PettyCashDTO> pettyCashDTOList = siteSupervisorService.getPettyCashBySsId(ssId);
        return new ResponseEntity<>(pettyCashDTOList, HttpStatus.OK);
    }


    @PutMapping("/petty_cash_record")
    public ResponseEntity<PettyCashRecordDTO> updatePettyCashRecord(@RequestBody PettyCashRecordDTO recordDTO){
        siteSupervisorService.updatePettyCashRecord(recordDTO);
        return new ResponseEntity<>(recordDTO,HttpStatus.OK);
    }

    @DeleteMapping("/petty_cash_record/{recordId}")
    public ResponseEntity<String> deletePettyCashRecord(@PathVariable int recordId) {
        boolean deleted = siteSupervisorService.deletePettyCashRecord(recordId);
        if (deleted) {
            return ResponseEntity.ok("Deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("record not found");
        }
    }

}