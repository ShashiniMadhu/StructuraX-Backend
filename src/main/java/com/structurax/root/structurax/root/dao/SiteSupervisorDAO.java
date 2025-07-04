package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.LaborAttendanceDTO;
import java.sql.Date;
import java.util.List;

public interface SiteSupervisorDAO {

    List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendance);

    List<LaborAttendanceDTO> getAllLaborAttendance();

    List<LaborAttendanceDTO> updateLaborAttendance(List<LaborAttendanceDTO> laborAttendanceDTO);


    List<LaborAttendanceDTO> deleteLaborAttendanceRecord(Integer projectId, Date date);

    List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(Integer projectId, Date date);
}
