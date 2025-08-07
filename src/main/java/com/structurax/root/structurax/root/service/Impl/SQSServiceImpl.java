package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.SQSDAO;
import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.service.BOQService;
import com.structurax.root.structurax.root.service.SQSService;

@Service
public class SQSServiceImpl implements SQSService {

    @Autowired
    private SQSDAO sqsDAO;
    
    @Autowired
    private BOQService boqService;

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
    
    // BOQ management methods implementation
    @Override
    public List<BOQWithItemsDTO> getAllBOQs() {
        return boqService.getAllBOQs();
    }
    
    @Override
    public List<com.structurax.root.structurax.root.dto.BOQWithProjectDTO> getAllBOQsWithProjectInfo() {
        return boqService.getAllBOQsWithProjectInfo();
    }
    
    @Override
    public boolean updateBOQWithItems(BOQWithItemsDTO boqWithItems) {
        return boqService.updateBOQWithItems(boqWithItems);
    }
    
    @Override
    public boolean updateBOQStatus(String boqId, BOQDTO.Status status) {
        return boqService.updateBOQStatus(boqId, status);
    } 
    
    @Override
    public BOQWithItemsDTO getBOQWithItemsById(String boqId) {
        try {
            BOQDTO boq = boqService.getBOQById(boqId);
            if (boq != null) {
                List<com.structurax.root.structurax.root.dto.BOQitemDTO> items = boqService.getBOQItemsByBOQId(boqId);
                return new BOQWithItemsDTO(boq, items);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
