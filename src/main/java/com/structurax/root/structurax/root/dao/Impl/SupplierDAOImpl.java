package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.SupplierDAO;
import com.structurax.root.structurax.root.dto.CatalogDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SupplierDAOImpl implements SupplierDAO {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Override
    public CatalogDTO createCatalog(CatalogDTO catalogDTO) {
        String sql = "INSERT INTO catalog(name, description, active) VALUES (?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, catalogDTO.getName());
            ps.setString(2, catalogDTO.getDescription());
            ps.setBoolean(3, catalogDTO.getActive());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                catalogDTO.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating catalog", e);
        }
        return catalogDTO;
    }

    @Override
    public List<CatalogDTO> getAllCatalogs() {
        String sql = "SELECT * FROM catalog WHERE active = true";
        List<CatalogDTO> catalogs = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CatalogDTO catalog = new CatalogDTO();
                catalog.setId(rs.getInt("id"));
                catalog.setName(rs.getString("name"));
                catalog.setDescription(rs.getString("description"));
                catalog.setActive(rs.getBoolean("active"));
                catalogs.add(catalog);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving catalogs", e);
        }
        return catalogs;
    }

    @Override
    public void deactivateCatalog(Integer catalogId) {
        String sql = "UPDATE catalog SET active = false WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, catalogId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deactivating catalog", e);
        }
    }

    @Override
    public CatalogDTO getCatalogById(Integer id) {
        String sql = "SELECT * FROM catalog WHERE id = ?";
        CatalogDTO catalog = null;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                catalog = new CatalogDTO();
                catalog.setId(rs.getInt("id"));
                catalog.setName(rs.getString("name"));
                catalog.setDescription(rs.getString("description"));
                catalog.setActive(rs.getBoolean("active"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving catalog by id", e);
        }
        return catalog;
    }
}