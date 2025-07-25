package com.structurax.root.structurax.root.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientAndBOQDTO;
import com.structurax.root.structurax.root.service.QSService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/qs")
public class QSController {

    @Autowired
    private QSService qsService;

    /**
     * Endpoint to get projects for the given QS id.
     */
    @GetMapping("/projects/{qsId}")
    public List<Project1DTO> getProjectsForQS(@PathVariable String qsId) {
        return qsService.getProjectsByQSId(qsId);
    }
    
    /**
     * Endpoint to get projects with client and BOQ data for the given QS id.
     */
    @GetMapping("/projects-with-data/{qsId}")
    public List<ProjectWithClientAndBOQDTO> getProjectsWithDataForQS(@PathVariable String qsId) {
        return qsService.getProjectsWithClientAndBOQByQSId(qsId);
    }
}
