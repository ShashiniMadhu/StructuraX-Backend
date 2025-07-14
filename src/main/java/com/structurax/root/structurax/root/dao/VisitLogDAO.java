package com.structurax.root.structurax.root.dao;

import  com.structurax.root.structurax.root.dto.VisitLogDTO;
import java.util.List;

public interface VisitLogDAO {
    VisitLogDTO createVisitLog(VisitLogDTO visitLogDTO);
    List<VisitLogDTO> getAllVisitLogs();
    VisitLogDTO getVisitLogById(Integer id);
    VisitLogDTO deleteVisitLogById(Integer id);
}
