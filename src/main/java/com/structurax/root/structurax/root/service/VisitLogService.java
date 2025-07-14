package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dao.VisitLogDAO;
import com.structurax.root.structurax.root.dto.VisitLogDTO;
import java.util.List;
public interface VisitLogService {
    VisitLogDTO createVisitLog(VisitLogDTO dto);
    List<VisitLogDTO> getAllVisitLogs();
    VisitLogDTO getVisitLogById(Integer id);
    VisitLogDTO deleteVisitLogById(Integer id);
}