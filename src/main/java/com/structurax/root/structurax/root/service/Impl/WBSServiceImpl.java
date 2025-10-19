package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.WBSDAO;
import com.structurax.root.structurax.root.dto.WBSDTO;
import com.structurax.root.structurax.root.service.WBSService;

@Service
public class WBSServiceImpl implements WBSService {
    
    @Autowired
    private WBSDAO wbsDAO;
    
    @Override
    public int createWBSTask(WBSDTO wbs) {
        // Validate required fields
        if (wbs.getProjectId() == null || wbs.getProjectId().trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID is required");
        }
        if (wbs.getName() == null || wbs.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name is required");
        }
        if (wbs.getStatus() == null || wbs.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        
        return wbsDAO.insertWBSTask(wbs);
    }
    
    @Override
    public List<Integer> createBulkWBSTasks(List<WBSDTO> wbsTasks) {
        // Validate list
        if (wbsTasks == null || wbsTasks.isEmpty()) {
            throw new IllegalArgumentException("WBS tasks list cannot be null or empty");
        }
        
        // Validate each task
        for (int i = 0; i < wbsTasks.size(); i++) {
            WBSDTO wbs = wbsTasks.get(i);
            if (wbs.getProjectId() == null || wbs.getProjectId().trim().isEmpty()) {
                throw new IllegalArgumentException("Project ID is required for task at index " + i);
            }
            if (wbs.getName() == null || wbs.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Task name is required for task at index " + i);
            }
            if (wbs.getStatus() == null || wbs.getStatus().trim().isEmpty()) {
                throw new IllegalArgumentException("Status is required for task at index " + i);
            }
        }
        
        return wbsDAO.insertBulkWBSTasks(wbsTasks);
    }
    
    @Override
    public List<WBSDTO> getWBSByProjectId(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID is required");
        }
        return wbsDAO.getWBSByProjectId(projectId);
    }
    
    @Override
    public WBSDTO getWBSByTaskId(int taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }
        return wbsDAO.getWBSByTaskId(taskId);
    }
    
    @Override
    public boolean updateWBSTask(WBSDTO wbs) {
        // Validate required fields
        if (wbs.getTaskId() <= 0) {
            throw new IllegalArgumentException("Task ID is required for update");
        }
        if (wbs.getProjectId() == null || wbs.getProjectId().trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID is required");
        }
        if (wbs.getName() == null || wbs.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name is required");
        }
        if (wbs.getStatus() == null || wbs.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        
        return wbsDAO.updateWBSTask(wbs);
    }
    
    @Override
    public boolean updateWBSMilestone(int taskId, boolean milestone) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }
        return wbsDAO.updateWBSMilestone(taskId, milestone);
    }
    
    @Override
    public boolean deleteWBSTask(int taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }
        return wbsDAO.deleteWBSTask(taskId);
    }
    
    @Override
    public int deleteCompleteWBS(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project ID is required");
        }
        return wbsDAO.deleteWBSByProjectId(projectId);
    }
    
    @Override
    public List<WBSDTO> getChildTasks(int parentId) {
        if (parentId <= 0) {
            throw new IllegalArgumentException("Parent ID must be positive");
        }
        return wbsDAO.getChildTasks(parentId);
    }
}
