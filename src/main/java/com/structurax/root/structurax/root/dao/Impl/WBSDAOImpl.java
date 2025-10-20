package com.structurax.root.structurax.root.dao.Impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.WBSDAO;
import com.structurax.root.structurax.root.dto.WBSDTO;

@Repository
public class WBSDAOImpl implements WBSDAO {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public int insertWBSTask(WBSDTO wbs) {
        String sql = "INSERT INTO wbs (project_id, parent_id, name, status, milestone) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, wbs.getProjectId());
            if (wbs.getParentId() != null) {
                ps.setInt(2, wbs.getParentId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, wbs.getName());
            ps.setString(4, wbs.getStatus());
            ps.setBoolean(5, wbs.getMilestone() != null ? wbs.getMilestone() : false);
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().intValue();
        } else {
            throw new RuntimeException("Failed to insert WBS task");
        }
    }
    
    @Override
    public List<Integer> insertBulkWBSTasks(List<WBSDTO> wbsTasks) {
        if (wbsTasks == null || wbsTasks.isEmpty()) {
            throw new IllegalArgumentException("WBS tasks list cannot be null or empty");
        }
        
        List<Integer> generatedIds = new java.util.ArrayList<>();
        String sql = "INSERT INTO wbs (project_id, parent_id, name, status, milestone) VALUES (?, ?, ?, ?, ?)";
        
        try {
            for (WBSDTO wbs : wbsTasks) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                
                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, wbs.getProjectId());
                    if (wbs.getParentId() != null) {
                        ps.setInt(2, wbs.getParentId());
                    } else {
                        ps.setNull(2, java.sql.Types.INTEGER);
                    }
                    ps.setString(3, wbs.getName());
                    ps.setString(4, wbs.getStatus());
                    ps.setBoolean(5, wbs.getMilestone() != null ? wbs.getMilestone() : false);
                    return ps;
                }, keyHolder);
                
                if (keyHolder.getKey() != null) {
                    int generatedId = keyHolder.getKey().intValue();
                    generatedIds.add(generatedId);
                    
                    // Update the taskId in the DTO for reference
                    wbs.setTaskId(generatedId);
                } else {
                    throw new RuntimeException("Failed to insert WBS task: " + wbs.getName());
                }
            }
            
            return generatedIds;
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert bulk WBS tasks: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<WBSDTO> getWBSByProjectId(String projectId) {
        String sql = "SELECT task_id, project_id, parent_id, name, status, milestone FROM wbs WHERE project_id = ? ORDER BY task_id";
        return jdbcTemplate.query(sql, new Object[]{projectId}, (rs, rowNum) -> {
            WBSDTO wbs = new WBSDTO();
            wbs.setTaskId(rs.getInt("task_id"));
            wbs.setProjectId(rs.getString("project_id"));
            int parentId = rs.getInt("parent_id");
            wbs.setParentId(rs.wasNull() ? null : parentId);
            wbs.setName(rs.getString("name"));
            wbs.setStatus(rs.getString("status"));
            wbs.setMilestone(rs.getBoolean("milestone"));
            return wbs;
        });
    }
    
    @Override
    public WBSDTO getWBSByTaskId(int taskId) {
        String sql = "SELECT task_id, project_id, parent_id, name, status, milestone FROM wbs WHERE task_id = ?";
        List<WBSDTO> results = jdbcTemplate.query(sql, new Object[]{taskId}, (rs, rowNum) -> {
            WBSDTO wbs = new WBSDTO();
            wbs.setTaskId(rs.getInt("task_id"));
            wbs.setProjectId(rs.getString("project_id"));
            int parentId = rs.getInt("parent_id");
            wbs.setParentId(rs.wasNull() ? null : parentId);
            wbs.setName(rs.getString("name"));
            wbs.setStatus(rs.getString("status"));
            wbs.setMilestone(rs.getBoolean("milestone"));
            return wbs;
        });
        
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    public boolean updateWBSTask(WBSDTO wbs) {
        String sql = "UPDATE wbs SET project_id = ?, parent_id = ?, name = ?, status = ?, milestone = ? WHERE task_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql,
                wbs.getProjectId(),
                wbs.getParentId(),
                wbs.getName(),
                wbs.getStatus(),
                wbs.getMilestone() != null ? wbs.getMilestone() : false,
                wbs.getTaskId()
            );
            return rowsAffected > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update WBS task: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateWBSMilestone(int taskId, boolean milestone) {
        String sql = "UPDATE wbs SET milestone = ? WHERE task_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, milestone, taskId);
            return rowsAffected > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update WBS milestone: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteWBSTask(int taskId) {
        try {
            // Disable foreign key checks temporarily
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Get all child tasks recursively and collect their IDs
            List<Integer> taskIdsToDelete = new java.util.ArrayList<>();
            collectTaskIds(taskId, taskIdsToDelete);
            
            // Delete all tasks at once
            if (!taskIdsToDelete.isEmpty()) {
                String placeholders = String.join(",", taskIdsToDelete.stream()
                    .map(String::valueOf).toArray(String[]::new));
                String sql = "DELETE FROM wbs WHERE task_id IN (" + placeholders + ")";
                jdbcTemplate.update(sql);
            }
            
            // Re-enable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            return true;
        } catch (Exception e) {
            // Make sure to re-enable foreign key checks even if an error occurs
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            } catch (Exception ex) {
                // Ignore error when re-enabling
            }
            throw new RuntimeException("Failed to delete WBS task: " + e.getMessage(), e);
        }
    }
    
    // Helper method to recursively collect all task IDs (including children)
    private void collectTaskIds(int taskId, List<Integer> taskIds) {
        taskIds.add(taskId);
        List<WBSDTO> children = getChildTasks(taskId);
        for (WBSDTO child : children) {
            collectTaskIds(child.getTaskId(), taskIds);
        }
    }
    
    @Override
    public int deleteWBSByProjectId(String projectId) {
        try {
            // Disable foreign key checks temporarily
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Delete all WBS tasks for the project
            String sql = "DELETE FROM wbs WHERE project_id = ?";
            int rowsDeleted = jdbcTemplate.update(sql, projectId);
            
            // Re-enable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            return rowsDeleted;
        } catch (Exception e) {
            // Make sure to re-enable foreign key checks even if an error occurs
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            } catch (Exception ex) {
                // Ignore error when re-enabling
            }
            throw new RuntimeException("Failed to delete WBS for project: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<WBSDTO> getChildTasks(int parentId) {
        String sql = "SELECT task_id, project_id, parent_id, name, status, milestone FROM wbs WHERE parent_id = ? ORDER BY task_id";
        return jdbcTemplate.query(sql, new Object[]{parentId}, (rs, rowNum) -> {
            WBSDTO wbs = new WBSDTO();
            wbs.setTaskId(rs.getInt("task_id"));
            wbs.setProjectId(rs.getString("project_id"));
            int parentIdValue = rs.getInt("parent_id");
            wbs.setParentId(rs.wasNull() ? null : parentIdValue);
            wbs.setName(rs.getString("name"));
            wbs.setStatus(rs.getString("status"));
            wbs.setMilestone(rs.getBoolean("milestone"));
            return wbs;
        });
    }
}
