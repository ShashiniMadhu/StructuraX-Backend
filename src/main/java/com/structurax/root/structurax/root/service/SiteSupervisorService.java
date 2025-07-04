// SiteSupervisorService.java
package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.LaborAttendanceDTO;

import java.sql.Date;
import java.util.List;

public interface SiteSupervisorService {

    List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendance);


    List<LaborAttendanceDTO> getAllLaborAttendance();

    List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(Integer project_id, Date date);

    List<LaborAttendanceDTO> updateLaborAttendance(List<LaborAttendanceDTO> laborAttendanceDTO);

    List<LaborAttendanceDTO> deleteLaborAttendanceRecord(Integer project_id, Date date);
}
