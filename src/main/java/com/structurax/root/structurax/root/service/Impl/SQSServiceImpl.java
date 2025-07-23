package com.structurax.root.structurax.root.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.SQSDAO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.service.SQSService;

@Service
public class SQSServiceImpl implements SQSService {

    @Autowired
    private SQSDAO sqsDAO;

    @Override
    public Project1DTO getProjectById(String id) {
        return sqsDAO.getProjectById(id);
    }

    @Override
    public boolean assignQsToProject(String pid, String eid) {
        return sqsDAO.assignQsToProject(pid, eid);
    }

    @Override
    public java.util.List<com.structurax.root.structurax.root.dao.Impl.SQSDAOImpl.ProjectInfo> getProjectsWithoutQs() {
        return sqsDAO.getProjectsWithoutQs();
    }

    @Override
    public java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> getQSOfficers() {
        return sqsDAO.getQSOfficers();
    }
}
