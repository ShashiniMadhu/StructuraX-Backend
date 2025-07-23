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
}
