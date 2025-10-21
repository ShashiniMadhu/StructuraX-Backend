package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.DirectorDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.DirectorService;
import com.structurax.root.structurax.root.service.MailService;
import com.structurax.root.structurax.root.util.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class DirectorServiceImpl implements DirectorService {

    @Autowired
    private DirectorDAO directorDAO;

    @Autowired
    private MailService mailService;

    @Override
    public ClientOneDTO createClient(ClientOneDTO clientOneDTO) {
        String otp = OtpUtil.generateOtp();

        ClientOneDTO createdClient = directorDAO.createClient(clientOneDTO,otp);

        mailService.sendClientRegisterOtp(clientOneDTO.getEmail(),clientOneDTO.getName(),otp);

        return createdClient;
    }

    @Override
    public List<ClientWithPlaneDTO> getClientWithPlan() {
        List<ClientWithPlaneDTO> clients = directorDAO.getClientWithPlan();
        return clients;
    }

    @Override
    public List<ClientWithPlaneDTO> getClientWithoutPlan() {
        List<ClientWithPlaneDTO> clients = directorDAO.getClientWithoutPlan();
        return clients;
    }

    @Override
    public ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO) {
        ProjectInitiateDTO  initializedProject = directorDAO.initializeProject(projectInitiateDTO);
        return initializedProject;
    }

    @Override
    public List<ProjectInitiateDTO> getAllProjects() {
        List<ProjectInitiateDTO> projects = directorDAO.getAllProjects();
        return projects;
    }

    @Override
    public ProjectInitiateDTO getProjectById(String id) {
        ProjectInitiateDTO project = directorDAO.getProjectById(id);
        return project;
    }

    @Override
    public List<ProjectInitiateDTO> getPendingProjects() {
        List<ProjectInitiateDTO> projects = directorDAO.getPendingProjects();
        return projects;
    }

    @Override
    public void startProject(String projectId, ProjectStartDTO projectStartDTO) throws SQLException {
        directorDAO.startProject(projectId, projectStartDTO);
    }

    @Override
    public List<AllProjectDocumentDTO> getAllDocumentsByProjectId(String projectId) {
        List<AllProjectDocumentDTO> documents = directorDAO.getAllDocumentsById(projectId);
        return documents;
    }

    @Override
    public List<GetEmployeeDTO> getAllEmployee() throws SQLException {
        return directorDAO.getAllEmployees();
    }

    @Override
    public EmployeeByIdDTO getEmployeeById(String empid) {
        return directorDAO.getEmployeeById(empid);
    }

    @Override
    public double calculateProjectProgress(String projectId) {
        return directorDAO.calculateProjectProgress(projectId);
    }

    @Override
    public List<CatalogDTO> getInventory() throws SQLException {
        return directorDAO.getInventory();
    }

    @Override
    public void addInventoryItem(AddinventoryDTO addinventoryDTO) {
        directorDAO.addInventoryItem(addinventoryDTO);
    }


}
