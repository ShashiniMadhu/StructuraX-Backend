package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public interface DirectorService {

    ClientOneDTO createClient(ClientOneDTO clientOneDTO);
    List<ClientWithPlaneDTO> getClientWithPlan();
    List<ClientWithPlaneDTO> getClientWithoutPlan();
    ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO);
    List<ProjectInitiateDTO> getAllProjects();
    ProjectInitiateDTO getProjectById(String id);
    List<ProjectInitiateDTO> getPendingProjects();
    void startProject(String projectId, ProjectStartDTO projectStartDTO) throws SQLException;
    List<AllProjectDocumentDTO> getAllDocumentsByProjectId(String projectId);
    List<GetEmployeeDTO> getAllEmployee() throws SQLException;
    EmployeeByIdDTO getEmployeeById(String empid);
    double calculateProjectProgress(String projectId);

}
