package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.WBSDAO;
import com.structurax.root.structurax.root.dto.WBSDTO;
import com.structurax.root.structurax.root.service.WBSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WBSServiceImpl implements WBSService {

    private static final Logger logger = LoggerFactory.getLogger(WBSServiceImpl.class);
    private final WBSDAO wbsDAO;

    public WBSServiceImpl(WBSDAO wbsDAO) {
        this.wbsDAO = wbsDAO;
    }

    @Override
    public List<WBSDTO> getWBSByProjectId(String projectId) {
        logger.info("Fetching all WBS tasks for project_id: {}", projectId);
        return wbsDAO.getWBSByProjectId(projectId);
    }

    @Override
    public WBSDTO getWBSByTaskId(Integer taskId) {
        logger.info("Fetching WBS task with task_id: {}", taskId);
        return wbsDAO.getWBSByTaskId(taskId);
    }

    @Override
    public List<WBSDTO> getWBSByParentId(Integer parentId) {
        logger.info("Fetching child WBS tasks for parent_id: {}", parentId);
        return wbsDAO.getWBSByParentId(parentId);
    }

    @Override
    public List<WBSDTO> getRootWBSTasks(String projectId) {
        logger.info("Fetching root WBS tasks for project_id: {}", projectId);
        return wbsDAO.getRootWBSTasks(projectId);
    }

    @Override
    public WBSDTO createWBS(WBSDTO wbsDTO) {
        logger.info("Creating new WBS task for project_id: {}", wbsDTO.getProjectId());
        return wbsDAO.createWBS(wbsDTO);
    }

    @Override
    public void updateWBS(WBSDTO wbsDTO) {
        logger.info("Updating WBS task with task_id: {}", wbsDTO.getTaskId());
        wbsDAO.updateWBS(wbsDTO);
    }

    @Override
    public void deleteWBS(Integer taskId) {
        logger.info("Deleting WBS task with task_id: {}", taskId);
        wbsDAO.deleteWBS(taskId);
    }
}