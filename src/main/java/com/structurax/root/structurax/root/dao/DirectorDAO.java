package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface DirectorDAO {
    ClientDTO createClient (ClientDTO clientDTO, String otp);
    List<ClientDTO> getClientWithPlan();

    List<ClientDTO> getClientWithoutPlan();
    ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO);
    List<ProjectInitiateDTO> getAllProjects();

    ProjectInitiateDTO getProjectById(String id);


    List<ProjectInitiateDTO> getPendingProjects();

    void startProject(String projectId, ProjectStartDTO projectStartDTO);


}
