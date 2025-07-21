// SiteSupervisorService.java
package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.*;

import java.sql.Date;
import java.util.List;

public interface SiteSupervisorService {

    List<ProjectDTO> getProjectsBySsId(String id);

    List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendance);

    List<LaborAttendanceDTO> getAllLaborAttendance();

    List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(String project_id, Date date);

    LaborAttendanceDTO updateLaborAttendance(LaborAttendanceDTO laborAttendanceDTO);

    List<LaborAttendanceDTO> deleteLaborAttendanceRecord(String project_id, Date date);


    // material and tool  request
    List<SiteResourceDTO> getMaterialsByRequestId(Integer id);

    List<RequestSiteResourcesDTO> getAllMaterialRequests();

    List<RequestSiteResourcesDTO> getAllToolRequests();

    //RequestDTO getRequestById(Integer id);
    RequestSiteResourcesDTO createMaterialRequest(RequestSiteResourcesDTO requestDTO);

    RequestSiteResourcesDTO updateRequest(RequestSiteResourcesDTO requestSiteResourcesDTO);


    //to-do list
    TodoDTO createToDo(TodoDTO todoDTO);

    List<TodoDTO> getToDoBySpId(String id );

    boolean updateTodo(TodoDTO todoDTO);

    boolean deleteToDoTask(Integer taskId);
}