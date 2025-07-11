// SiteSupervisorService.java
package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.InstallmentDTO;
import com.structurax.root.structurax.root.dto.LaborAttendanceDTO;
import com.structurax.root.structurax.root.dto.MaterialDTO;
import com.structurax.root.structurax.root.dto.RequestDTO;

import java.sql.Date;
import java.util.List;

public interface SiteSupervisorService {

    List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendance);

    List<LaborAttendanceDTO> getAllLaborAttendance();

    List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(Integer project_id, Date date);

    LaborAttendanceDTO updateLaborAttendance(LaborAttendanceDTO laborAttendanceDTO);

    List<LaborAttendanceDTO> deleteLaborAttendanceRecord(Integer project_id, Date date);


    // material request
    List<MaterialDTO> getMaterialsByRequestId(Integer id);

    List<RequestDTO> getAllMaterialRequests();

    List<RequestDTO> getAllToolRequests();

    //RequestDTO getRequestById(Integer id);

    RequestDTO createMaterialRequest(RequestDTO requestDTO);
}
