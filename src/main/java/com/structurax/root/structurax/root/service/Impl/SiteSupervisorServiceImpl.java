// SiteSupervisorServiceImpl.java
package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.SiteSupervisorDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.SiteSupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service  // REQUIRED!
public class SiteSupervisorServiceImpl implements SiteSupervisorService {

    @Autowired
    private SiteSupervisorDAO siteSupervisorDAO;


    public List<LaborAttendanceDTO> createLaborAttendance(List<LaborAttendanceDTO> laborAttendanceList) {
        return siteSupervisorDAO.createLaborAttendance(laborAttendanceList);
    }


    @Override
    public List<LaborAttendanceDTO> getAllLaborAttendance() {
        return siteSupervisorDAO.getAllLaborAttendance();
    }

    @Override
    public List<LaborAttendanceDTO> getAttendanceByProjectIdAndDate(Integer project_id, Date date) {
        return siteSupervisorDAO.getAttendanceByProjectIdAndDate(project_id,date);
    }

    @Override
    public LaborAttendanceDTO updateLaborAttendance(LaborAttendanceDTO laborAttendanceDTO) {
        return siteSupervisorDAO.updateLaborAttendance(laborAttendanceDTO);
    }


    @Override
    public List<LaborAttendanceDTO> deleteLaborAttendanceRecord(Integer project_id, Date date) {
        return siteSupervisorDAO.deleteLaborAttendanceRecord(project_id, date);
    }

    @Override
    public List<MaterialDTO> getMaterialsByRequestId(Integer id) {
        return siteSupervisorDAO.getMaterialsByRequestId(id);
    }

    @Override
    public List<RequestDTO> getAllMaterialRequests() {
        return siteSupervisorDAO.getAllMaterialRequests();
    }

    @Override
    public List<RequestDTO> getAllToolRequests() {
        return siteSupervisorDAO.getAllToolRequests();
    }

    @Override
    public RequestDTO createMaterialRequest(RequestDTO requestDTO) {
        RequestDTO request=siteSupervisorDAO.createMaterialRequest(requestDTO);
        return request;
    }

    /*@Override
    public RequestDTO getRequestById(Integer id) {
        RequestDTO request = siteSupervisorDAO.getRequestById(id); // fetch plan
        if (request != null) {
            List<MaterialDTO> materials = siteSupervisorDAO.getMaterialsByRequestId(id); // fetch installments
            request.setMaterials(materials); // attach
        }
        return request;
    }*/
}
