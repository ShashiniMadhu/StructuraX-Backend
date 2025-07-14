package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.VisitLogDAO;
import com.structurax.root.structurax.root.dto.VisitLogDTO;
import com.structurax.root.structurax.root.service.VisitLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitLogServiceImpl implements VisitLogService {

    @Autowired
    private VisitLogDAO visitLogDAO;

    @Override
    public VisitLogDTO createVisitLog(VisitLogDTO dto) {
        return visitLogDAO.createVisitLog(dto);
    }

    @Override
    public List<VisitLogDTO> getAllVisitLogs() {
        return visitLogDAO.getAllVisitLogs();
    }

    @Override
    public VisitLogDTO getVisitLogById(Integer id) {
        return visitLogDAO.getVisitLogById(id);
    }

    @Override
    public VisitLogDTO deleteVisitLogById(Integer id) {
        return visitLogDAO.deleteVisitLogById(id);
    }
}