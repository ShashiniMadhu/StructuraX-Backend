package com.structurax.root.structurax.root.dao.Impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.dto.SupplierDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;

@Repository
public class SupplierDAOImpl implements SupplierDAO {

    private static final Logger logger = LoggerFactory.getLogger(SupplierDAOImpl.class);

    @Autowired
    private DatabaseConnection databaseConnection;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<SupplierDTO> findByEmail(String email) {
        String sql = "SELECT * FROM supplier WHERE email = ?";
        try {
            SupplierDTO supplier = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SupplierDTO.class), email);
            return Optional.ofNullable(supplier);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public CatalogDTO createCatalog(CatalogDTO catalogDTO) {
        // If itemId is provided, use it; otherwise let database auto-increment
        String sql;
        if (catalogDTO.getItemId() != null) {
            sql = "INSERT INTO catalog(item_id, name, description, rate, availability, category) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO catalog(name, description, rate, availability, category) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int paramIndex = 1;

            // Set itemId if provided
            if (catalogDTO.getItemId() != null) {
                ps.setInt(paramIndex++, catalogDTO.getItemId());
            }

            ps.setString(paramIndex++, catalogDTO.getName());
            ps.setString(paramIndex++, catalogDTO.getDescription());
            ps.setFloat(paramIndex++, catalogDTO.getRate()); // Changed to setFloat
            ps.setBoolean(paramIndex++, catalogDTO.getAvailability());
            ps.setString(paramIndex, catalogDTO.getCategory());

            int rowsAffected = ps.executeUpdate();
            logger.info("Catalog created successfully. Rows affected: {}", rowsAffected);

            // Get the generated key (auto-incremented item_id)
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                if (catalogDTO.getItemId() == null) {
                    catalogDTO.setItemId(rs.getInt(1)); // Set the auto-generated item_id
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating catalog: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating catalog: " + e.getMessage(), e);
        }
        return catalogDTO;
    }

    @Override
    public List<CatalogDTO> getAllCatalogs() {
        String sql = "SELECT item_id, name, description, rate, availability, category FROM catalog";
        List<CatalogDTO> catalogs = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CatalogDTO catalog = new CatalogDTO();
                catalog.setItemId(rs.getInt("item_id"));
                catalog.setName(rs.getString("name"));
                catalog.setDescription(rs.getString("description"));
                catalog.setRate(rs.getFloat("rate")); // Changed to getFloat
                catalog.setAvailability(rs.getBoolean("availability"));
                catalog.setCategory(rs.getString("category"));
                catalogs.add(catalog);
            }
            logger.info("Retrieved {} catalogs from database", catalogs.size());
        } catch (SQLException e) {
            logger.error("Error retrieving catalogs: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving catalogs: " + e.getMessage(), e);
        }
        return catalogs;
    }

    @Override
    public void deleteCatalog(Integer itemId) {
        String sql = "DELETE FROM catalog WHERE item_id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No catalog found with item_id: {}", itemId);
                throw new RuntimeException("No catalog found with item_id: " + itemId);
            }
            logger.info("Catalog with item_id {} deleted successfully", itemId);
        } catch (SQLException e) {
            logger.error("Error deleting catalog with item_id {}: {}", itemId, e.getMessage(), e);
            throw new RuntimeException("Error deleting catalog: " + e.getMessage(), e);
        }
    }

    @Override
    public CatalogDTO getCatalogById(Integer itemId) {
        String sql = "SELECT item_id, name, description, rate, availability, category FROM catalog WHERE item_id = ?";
        CatalogDTO catalog = null;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                catalog = new CatalogDTO();
                catalog.setItemId(rs.getInt("item_id"));
                catalog.setName(rs.getString("name"));
                catalog.setDescription(rs.getString("description"));
                catalog.setRate(rs.getFloat("rate")); // Changed to getFloat
                catalog.setAvailability(rs.getBoolean("availability"));
                catalog.setCategory(rs.getString("category"));
                logger.info("Retrieved catalog with item_id: {}", itemId);
            } else {
                logger.warn("No catalog found with item_id: {}", itemId);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving catalog by item_id {}: {}", itemId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving catalog by item_id: " + e.getMessage(), e);
        }
        return catalog;
    }
    
    @Override
    public SupplierDTO getSupplierById(Integer supplierId) {
        String sql = "SELECT * FROM supplier WHERE supplier_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SupplierDTO.class), supplierId);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("No supplier found with supplier_id: {}", supplierId);
            return null;
        } catch (Exception e) {
            logger.error("Error retrieving supplier by supplier_id {}: {}", supplierId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving supplier by supplier_id: " + e.getMessage(), e);
        }
    }
}