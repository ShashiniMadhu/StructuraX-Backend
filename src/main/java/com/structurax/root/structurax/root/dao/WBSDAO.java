package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.WBSDTO;

import java.util.List;

public interface WBSDAO {

    /**
     * Get all WBS tasks for a specific project
     */
    List<WBSDTO> getWBSByProjectId(String projectId);

    /**
     * Get a single WBS task by task ID
     */
    WBSDTO getWBSByTaskId(Integer taskId);

    /**
     * Get all child tasks for a parent task
     */
    List<WBSDTO> getWBSByParentId(Integer parentId);

    /**
     * Get all root level tasks (tasks with no parent) for a project
     */
    List<WBSDTO> getRootWBSTasks(String projectId);

    /**
     * Create a new WBS task
     */
    WBSDTO createWBS(WBSDTO wbsDTO);

    /**
     * Update WBS task
     */
    void updateWBS(WBSDTO wbsDTO);

    /**
     * Delete WBS task
     */
    void deleteWBS(Integer taskId);
}
