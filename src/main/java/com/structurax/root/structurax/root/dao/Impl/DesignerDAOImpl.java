package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.DesignerDAO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import com.structurax.root.structurax.root.service.Impl.AdminServiceImpl;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DesignerDAOImpl implements DesignerDAO {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private DatabaseConnection databaseConnection;

    // Helper method to close resources
    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<DesignFullDTO> getAllDesigns() {
        final List<DesignFullDTO> designList = new ArrayList<>();

        // Updated SQL query with concatenated client name
        final String sql = """
                SELECT d.design_id, d.project_id, d.name, d.type, d.due_date, d.priority, d.price, d.design_link, d.description, d.additional_note, d.status, d.client_id, d.employee_id,
                    CONCAT(IFNULL(c.first_name, ''), ' ', IFNULL(c.last_name, '')) AS client_name
                FROM design d
                LEFT JOIN client c ON d.client_id = c.client_id
                ORDER BY d.design_id
                """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                String clientName = resultSet.getString("client_name");

                // Clean up the client name (remove extra spaces)
                if (clientName != null) {
                    clientName = clientName.trim();
                    if (clientName.isEmpty()) {
                        clientName = null;
                    }
                }

                DesignFullDTO design = new DesignFullDTO(
                        resultSet.getString("design_id"),
                        resultSet.getString("project_id"),
                        resultSet.getString("name"),
                        resultSet.getString("type"),
                        resultSet.getDate("due_date"),
                        resultSet.getString("priority"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getString("design_link"),
                        resultSet.getString("description"),
                        resultSet.getString("additional_note"),
                        resultSet.getString("status"),
                        resultSet.getString("client_id"),
                        resultSet.getString("employee_id"),
                        resultSet.getString("client_name")
                );
                designList.add(design);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all designs with client information: " + e.getMessage(), e);
        }

        return designList;
    }

    @Override
    public DesignDTO deleteDesign(String id) {
        String deleteQuery = "DELETE FROM design WHERE design_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setString(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Design not found with ID: " + id);
            }

            // Return minimal DTO with deleted ID and success message in description
            DesignDTO result = new DesignDTO();
            result.setDesignId(id);
            result.setDescription("Design deleted successfully");
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting design: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }
    }

    @Override
    public DesignFullDTO updateDesign(String id, DesignFullDTO updatedDesign) {
        // First check if design exists
        DesignFullDTO existingDesign = getDesignById(id);
        if (existingDesign == null) {
            throw new RuntimeException("Design not found with ID: " + id);
        }

        // Update query
        String updateQuery = """
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
            preparedStatement = connection.prepareStatement(updateQuery);

            // Set parameters
            preparedStatement.setString(1, updatedDesign.getProjectId());
            preparedStatement.setString(2, updatedDesign.getName());
            preparedStatement.setString(3, updatedDesign.getType());
            preparedStatement.setDate(4, updatedDesign.getDueDate() != null ? new java.sql.Date(updatedDesign.getDueDate().getTime()) : null);
            preparedStatement.setString(5, updatedDesign.getPriority());
            preparedStatement.setBigDecimal(6, updatedDesign.getPrice());
            preparedStatement.setString(7, updatedDesign.getDesignLink());
            preparedStatement.setString(8, updatedDesign.getDescription());
            preparedStatement.setString(9, updatedDesign.getAdditionalNote());
            preparedStatement.setString(10, updatedDesign.getStatus());
            preparedStatement.setString(11, updatedDesign.getClientId());
            preparedStatement.setString(12, updatedDesign.getEmployeeId());
            preparedStatement.setString(13, id); // WHERE clause

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were updated. Design might not exist with ID: " + id);
            }

            // Return the updated design
            return getDesignById(id);

        } catch (SQLException e) {
            throw new RuntimeException("Error updating design: " + e.getMessage(), e);
        } finally {
            closeResources(preparedStatement, connection);
        }
    }

    @Override
    // Helper method to get design by ID (if you don't have this already)
    public DesignFullDTO getDesignById(String id) {
        final String sql = """
                SELECT d.design_id, d.project_id, d.name, d.type, d.due_date, d.priority, d.price, d.design_link, d.description, d.additional_note, d.status, d.client_id, d.employee_id,
                    CONCAT(IFNULL(c.first_name, ''), ' ', IFNULL(c.last_name, '')) AS client_name
                FROM design d
                LEFT JOIN client c ON d.client_id = c.client_id
                WHERE d.design_id = ?
                """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String clientName = resultSet.getString("client_name");

                    // Clean up the client name (remove extra spaces)
                    if (clientName != null) {
                        clientName = clientName.trim();
                        if (clientName.isEmpty()) {
                            clientName = null;
                        }
                    }

                    return new DesignFullDTO(
                            resultSet.getString("design_id"),
                            resultSet.getString("project_id"),
                            resultSet.getString("name"),
                            resultSet.getString("type"),
                            resultSet.getDate("due_date"),
                            resultSet.getString("priority"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getString("design_link"),
                            resultSet.getString("description"),
                            resultSet.getString("additional_note"),
                            resultSet.getString("status"),
                            resultSet.getString("client_id"),
                            resultSet.getString("employee_id"),
                            clientName
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching design: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<ClientDTO> getClientsWithoutPlan() {
        final List<ClientDTO> clientList = new ArrayList<>();

        final String sql = """
        SELECT client_id, first_name, last_name, email, password, contact_number, type, is_have_plan, address,
               CONCAT(IFNULL(first_name, ''), ' ', IFNULL(last_name, '')) AS client_name
        FROM client
        WHERE is_have_plan = 0
    """;

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                ClientDTO client = new ClientDTO();
                client.setClientId(resultSet.getString("client_id"));
                client.setFirstName(resultSet.getString("first_name"));
                client.setLastName(resultSet.getString("last_name"));
                client.setEmail(resultSet.getString("email"));
                client.setPassword(resultSet.getString("password"));
                client.setContactNumber(resultSet.getString("contact_number"));
                client.setType(resultSet.getString("type"));
                client.setIsHavePlan(resultSet.getBoolean("is_have_plan"));
                client.setAddress(resultSet.getString("address"));

                clientList.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // You can replace this with a proper logging mechanism
        }

        return null;
    }

    @Override
    public DesignDTO initializingDesign(DesignDTO designDTO) {
        String sql = """
        INSERT INTO design (project_id, name, type, due_date, priority, price, 
                           design_link, description, additional_note, status, client_id, employee_id)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Generate unique project_id
            String projectId = "PRJ_003";

            // Set default values
            String status = "ongoing";
            String employeeId = "EMP_001"; // Replace with actual employee ID logic

            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);

            // Set parameters (design_id is auto-generated by trigger)
            preparedStatement.setString(1, projectId);
            preparedStatement.setString(2, designDTO.getName());
            preparedStatement.setString(3, designDTO.getType());
            preparedStatement.setDate(4, designDTO.getDueDate() != null ? new java.sql.Date(designDTO.getDueDate().getTime()) : null);
            preparedStatement.setString(5, designDTO.getPriority());
            preparedStatement.setBigDecimal(6, designDTO.getPrice());
            preparedStatement.setString(7, designDTO.getDesignLink());
            preparedStatement.setString(8, designDTO.getDescription());
            preparedStatement.setString(9, designDTO.getAdditionalNote());
            preparedStatement.setString(10, status);
            preparedStatement.setString(11, designDTO.getClientId());
            preparedStatement.setString(12, employeeId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Set the generated values back to DTO
                designDTO.setProjectId(projectId);
                designDTO.setStatus(status);
                designDTO.setEmployeeId(employeeId);
                // Note: design_id will be auto-generated by database trigger

                logger.info("Design initialized successfully");
                return designDTO;
            } else {
                throw new RuntimeException("Failed to initialize design");
            }

        } catch (SQLException e) {
            logger.error("Error initializing design: {}", e.getMessage(), e);
            throw new RuntimeException("Database error while initializing design: " + e.getMessage());
        } finally {
            closeResources(preparedStatement, connection);
        }
    }
}