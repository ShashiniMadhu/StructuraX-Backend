package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.WBSDAO;
import com.structurax.root.structurax.root.dto.WBSDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class WBSDAOImpl implements WBSDAO {

    private static final Logger logger = LoggerFactory.getLogger(WBSDAOImpl.class);
    private final DatabaseConnection databaseConnection;

    public WBSDAOImpl(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<WBSDTO> getWBSByProjectId(String projectId) {
        String sql = "SELECT task_id, project_id, parent_id, name, status, milestone " +
                     "FROM wbs WHERE project_id = ? ORDER BY task_id";
        List<WBSDTO> wbsList = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                wbsList.add(mapResultSetToDTO(rs));
            }
            logger.info("Retrieved {} WBS tasks for project_id: {}", wbsList.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving WBS tasks for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving WBS tasks: " + e.getMessage(), e);
        }
        return wbsList;
    }

    @Override
    public WBSDTO getWBSByTaskId(Integer taskId) {
        String sql = "SELECT task_id, project_id, parent_id, name, status, milestone " +
                     "FROM wbs WHERE task_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                logger.info("Retrieved WBS task with task_id: {}", taskId);
                return mapResultSetToDTO(rs);
            } else {
                logger.warn("No WBS task found with task_id: {}", taskId);
                return null;
            }
        } catch (SQLException e) {
            logger.error("Error retrieving WBS task by id {}: {}", taskId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving WBS task: " + e.getMessage(), e);
        }
    }

    @Override
    public List<WBSDTO> getWBSByParentId(Integer parentId) {
        String sql = "SELECT task_id, project_id, parent_id, name, status, milestone " +
                     "FROM wbs WHERE parent_id = ? ORDER BY task_id";
        List<WBSDTO> wbsList = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                wbsList.add(mapResultSetToDTO(rs));
            }
            logger.info("Retrieved {} child WBS tasks for parent_id: {}", wbsList.size(), parentId);
        } catch (SQLException e) {
            logger.error("Error retrieving child WBS tasks for parent {}: {}", parentId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving child WBS tasks: " + e.getMessage(), e);
        }
        return wbsList;
    }

    @Override
    public List<WBSDTO> getRootWBSTasks(String projectId) {
        String sql = "SELECT task_id, project_id, parent_id, name, status, milestone " +
                     "FROM wbs WHERE project_id = ? AND parent_id IS NULL ORDER BY task_id";
        List<WBSDTO> wbsList = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, projectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                wbsList.add(mapResultSetToDTO(rs));
            }
            logger.info("Retrieved {} root WBS tasks for project_id: {}", wbsList.size(), projectId);
        } catch (SQLException e) {
            logger.error("Error retrieving root WBS tasks for project {}: {}", projectId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving root WBS tasks: " + e.getMessage(), e);
        }
        return wbsList;
    }

    @Override
    public WBSDTO createWBS(WBSDTO wbsDTO) {
        String sql = "INSERT INTO wbs (project_id, parent_id, name, status, milestone) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, wbsDTO.getProjectId());
            if (wbsDTO.getParentId() != null) {
                ps.setInt(2, wbsDTO.getParentId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, wbsDTO.getName());
            ps.setString(4, wbsDTO.getStatus());
            ps.setBoolean(5, wbsDTO.getMilestone() != null ? wbsDTO.getMilestone() : false);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating WBS task failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    wbsDTO.setTaskId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating WBS task failed, no ID obtained.");
                }
            }

            logger.info("WBS task created successfully with ID: {}", wbsDTO.getTaskId());
            return wbsDTO;

        } catch (SQLException e) {
            logger.error("Error creating WBS task: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating WBS task: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateWBS(WBSDTO wbsDTO) {
        String sql = "UPDATE wbs SET name = ?, status = ?, milestone = ?, parent_id = ? WHERE task_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, wbsDTO.getName());
            ps.setString(2, wbsDTO.getStatus());
            ps.setBoolean(3, wbsDTO.getMilestone() != null ? wbsDTO.getMilestone() : false);
            if (wbsDTO.getParentId() != null) {
                ps.setInt(4, wbsDTO.getParentId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, wbsDTO.getTaskId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No WBS task found with task_id: {}", wbsDTO.getTaskId());
            } else {
                logger.info("WBS task updated successfully with ID: {}", wbsDTO.getTaskId());
            }

        } catch (SQLException e) {
            logger.error("Error updating WBS task: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating WBS task: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteWBS(Integer taskId) {
        String sql = "DELETE FROM wbs WHERE task_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, taskId);
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("No WBS task found with task_id: {}", taskId);
            } else {
                logger.info("WBS task deleted successfully with ID: {}", taskId);
            }

        } catch (SQLException e) {
            logger.error("Error deleting WBS task: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting WBS task: " + e.getMessage(), e);
        }
    }

    private WBSDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        WBSDTO dto = new WBSDTO();
        dto.setTaskId(rs.getInt("task_id"));
        dto.setProjectId(rs.getString("project_id"));

        int parentId = rs.getInt("parent_id");
        dto.setParentId(rs.wasNull() ? null : parentId);

        dto.setName(rs.getString("name"));
        dto.setStatus(rs.getString("status"));
        dto.setMilestone(rs.getBoolean("milestone"));

        return dto;
    }
}
