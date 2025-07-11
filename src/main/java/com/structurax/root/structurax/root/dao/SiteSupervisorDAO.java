package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.LaborAttendanceDTO;
import com.structurax.root.structurax.root.dto.MaterialDTO;
import com.structurax.root.structurax.root.dto.RequestDTO;

import java.sql.Date;
import java.util.List;

public interface SiteSupervisorDAO {

    List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendance);

    List<LaborAttendanceDTO> getAllLaborAttendance();

    LaborAttendanceDTO updateLaborAttendance(LaborAttendanceDTO laborAttendanceDTO);

    List<LaborAttendanceDTO> deleteLaborAttendanceRecord(Integer projectId, Date date);

    List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(Integer projectId, Date date);


    //material request
    List<MaterialDTO> getMaterialsByRequestId(Integer id);

    List<RequestDTO> getAllMaterialRequests();

    List<RequestDTO> getAllToolRequests();

    //RequestDTO getRequestById(Integer id);

    RequestDTO createMaterialRequest(RequestDTO requestDTO);
}
