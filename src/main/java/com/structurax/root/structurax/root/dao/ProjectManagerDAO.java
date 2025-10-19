package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.util.List;

public interface ProjectManagerDAO {
    SiteVisitLogDTO createVisitLog(SiteVisitLogDTO visitLogDTO);
//    List<SiteVisitLogDTO> getAllVisitLogs();
List<SiteVisitLogDTO> getSiteVisitLogsByPmId(String pmId);
    boolean updateVisitLog(SiteVisitLogDTO visitLogDTO);
    List<VisitRequestDTO> getAllVisitRequests( String pmId);
    boolean updateVisitRequest(VisitRequestDTO visitRequestDTO);
    List<ProjectInitiateDTO> getProjectsByPmIdAndStatus(String pmId, String status);
    boolean updateRequestSiteResourceApproval(Integer requestId, String pmApproval);
    List<TodoDTO> getTodosByEmployeeId(String employeeId);
    TodoDTO createTodo(TodoDTO todo);
    boolean updateTodo(TodoDTO todo);
    boolean deleteTodo(Integer taskId);
    List<DailyUpdatesDTO> getDailyUpdatesByPmId(String pmId);
    List<ProjectInitiateDTO> getProjectsWithNullLocationByPmId(String pmId);
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
