package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.QSProjectDAO;
import com.structurax.root.structurax.root.dto.DailyUpdateDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientDTO;
import com.structurax.root.structurax.root.dto.SiteVisitWithParticipantsDTO;
import com.structurax.root.structurax.root.service.QSProjectService;

@Service
public class QSProjectServiceImpl implements QSProjectService {
    
    @Autowired
    private QSProjectDAO qsProjectDAO;
    
    @Override
    public List<ProjectWithClientDTO> getAllProjects() {
        return qsProjectDAO.getAllProjects();
    }
    
    @Override
    public List<ProjectWithClientDTO> getProjectsByEmployeeId(String employeeId) {
        return qsProjectDAO.getProjectsByEmployeeId(employeeId);
    }
    
    @Override
    public List<DesignDTO> getDesignFilesByProjectId(String projectId) {
        return qsProjectDAO.getDesignFilesByProjectId(projectId);
    }
    
    @Override
    public List<DailyUpdateDTO> getDailyUpdatesByProjectId(String projectId) {
        return qsProjectDAO.getDailyUpdatesByProjectId(projectId);
    }
    
    @Override
    public List<SiteVisitWithParticipantsDTO> getSiteVisitsByProjectId(String projectId) {
        return qsProjectDAO.getSiteVisitsByProjectId(projectId);
    }
}
