package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import com.structurax.root.structurax.root.dto.VisitRequestDTO;

import java.util.List;

public interface ProjectManagerDAO {
    SiteVisitLogDTO createVisitLog(SiteVisitLogDTO visitLogDTO);
    List<SiteVisitLogDTO> getAllVisitLogs();
    SiteVisitLogDTO getVisitLogById(Integer id);
    boolean updateVisitLog(SiteVisitLogDTO visitLogDTO);
    List<VisitRequestDTO> getAllVisitRequests();
    boolean updateVisitRequest(VisitRequestDTO visitRequestDTO);
}
