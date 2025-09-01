package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface ProjectManagerService {
    SiteVisitLogDTO createVisitLog(SiteVisitLogDTO dto);
    List<SiteVisitLogDTO> getAllVisitLogs();
    SiteVisitLogDTO getVisitLogById(Integer id);
    boolean updateVisitLog(SiteVisitLogDTO dto);
    List<VisitRequestDTO> getAllVisitRequests();
    boolean updateVisitRequest(VisitRequestDTO dto);
    List<ProjectInitiateDTO> getOngoingProjectsByPmId(String pmId);
    List<RequestSiteResourceDTO> getRequestSiteResourcesByPmId(String pmId);
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
    String getDesignLink(String pmId);
    List<WBSDTO> getWBSByProjectId(String projectId);
    List<BOQitemDTO> getBOQItemsByProjectId(String projectId);

}