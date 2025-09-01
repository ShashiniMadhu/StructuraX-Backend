package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface ProjectManagerDAO {
    SiteVisitLogDTO createVisitLog(SiteVisitLogDTO visitLogDTO);
    List<SiteVisitLogDTO> getAllVisitLogs();
    SiteVisitLogDTO getVisitLogById(Integer id);
    boolean updateVisitLog(SiteVisitLogDTO visitLogDTO);
    List<VisitRequestDTO> getAllVisitRequests();
    boolean updateVisitRequest(VisitRequestDTO visitRequestDTO);
    List<ProjectInitiateDTO> getProjectsByPmIdAndStatus(String pmId, String status);
    List<RequestSiteResourceDTO> getRequestSiteResourcesByPmId(String pmId);
    boolean updateRequestSiteResourceApproval(Integer requestId, boolean pmApproval);
    List<TodoDTO> getTodosByEmployeeId(String employeeId);
    TodoDTO createTodo(TodoDTO todo);
    boolean updateTodo(TodoDTO todo);
    boolean deleteTodo(Integer taskId);
    List<DailyUpdatesDTO> getDailyUpdatesByPmId(String pmId);
    List<ProjectInitiateDTO> getProjectsWithNullLocationByPmId(String pmId);
    boolean updateProjectLocation(String projectId, String location);
    boolean insertProjectMaterials(ProjectMaterialsDTO projectMaterials);
    List<ProjectInitiateDTO> getCompletedProjectsByPmId(String pmId);
    String getDesignLink(String pmId);
    List<WBSDTO> getWBSByProjectId(String projectId);
    List<BOQitemDTO> getBOQItemsByProjectId(String projectId);

}
