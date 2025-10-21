package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.DirectorService;
import com.structurax.root.structurax.root.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/director")
@CrossOrigin("http://localhost:5173/")

public class
DirectorController {

    @Autowired
    private DirectorService directorService;

    @Autowired
    private MailService mailService;

    @PostMapping("/add_client")
    public String addClient(@RequestBody ClientOneDTO clientOneDTO) {
        directorService.createClient(clientOneDTO);
        return "Client registered successfully";

    }

    @GetMapping("/get_all_client_withplan")
    public List<ClientWithPlaneDTO> getClients() {
        return directorService.getClientWithPlan();
    }

    @GetMapping("/get_all_client_withoutplan")
    public List<ClientWithPlaneDTO> getClientWithoutPlan() {
        return directorService.getClientWithoutPlan();
    }

    @PostMapping("/initiate_project")
    public ResponseEntity<ProjectInitiateDTO> initiateProject(@RequestBody ProjectInitiateDTO projectInitiateDTO) {
        ProjectInitiateDTO savedProject= directorService.initializeProject(projectInitiateDTO);
        return new ResponseEntity<>(savedProject, HttpStatus.OK);
    }

    @GetMapping("get_all_projects")
    public List<ProjectInitiateDTO> getAllProjects() {
        return directorService.getAllProjects();
    }

    @GetMapping("get_project_by_id/{id}")
    public ResponseEntity<ProjectInitiateDTO> getProjectById(@PathVariable String id) {
        ProjectInitiateDTO project =  directorService.getProjectById(id);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("get_pending_projects")
    public List<ProjectInitiateDTO> getPendingProjects() {
        return directorService.getPendingProjects();
    }

    @PutMapping("/start_project/{projectId}")
    public ResponseEntity<String> startProject(
            @PathVariable String projectId,
            @RequestBody ProjectStartDTO projectStartDTO) throws SQLException {
        directorService.startProject(projectId, projectStartDTO);
        return new ResponseEntity<>("Project started successfully", HttpStatus.OK);
    }
    @GetMapping("/get_all_document_by_id/{id}")
    public List<AllProjectDocumentDTO> getAllProjectDocuments(@PathVariable String id) {
        return directorService.getAllDocumentsByProjectId(id);
    }

    @GetMapping("/get_all_employee")
    public List<GetEmployeeDTO> getAllEmployees() throws SQLException {
        return directorService.getAllEmployee();
    }

    @GetMapping("/{id}")
    public EmployeeByIdDTO getEmployee(@PathVariable String id) {
       return directorService.getEmployeeById(id);
    }

    @GetMapping("/{projectId}/progress")
    public ResponseEntity<Double> getProjectProgress(@PathVariable String projectId) {
        double progress = directorService.calculateProjectProgress(projectId);
        return ResponseEntity.ok(Math.round(progress * 10.0) / 10.0);
    }

    @GetMapping("/inventory")
    public List<CatalogDTO> getInventory() throws SQLException {
        return directorService.getInventory();
    }

    @PostMapping("/add_inventory")
    public String addInventory(@RequestBody AddinventoryDTO addinventoryDTO){
        directorService.addInventoryItem(addinventoryDTO);
        return "Inventory added successfully";

    }






}
