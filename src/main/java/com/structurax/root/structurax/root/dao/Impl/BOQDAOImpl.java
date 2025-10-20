package com.structurax.root.structurax.root.dao.Impl;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.BOQDAO;
import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;

@Repository
public class BOQDAOImpl implements BOQDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void updateBOQ(BOQDTO boq) {
        String sql = "UPDATE boq SET project_id = ?, date = ?, qs_id = ?, status = ? WHERE boq_id = ?";
        jdbcTemplate.update(sql,
                boq.getProjectId(),
                boq.getDate(),
                boq.getQsId(),
                boq.getStatus().name().toLowerCase(),
                boq.getBoqId()
        );
    }

    @Override
    public void deleteBOQItemsByBOQId(String boqId) {
        String sql = "DELETE FROM boq_item WHERE boq_id = ?";
        jdbcTemplate.update(sql, boqId);
    }

    @Override
    public String insertBOQ(BOQDTO boq) {
        String sql = "INSERT INTO boq (project_id, date, qs_id, status) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, boq.getProjectId());
            ps.setObject(2, boq.getDate());
            ps.setString(3, boq.getQsId());
            ps.setString(4, boq.getStatus().name().toLowerCase());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            return keyHolder.getKey().toString();
        } else {
            return null;
        }
    }

    @Override
    public void insertBOQItem(BOQitemDTO item) {
        String sql = "INSERT INTO boq_item (boq_id, item_description, rate, unit, quantity, amount) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                item.getBoqId(),
                item.getItemDescription(),
                item.getRate(),
                item.getUnit(),
                item.getQuantity(),
                item.getAmount()
        );
    }

    @Override
    public BOQDTO getBOQById(String boqId) {
        String sql = "SELECT boq_id, project_id, date, qs_id, status FROM boq WHERE boq_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{boqId}, (rs, rowNum) -> {
            BOQDTO boq = new BOQDTO();
            boq.setBoqId(rs.getString("boq_id"));
            boq.setProjectId(rs.getString("project_id"));
            boq.setDate(rs.getObject("date", java.time.LocalDate.class));
            boq.setQsId(rs.getString("qs_id"));
            boq.setStatus(BOQDTO.Status.valueOf(rs.getString("status").toUpperCase()));
            return boq;
        });
    }

    @Override
    public BOQDTO getBOQByProjectId(String projectId) {
        String sql = "SELECT boq_id, project_id, date, qs_id, status FROM boq WHERE project_id = ? ORDER BY date DESC LIMIT 1";
        List<BOQDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            BOQDTO boq = new BOQDTO();
            boq.setBoqId(rs.getString("boq_id"));
            boq.setProjectId(rs.getString("project_id"));
            boq.setDate(rs.getObject("date", java.time.LocalDate.class));
            boq.setQsId(rs.getString("qs_id"));
            boq.setStatus(BOQDTO.Status.valueOf(rs.getString("status").toUpperCase()));
            return boq;
        }, projectId);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<BOQitemDTO> getBOQItemsByBOQId(String boqId) {
        String sql = "SELECT item_id, boq_id, item_description, rate, unit, quantity, amount FROM boq_item WHERE boq_id = ?";
        return jdbcTemplate.query(sql, new Object[]{boqId}, (rs, rowNum) -> {
            BOQitemDTO item = new BOQitemDTO();
            item.setItemId(rs.getString("item_id"));
            item.setBoqId(rs.getString("boq_id"));
            item.setItemDescription(rs.getString("item_description"));
            item.setRate(rs.getDouble("rate"));
            item.setUnit(rs.getString("unit"));
            item.setQuantity(rs.getDouble("quantity"));
            item.setAmount(rs.getDouble("amount"));
            return item;
        });
    }
    // Update BOQ item and set BOQ status to 'final' if status is 'approved' and item name was changed
    public void updateBOQItem(BOQitemDTO item) {
        // Get the current item from DB
        String selectSql = "SELECT item_description FROM boq_item WHERE item_id = ?";
        String currentDescription = jdbcTemplate.queryForObject(selectSql, (rs, rowNum) -> rs.getString("item_description"), item.getItemId());

        // Update the item
        String updateSql = "UPDATE boq_item SET item_description = ?, rate = ?, unit = ?, quantity = ?, amount = ? WHERE item_id = ?";
        jdbcTemplate.update(updateSql,
                item.getItemDescription(),
                item.getRate(),
                item.getUnit(),
                item.getQuantity(),
                item.getAmount(),
                item.getItemId()
        );

        // If item name changed, check BOQ status
        if (currentDescription != null && !currentDescription.equals(item.getItemDescription())) {
            String boqStatusSql = "SELECT status FROM boq WHERE boq_id = ?";
            String status = jdbcTemplate.queryForObject(boqStatusSql, (rs, rowNum) -> rs.getString("status"), item.getBoqId());
            if ("approved".equalsIgnoreCase(status)) {
                String updateBoqStatusSql = "UPDATE boq SET status = ? WHERE boq_id = ?";
                jdbcTemplate.update(updateBoqStatusSql, "final", item.getBoqId());
            }
        }
    }

    // SQS specific methods implementation
    @Override
    public List<BOQDTO> getAllBOQs() {
        String sql = "SELECT boq_id, project_id, date, qs_id, status FROM boq ORDER BY date DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BOQDTO boq = new BOQDTO();
            boq.setBoqId(rs.getString("boq_id"));
            boq.setProjectId(rs.getString("project_id"));
            boq.setDate(rs.getObject("date", java.time.LocalDate.class));
            boq.setQsId(rs.getString("qs_id"));
            boq.setStatus(BOQDTO.Status.valueOf(rs.getString("status").toUpperCase()));
            return boq;
        });
    }

    @Override
    public boolean updateBOQStatus(String boqId, BOQDTO.Status status) {
        try {
            String sql = "UPDATE boq SET status = ? WHERE boq_id = ?";
            int rowsAffected = jdbcTemplate.update(sql, status.name().toLowerCase(), boqId);
            return rowsAffected > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<com.structurax.root.structurax.root.dto.BOQWithProjectDTO> getAllBOQsWithProjectInfo() {
        String sql = """
            SELECT b.boq_id, b.project_id, b.date, b.qs_id, b.status,
                   p.name as project_name, p.location as project_location, p.category as project_category,
                   u.name as qs_name
            FROM boq b
            LEFT JOIN project p ON b.project_id = p.project_id
            LEFT JOIN employee e ON b.qs_id = e.employee_id
            LEFT JOIN users u ON e.user_id = u.user_id
            ORDER BY b.date DESC
            """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            // Create BOQ DTO
            BOQDTO boq = new BOQDTO();
            boq.setBoqId(rs.getString("boq_id"));
            boq.setProjectId(rs.getString("project_id"));
            boq.setDate(rs.getObject("date", java.time.LocalDate.class));
            boq.setQsId(rs.getString("qs_id"));
            boq.setStatus(BOQDTO.Status.valueOf(rs.getString("status").toUpperCase()));
            
            // Get BOQ items
            List<com.structurax.root.structurax.root.dto.BOQitemDTO> items = getBOQItemsByBOQId(rs.getString("boq_id"));
            
            // Create BOQWithProjectDTO
            com.structurax.root.structurax.root.dto.BOQWithProjectDTO boqWithProject = 
                new com.structurax.root.structurax.root.dto.BOQWithProjectDTO();
            boqWithProject.setBoq(boq);
            boqWithProject.setItems(items);
            boqWithProject.setProjectName(rs.getString("project_name"));
            boqWithProject.setProjectLocation(rs.getString("project_location"));
            boqWithProject.setProjectCategory(rs.getString("project_category"));
            boqWithProject.setQsName(rs.getString("qs_name"));
            
            return boqWithProject;
        });
    }
}
