package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.SiteVisitLogDTO;
import java.util.List;

public interface ProjectManagerService {
    SiteVisitLogDTO createVisitLog(SiteVisitLogDTO dto);
    List<SiteVisitLogDTO> getAllVisitLogs();
    SiteVisitLogDTO getVisitLogById(Integer id);
}