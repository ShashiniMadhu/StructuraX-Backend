package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.ProjectInitiateDTO;
import com.structurax.root.structurax.root.dto.ProjectStartDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectorService {

    ClientDTO createClient(ClientDTO clientDTO);
    List<ClientDTO> getClientWithPlan();
    List<ClientDTO> getClientWithoutPlan();
    ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO);
    List<ProjectInitiateDTO> getAllProjects();
    ProjectInitiateDTO getProjectById(String id);
    List<ProjectInitiateDTO> getPendingProjects();
    void startProject(String projectId, ProjectStartDTO projectStartDTO);

}
