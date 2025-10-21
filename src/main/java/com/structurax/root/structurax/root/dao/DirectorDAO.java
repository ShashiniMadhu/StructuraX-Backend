package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.sql.SQLException;
import java.util.List;

public interface DirectorDAO {
    ClientOneDTO createClient (ClientOneDTO clientOneDTO, String otp);
    List<ClientWithPlaneDTO> getClientWithPlan();

    List<ClientWithPlaneDTO> getClientWithoutPlan();
    ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO);
    List<ProjectInitiateDTO> getAllProjects();

    ProjectInitiateDTO getProjectById(String id);


    List<ProjectInitiateDTO> getPendingProjects();

    void startProject(String projectId, ProjectStartDTO projectStartDTO) throws SQLException;
    List<AllProjectDocumentDTO> getAllDocumentsById(String projectId);
    List<GetEmployeeDTO> getAllEmployees() throws SQLException;
    EmployeeByIdDTO getEmployeeById(String empid);

    double calculateProjectProgress(String projectId);
    List<CatalogDTO> getInventory() throws SQLException;

    void addInventoryItem(AddinventoryDTO addinventoryDTO);
}
