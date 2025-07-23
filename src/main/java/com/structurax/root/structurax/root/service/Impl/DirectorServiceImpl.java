package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.DirectorDAO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.ProjectInitiateDTO;
import com.structurax.root.structurax.root.dto.ProjectStartDTO;
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
    public ClientDTO createClient(ClientDTO clientDTO) {
        String otp = OtpUtil.generateOtp();

        ClientDTO createdClient = directorDAO.createClient(clientDTO,otp);

        mailService.sendClientRegisterOtp(clientDTO.getEmail(),clientDTO.getFirstName(),otp);

        return createdClient;
    }

    @Override
    public List<ClientDTO> getClientWithPlan() {
        List<ClientDTO> clients = directorDAO.getClientWithPlan();
        return clients;
    }

    @Override
    public List<ClientDTO> getClientWithoutPlan() {
        List<ClientDTO> clients = directorDAO.getClientWithoutPlan();
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


}
