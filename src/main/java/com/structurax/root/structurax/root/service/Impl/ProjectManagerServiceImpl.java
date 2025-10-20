package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.ProjectManagerDAO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;
import com.structurax.root.structurax.root.dto.DailyUpdatesDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.PaymentDTO;
import com.structurax.root.structurax.root.dto.ProjectInitiateDTO;
import com.structurax.root.structurax.root.dto.ProjectMaterialsDTO;
import com.structurax.root.structurax.root.dto.RequestSiteResourceDTO;
import com.structurax.root.structurax.root.dto.SiteResourcesDTO;
import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import com.structurax.root.structurax.root.dto.TodoDTO;
import com.structurax.root.structurax.root.dto.VisitRequestDTO;
import com.structurax.root.structurax.root.dto.WBSDTO;
import com.structurax.root.structurax.root.service.ProjectManagerService;

@Service
public class ProjectManagerServiceImpl implements ProjectManagerService {

    @Autowired
    private ProjectManagerDAO visitLogDAO;

    @Override
    public SiteVisitLogDTO createVisitLog(SiteVisitLogDTO dto) {
        return visitLogDAO.createVisitLog(dto);
    }

    @Override
    public List<SiteVisitLogDTO> getAllVisitLogs() {
        // Since the DAO method is not in the interface, we'll use getSiteVisitLogsByPmId
        // For now, return empty list or implement differently
        throw new UnsupportedOperationException("getAllVisitLogs not supported - use getSiteVisitLogsByPmId instead");
    }

    @Override
    public SiteVisitLogDTO getVisitLogById(Integer id) {
        // This method is not in the DAO interface
        throw new UnsupportedOperationException("getVisitLogById not supported");
    }

    @Override
    public boolean updateVisitLog(SiteVisitLogDTO dto) {
        return visitLogDAO.updateVisitLog(dto);
    }

    @Override
    public List<VisitRequestDTO> getAllVisitRequests(){
        // Need to pass pmId - for now throw exception or get from context
        throw new UnsupportedOperationException("getAllVisitRequests requires pmId parameter");
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
    public PaymentDTO getPaymentByProjectId(String projectId) {
        List<PaymentDTO> payments = visitLogDAO.getPaymentByProjectId(projectId);
        return payments.isEmpty() ? null : payments.get(0);
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
    public List<ProjectMaterialsDTO> getProjectMaterialsByProjectId(String projectId) {
        return visitLogDAO.getProjectMaterialsByProjectId(projectId);
    }


    @Override
    public List<String> getOngoingProjectIds() {
        return visitLogDAO.getOngoingProjectIds();
    }



}