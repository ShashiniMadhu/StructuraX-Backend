package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.*;

import java.sql.Date;
import java.util.List;

public interface SiteSupervisorDAO {

    List<ProjectDTO> getProjectsBySsId(String id);

    List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendance);

    List<LaborAttendanceDTO> getAllLaborAttendance();

    LaborAttendanceDTO updateLaborAttendance(LaborAttendanceDTO laborAttendanceDTO);

    List<LaborAttendanceDTO> deleteLaborAttendanceRecord(String projectId, Date date);

    List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(String projectId, Date date);


    //material request
    List<SiteResourceDTO> getMaterialsByRequestId(Integer id);

    List<RequestSiteResourcesDTO> getAllMaterialRequests();

    List<RequestSiteResourcesDTO> getAllToolRequests();



    //RequestDTO getRequestById(Integer id);

    RequestSiteResourcesDTO createMaterialRequest(RequestSiteResourcesDTO requestDTO);

    RequestSiteResourcesDTO updateRequest(RequestSiteResourcesDTO requestSiteResourcesDTO);

    RequestSiteResourcesDTO getRequestById(int requestId);

    RequestSiteResourcesDTO deleteRequest(int requestId);


    //todo
    TodoDTO createToDo(TodoDTO todoDTO);

    List<TodoDTO> getToDoBySpId(String id);

    boolean updateTodo(TodoDTO todoDTO);

    boolean deleteToDoTask(Integer taskId);
}