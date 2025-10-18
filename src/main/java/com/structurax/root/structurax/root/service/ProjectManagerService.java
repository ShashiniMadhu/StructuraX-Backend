package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface ProjectManagerService {
    SiteVisitLogDTO createVisitLog(SiteVisitLogDTO dto);
    List<SiteVisitLogDTO> getSiteVisitLogsByPmId(String pmId);
    boolean updateVisitLog(SiteVisitLogDTO dto);
    List<VisitRequestDTO> getAllVisitRequests(String pmId);
    boolean updateVisitRequest(VisitRequestDTO dto);
    List<ProjectInitiateDTO> getOngoingProjectsByPmId(String pmId);
    boolean approveRequestSiteResource(Integer requestId);
    boolean rejectRequestSiteResource(Integer requestId);
    List<TodoDTO> getTodosByEmployeeId(String employeeId);
    TodoDTO createTodo(TodoDTO todo);
    boolean updateTodo(TodoDTO todo);
    boolean deleteTodo(Integer taskId);
    List<DailyUpdatesDTO> getDailyUpdatesByPmId(String pmId);
    List<ProjectInitiateDTO> getNullLocationProjectsByPmId(String pmId);
    boolean updateProjectLocation(String projectId, String location);
    boolean insertProjectMaterials(ProjectMaterialsDTO projectMaterials);
    List<ProjectInitiateDTO> getCompletedProjectsByPmId(String pmId);
    List<DesignDTO> getDesignLink(String projectId);
    List<WBSDTO> getWBSByProjectId(String projectId);
    List<BOQitemDTO> getBOQItemsByProjectId(String projectId);
    List<PaymentDTO> getPaymentByProjectId(String projectId);
    List<RequestSiteResourceDTO> getPendingRequestsByPmId(String pmId);
    List<SiteResourcesDTO> getSiteResourcesByRequestId(Integer requestId);
    List<ProjectMaterialsDTO> getProjectMaterialsByProjectId(String projectId);
    List<String> getOngoingProjectIds();


}