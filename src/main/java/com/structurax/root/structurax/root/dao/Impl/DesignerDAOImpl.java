package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.DesignerDAO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class DesignerDAOImpl implements DesignerDAO {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Override
    public DesignFullDTO getDesignById(String id) {
        final String sql = """
            SELECT 
                d.design_id,
                d.project_id,
                d.name,
                d.type,
                d.due_date,
                d.priority,
                d.price,
                d.design_link,
                d.description,
                d.additional_note,
                d.status,
                d.client_id,
                d.employee_id,
                u.name as client_name
            FROM design d
            LEFT JOIN client c ON d.client_id = c.client_id
            LEFT JOIN users u ON c.user_id = u.user_id
            WHERE d.design_id = ?
        """;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToDesignFullDTO(resultSet);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching design by ID: " + e.getMessage(), e);
        } finally {
            closeResources(resultSet, preparedStatement, connection);
        }
    }

    @Override
    public List<DesignFullDTO> getAllDesigns() {
        final List<DesignFullDTO> designList = new ArrayList<>();
        final String sql = """
            SELECT 
                d.design_id,
                d.project_id,
                d.name,
                d.type,
                d.due_date,
                d.priority,
                d.price,
                d.design_link,
                d.description,
                d.additional_note,
                d.status,
                d.client_id,
                d.employee_id,
                u.name as client_name
            FROM design d
            LEFT JOIN client c ON d.client_id = c.client_id
            LEFT JOIN users u ON c.user_id = u.user_id
            ORDER BY d.design_id ASC
        """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                designList.add(mapResultSetToDesignFullDTO(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all designs: " + e.getMessage(), e);
        }

        return designList;
    }

    @Override
    public DesignDTO deleteDesign(String id) {
        final String selectSql = """
            SELECT 
                design_id, project_id, name, type, due_date, priority, 
                price, design_link, description, additional_note, status, 
                client_id, employee_id
            FROM design
            WHERE design_id = ?
        """;

        final String deleteSql = "DELETE FROM design WHERE design_id = ?";

        Connection connection = null;
        PreparedStatement selectStatement = null;
        PreparedStatement deleteStatement = null;
        ResultSet resultSet = null;
        DesignDTO designDTO = null;

        try {
            connection = databaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Fetch design before deletion
            selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setString(1, id);
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                designDTO = mapResultSetToDesignDTO(resultSet);
            }

            // Delete design
            deleteStatement = connection.prepareStatement(deleteSql);
            deleteStatement.setString(1, id);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected == 0) {
                connection.rollback();
                throw new RuntimeException("Design not found or already deleted: " + id);
            }

            connection.commit();
            return designDTO;

        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Error during rollback: " + rollbackEx.getMessage(), rollbackEx);
            }
            throw new RuntimeException("Error deleting design: " + e.getMessage(), e);
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeResources(resultSet, selectStatement, deleteStatement, connection);
        }
    }

    @Override
    public DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign) {
        final String sql = """
            UPDATE design SET
                project_id = ?,
                name = ?,
                type = ?,
                due_date = ?,
                priority = ?,
                price = ?,
                design_link = ?,
                description = ?,
                additional_note = ?,
                status = ?,
                client_id = ?,
                employee_id = ?
            WHERE design_id = ?
        """;

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, updatedDesign.getProjectId());
            preparedStatement.setString(2, updatedDesign.getName());
            preparedStatement.setString(3, updatedDesign.getType());
            preparedStatement.setDate(4, updatedDesign.getDueDate() != null ?
                    new java.sql.Date(updatedDesign.getDueDate().getTime()) : null);
            preparedStatement.setString(5, updatedDesign.getPriority());
            preparedStatement.setBigDecimal(6, updatedDesign.getPrice());
            preparedStatement.setString(7, updatedDesign.getDesignLink());
            preparedStatement.setString(8, updatedDesign.getDescription());
            preparedStatement.setString(9, updatedDesign.getAdditionalNote());
            preparedStatement.setString(10, updatedDesign.getStatus());
            preparedStatement.setString(11, updatedDesign.getClientId());
            preparedStatement.setString(12, updatedDesign.getEmployeeId());
            preparedStatement.setString(13, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Design not found for update: " + id);
            }

            return getDesignById(id);

        } catch (SQLException e) {
            throw new RuntimeException("Error updating design: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }
    }

    @Override
    public List<ClientDTO> getClientsWithoutPlan() {
        final List<ClientDTO> clientList = new ArrayList<>();
        final String sql = """
            SELECT 
                c.client_id,
                u.name,
                u.email,
                u.phone_number,
                u.address,
                c.type,
                c.is_have_plan
            FROM client c
            INNER JOIN users u ON c.user_id = u.user_id
            WHERE u.type = 'Client' AND c.is_have_plan = 0
            ORDER BY c.client_id ASC
        """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                ClientDTO client = new ClientDTO();
                client.setClientId(resultSet.getString("client_id"));
                client.setFirstName(resultSet.getString("name"));
                client.setEmail(resultSet.getString("email"));
                client.setContactNumber(resultSet.getString("phone_number"));
                client.setAddress(resultSet.getString("address"));
                client.setType(resultSet.getString("type"));
                client.setIsHavePlan(resultSet.getBoolean("is_have_plan"));

                clientList.add(client);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching clients without plan: " + e.getMessage(), e);
        }

        return clientList;
    }

    @Override
    public DesignDTO initializingDesign(DesignDTO designDTO) {
        // Don't include status in the INSERT - let database use DEFAULT value
        final String sql = """
        INSERT INTO design (project_id, name, type, due_date, priority, 
                          price, design_link, description, additional_note, 
                          client_id, employee_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, designDTO.getProjectId());
            preparedStatement.setString(2, designDTO.getName());
            preparedStatement.setString(3, designDTO.getType());
            preparedStatement.setDate(4, designDTO.getDueDate() != null ?
                    new java.sql.Date(designDTO.getDueDate().getTime()) : null);
            preparedStatement.setString(5, designDTO.getPriority());
            preparedStatement.setBigDecimal(6, designDTO.getPrice());
            preparedStatement.setString(7, designDTO.getDesignLink());
            preparedStatement.setString(8, designDTO.getDescription());
            preparedStatement.setString(9, designDTO.getAdditionalNote());
            preparedStatement.setString(10, designDTO.getClientId());
            preparedStatement.setString(11, designDTO.getEmployeeId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to create design");
            }

            // Get generated design_id
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                designDTO.setDesignId(generatedKeys.getString(1));
            }

            return designDTO;

        } catch (SQLException e) {
            throw new RuntimeException("Error creating design: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }
    }

    // Helper Methods

    private DesignFullDTO mapResultSetToDesignFullDTO(ResultSet resultSet) throws SQLException {
        DesignFullDTO design = new DesignFullDTO();
        design.setDesignId(resultSet.getString("design_id"));
        design.setProjectId(resultSet.getString("project_id"));
        design.setName(resultSet.getString("name"));
        design.setType(resultSet.getString("type"));

        Date dueDate = resultSet.getDate("due_date");
        if (dueDate != null) {
            design.setDueDate(new java.util.Date(dueDate.getTime()));
        }

        design.setPriority(resultSet.getString("priority"));
        design.setPrice(resultSet.getBigDecimal("price"));
        design.setDesignLink(resultSet.getString("design_link"));
        design.setDescription(resultSet.getString("description"));
        design.setAdditionalNote(resultSet.getString("additional_note"));
        design.setStatus(resultSet.getString("status"));
        design.setClientId(resultSet.getString("client_id"));
        design.setEmployeeId(resultSet.getString("employee_id"));
        design.setClientFullName(resultSet.getString("client_name"));

        return design;
    }

    private DesignDTO mapResultSetToDesignDTO(ResultSet resultSet) throws SQLException {
        DesignDTO design = new DesignDTO();
        design.setDesignId(resultSet.getString("design_id"));
        design.setProjectId(resultSet.getString("project_id"));
        design.setName(resultSet.getString("name"));
        design.setType(resultSet.getString("type"));

        Date dueDate = resultSet.getDate("due_date");
        if (dueDate != null) {
            design.setDueDate(new java.util.Date(dueDate.getTime()));
        }

        design.setPriority(resultSet.getString("priority"));
        design.setPrice(resultSet.getBigDecimal("price"));
        design.setDesignLink(resultSet.getString("design_link"));
        design.setDescription(resultSet.getString("description"));
        design.setAdditionalNote(resultSet.getString("additional_note"));
        design.setStatus(resultSet.getString("status"));
        design.setClientId(resultSet.getString("client_id"));
        design.setEmployeeId(resultSet.getString("employee_id"));

        return design;
    }

    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}