package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.ProjectManagerDAO;
import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import com.structurax.root.structurax.root.dto.VisitRequestDTO;
import com.structurax.root.structurax.root.service.ProjectManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectManagerServiceImpl implements ProjectManagerService {

    @Autowired
    private ProjectManagerDAO visitLogDAO;

    @Override
    public SiteVisitLogDTO createVisitLog(SiteVisitLogDTO dto) {
        return visitLogDAO.createVisitLog(dto);
    }

    @Override
    public List<SiteVisitLogDTO> getAllVisitLogs() {
        return visitLogDAO.getAllVisitLogs();
    }

    @Override
    public SiteVisitLogDTO getVisitLogById(Integer id) {
        return visitLogDAO.getVisitLogById(id);
    }

    @Override
    public boolean updateVisitLog(SiteVisitLogDTO dto) {
        return visitLogDAO.updateVisitLog(dto);
    }

    @Override
    public List<VisitRequestDTO> getAllVisitRequests(){
        return visitLogDAO.getAllVisitRequests();
    }
    @Override
    public  boolean updateVisitRequest(VisitRequestDTO dto){
        return  visitLogDAO.updateVisitRequest(dto);
    }

}