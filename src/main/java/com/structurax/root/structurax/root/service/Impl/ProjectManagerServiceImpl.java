package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.ProjectManagerDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.ProjectManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectManagerServiceImpl implements ProjectManagerService {

    @Autowired
    private ProjectManagerDAO visitLogDAO;

    @Override
    public SiteVisitLogDTO createVisitLog(SiteVisitLogDTO dto) {
        return visitLogDAO.createVisitLog(dto);
    }


    @Override
    public List<SiteVisitLogDTO> getSiteVisitLogsByPmId(String pmId) {
        return visitLogDAO.getSiteVisitLogsByPmId(pmId);
    }

    @Override
    public boolean updateVisitLog(SiteVisitLogDTO dto) {
        return visitLogDAO.updateVisitLog(dto);
    }

    @Override
    public List<VisitRequestDTO> getAllVisitRequests( String pmId){
        return visitLogDAO.getAllVisitRequests(pmId);
    }

    @Override
    public  boolean updateVisitRequest(VisitRequestDTO dto){
        return  visitLogDAO.updateVisitRequest(dto);
    }
    @Override
    public List<ProjectInitiateDTO> getOngoingProjectsByPmId(String pmId){
        return visitLogDAO.getProjectsByPmIdAndStatus(pmId , "ongoing");
    }

    @Override
    public boolean approveRequestSiteResource(Integer requestId) {
        return visitLogDAO.updateRequestSiteResourceApproval(requestId, "accepted");
    }

    @Override
    public boolean rejectRequestSiteResource(Integer requestId) {
        return visitLogDAO.updateRequestSiteResourceApproval(requestId, "rejected");
    }

    @Override
    public List<TodoDTO> getTodosByEmployeeId(String employeeId) {
        return visitLogDAO.getTodosByEmployeeId(employeeId);
    }

    @Override
    public TodoDTO createTodo(TodoDTO todo) {
        return visitLogDAO.createTodo(todo);
    }

    @Override
    public boolean updateTodo(TodoDTO todo) {
        return visitLogDAO.updateTodo(todo);
    }

    @Override
    public boolean deleteTodo(Integer taskId) {
        return visitLogDAO.deleteTodo(taskId);
    }

    @Override
    public List<DailyUpdatesDTO> getDailyUpdatesByPmId(String pmId) {
        return visitLogDAO.getDailyUpdatesByPmId(pmId);
    }

    @Override
    public List<ProjectInitiateDTO> getNullLocationProjectsByPmId(String pmId) {
        return visitLogDAO.getProjectsWithNullLocationByPmId(pmId);
    }

    @Override
    public boolean updateProjectLocation(String projectId, String location) {
        return visitLogDAO.updateProjectLocation(projectId, location);
    }

    @Override
    public boolean insertProjectMaterials(ProjectMaterialsDTO projectMaterials) {
        return visitLogDAO.insertProjectMaterials(projectMaterials);
    }

    @Override
    public List<ProjectInitiateDTO> getCompletedProjectsByPmId(String pmId) {
        return visitLogDAO.getCompletedProjectsByPmId(pmId);
    }

    @Override
    public List<DesignDTO> getDesignLink(String projectId) {
        return visitLogDAO.getDesignLink(projectId);
    }

    @Override
    public List<WBSDTO> getWBSByProjectId(String projectId) {
        return visitLogDAO.getWBSByProjectId(projectId);
    }

    @Override
    public List<BOQitemDTO> getBOQItemsByProjectId(String projectId) {
        return visitLogDAO.getBOQItemsByProjectId(projectId);
    }
    @Override
    public List<PaymentDTO> getPaymentByProjectId(String projectId) {
        return visitLogDAO.getPaymentByProjectId(projectId);
    }

    @Override
    public List<RequestSiteResourceDTO> getPendingRequestsByPmId(String pmId) {
        return visitLogDAO.getPendingRequestsByPmId(pmId);
    }

    @Override
    public List<SiteResourcesDTO> getSiteResourcesByRequestId(Integer requestId) {
        return visitLogDAO.getSiteResourcesByRequestId(requestId);
    }

    @Override
    public List<ProjectMaterialsDTO> getProjectMaterialsByProjectId(String projectId){
        return  visitLogDAO.getProjectMaterialsByProjectId(projectId);
    }



}