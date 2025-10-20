package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.WBSDTO;

public interface WBSService {
    /**
     * Create a new WBS task
     * @param wbs the WBS task to create
     * @return the generated task_id
     */
    int createWBSTask(WBSDTO wbs);
    
    /**
     * Bulk create multiple WBS tasks at once
     * @param wbsTasks list of WBS tasks to create
     * @return list of generated task_ids in the same order
     */
    List<Integer> createBulkWBSTasks(List<WBSDTO> wbsTasks);
    
    /**
     * Get all WBS tasks for a specific project
     * @param projectId the project ID
     * @return list of WBS tasks
     */
    List<WBSDTO> getWBSByProjectId(String projectId);
    
    /**
     * Get a single WBS task by task ID
     * @param taskId the task ID
     * @return the WBS task
     */
    WBSDTO getWBSByTaskId(int taskId);
    
    /**
     * Update a WBS task (all fields)
     * @param wbs the WBS task with updated data
     * @return true if update was successful
     */
    boolean updateWBSTask(WBSDTO wbs);
    
    /**
     * Update only the milestone flag of a WBS task
     * @param taskId the task ID
     * @param milestone the new milestone value
     * @return true if update was successful
     */
    boolean updateWBSMilestone(int taskId, boolean milestone);
    
    /**
     * Delete a single WBS task by task ID
     * @param taskId the task ID
     * @return true if deletion was successful
     */
    boolean deleteWBSTask(int taskId);
    
    /**
     * Delete all WBS tasks for a project (complete WBS)
     * @param projectId the project ID
     * @return number of tasks deleted
     */
    int deleteCompleteWBS(String projectId);
    
    /**
     * Get child tasks of a parent task
     * @param parentId the parent task ID
     * @return list of child WBS tasks
     */
    List<WBSDTO> getChildTasks(int parentId);
    
    /**
     * Get root level WBS tasks (tasks with no parent) for a project
     * @param projectId the project ID
     * @return list of root WBS tasks
     */
    List<WBSDTO> getRootWBSTasks(String projectId);
    
    // Alias methods for backward compatibility
    default WBSDTO createWBS(WBSDTO wbs) {
        int taskId = createWBSTask(wbs);
        wbs.setTaskId(taskId);
        return wbs;
    }
    
    default boolean updateWBS(WBSDTO wbs) {
        return updateWBSTask(wbs);
    }
    
    default boolean deleteWBS(int taskId) {
        return deleteWBSTask(taskId);
    }
    
    default List<WBSDTO> getWBSByParentId(int parentId) {
        return getChildTasks(parentId);
    }
}
