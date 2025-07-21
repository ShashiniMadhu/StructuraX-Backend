package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.ProjectInitiateDTO;
import com.structurax.root.structurax.root.dto.ProjectStartDTO;
import com.structurax.root.structurax.root.service.DirectorService;
import com.structurax.root.structurax.root.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/director")
@CrossOrigin("http://localhost:5173/")

public class DirectorController {

    @Autowired
    private DirectorService directorService;

    @Autowired
    private MailService mailService;

    @PostMapping("/add_client")
    public String addClient(@RequestBody ClientDTO clientDTO) {
        directorService.createClient( clientDTO);
        return "Client registered successfully";

    }

    @GetMapping("/get_all_client_withplan")
    public List<ClientDTO> getClients() {
        return directorService.getClientWithPlan();
    }

    @GetMapping("/get_all_client_withoutplan")
    public List<ClientDTO> getClientWithoutPlan() {
        return directorService.getClientWithoutPlan();
    }

    @PostMapping("/initiate_project")
    public String initiateProject(@RequestBody ProjectInitiateDTO projectInitiateDTO) {
        directorService.initializeProject(projectInitiateDTO);
        return "project initiated successfully";
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
            @RequestBody ProjectStartDTO projectStartDTO)
    {
        directorService.startProject(projectId, projectStartDTO);
        return new ResponseEntity<>("Project started successfully", HttpStatus.OK);
    }






}
