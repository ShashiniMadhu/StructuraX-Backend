package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.DirectorDAO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.ProjectInitiateDTO;
import com.structurax.root.structurax.root.dto.ProjectStartDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DirectorDAOImpl implements DirectorDAO {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Override
    public ClientDTO createClient(ClientDTO clientDTO,String otp) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement checkStmt = null;
        //BCrypt encoder


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(otp);

        if(clientDTO.getClientId() == null || clientDTO.getClientId().trim().isEmpty()){
            clientDTO.setClientId(generateClinetId());
        }

        try{
            connection = databaseConnection.getConnection();
            final String checkSql = "SELECT COUNT(*) FROM client WHERE email = ?";
            checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, clientDTO.getEmail());
            ResultSet resultSet = checkStmt.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) > 0){
                throw new RuntimeException("Client already exists");

            }
            final String sql = "INSERT INTO client (client_id,first_name,last_name,email,password,contact_number,type,is_have_plan,address)" +
                    "VALUES (?,?,?,?,?,?,?,?,?)";

            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, clientDTO.getClientId());
            preparedStatement.setString(2, sanitize(clientDTO.getFirstName()));
            preparedStatement.setString(3, sanitize(clientDTO.getLastName()));
            preparedStatement.setString(4, sanitize(clientDTO.getEmail()));
            preparedStatement.setString(5, hashedPassword);

            preparedStatement.setString(6, sanitize(clientDTO.getContactNumber()));
            preparedStatement.setString(7, sanitize(clientDTO.getType()));
            preparedStatement.setBoolean(8, clientDTO.isHavePlan());
            preparedStatement.setString(9, sanitize(clientDTO.getAddress()));

            int rowAffected = preparedStatement.executeUpdate();
            if(rowAffected == 0){
                throw new RuntimeException("Failed to create client");
            }
        }catch (SQLException e){
            throw new RuntimeException("Error inserting client:  " + e.getMessage(),e);

        }finally {
            closeResources(preparedStatement,connection,checkStmt);
        }

        return clientDTO;
    }

    @Override
    public List<ClientDTO> getClientWithPlan() {
        final List<ClientDTO> clientList = new ArrayList<>();
        final String sql = "SELECT c.* , d.design_link FROM client c LEFT JOIN design d ON c.client_id = d.client_id WHERE c.is_have_plan = 1";
        try(
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

                ){
            while (resultSet.next()){
                ClientDTO client = new ClientDTO(
                        resultSet.getString("client_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        null,
                        resultSet.getString("contact_number"),
                        resultSet.getString("type"),
                        resultSet.getBoolean("is_have_plan"),
                        resultSet.getString("address"),
                        resultSet.getString("design_link")
                );
                clientList.add(client);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error fecting employee: " + e.getMessage(),e);
        }
        return clientList;
    }

    @Override
    public List<ClientDTO> getClientWithoutPlan() {
        final List<ClientDTO> clientList = new ArrayList<>();
        final String sql = "SELECT * FROM client WHERE is_have_plan = 0";
        try(
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

        ){
            while (resultSet.next()){
                ClientDTO client = new ClientDTO(
                        resultSet.getString("client_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        null,
                        resultSet.getString("contact_number"),
                        resultSet.getString("type"),
                        resultSet.getBoolean("is_have_plan"),
                        resultSet.getString("address"),
                        null

                );
                clientList.add(client);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error fecting employee: " + e.getMessage(),e);
        }
        return clientList;
    }

    @Override
    public ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        if(projectInitiateDTO.getProjectId() == null || projectInitiateDTO.getProjectId().trim().isEmpty()){
            projectInitiateDTO.setProjectId(generateProjectId());
        }

        try{
            final String sql = "INSERT INTO project (project_id, name, status, budget, description, location, estimated_value, start_date, due_date, client_id, qs_id, pm_id,ss_id,category)"+
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, projectInitiateDTO.getProjectId());
            preparedStatement.setString(2, sanitize(projectInitiateDTO.getName()));
            preparedStatement.setString(3, "pending");
            preparedStatement.setBigDecimal(4, sanitizeForDecimal(projectInitiateDTO.getBudget()));
            preparedStatement.setString(5, sanitize(projectInitiateDTO.getDescription()));
            preparedStatement.setString(6, sanitize(projectInitiateDTO.getLocation()));
            preparedStatement.setBigDecimal(7, sanitizeForDecimal(projectInitiateDTO.getEstimatedValue()));
            preparedStatement.setDate(8, java.sql.Date.valueOf(sanitizeForDate(projectInitiateDTO.getStartDate())));
            preparedStatement.setDate(9, java.sql.Date.valueOf(sanitizeForDate(projectInitiateDTO.getDueDate())));
            preparedStatement.setString(10, projectInitiateDTO.getClientId());
            preparedStatement.setString(11, projectInitiateDTO.getQsId());
            preparedStatement.setString(12, projectInitiateDTO.getPmId());
            preparedStatement.setString(13, projectInitiateDTO.getSsId());
            preparedStatement.setString(14,sanitizeForCategory(projectInitiateDTO.getCategory()));

            int rowAffected = preparedStatement.executeUpdate();
            if(rowAffected == 0){
                throw new RuntimeException("Failed to initialize project: ");
            }


        projectInitiateDTO.setStatus("pending");
        }catch (SQLException e){
            throw new RuntimeException("Error inserting project: " + e.getMessage(),e);
        }finally {
            closeResources(preparedStatement,connection);
        }
        return projectInitiateDTO;
    }

    @Override
    public List<ProjectInitiateDTO> getAllProjects() {
        Map<String, ProjectInitiateDTO> projectMap = new LinkedHashMap<>();
        final String sql = "SELECT p.*, pi.image_url, c.first_name " +
                "FROM project p " +
                "LEFT JOIN project_images pi ON p.project_id = pi.project_id " +
                "LEFT JOIN client c ON p.client_id = c.client_id";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                String projectId = resultSet.getString("project_id");

                ProjectInitiateDTO project = projectMap.get(projectId);
                if (project == null) {
                    // Initialize image list
                    List<String> imageUrls = new ArrayList<>();

                    project = new ProjectInitiateDTO(
                            projectId,
                            resultSet.getString("name"),
                            resultSet.getString("status"),
                            resultSet.getBigDecimal("budget"),
                            resultSet.getString("description"),
                            resultSet.getString("location"),
                            resultSet.getBigDecimal("estimated_value"),
                            resultSet.getDate("start_date").toLocalDate(),
                            resultSet.getDate("due_date").toLocalDate(),
                            resultSet.getString("client_id"),
                            resultSet.getString("qs_id"),
                            resultSet.getString("pm_id"),
                            resultSet.getString("ss_id"),
                            resultSet.getString("category"),
                            imageUrls,
                            resultSet.getString("first_name")
                    );

                    projectMap.put(projectId, project);
                }

                String imageUrl = resultSet.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    project.getImage_url().add(imageUrl);  // Add to the list
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting projects: " + e.getMessage(), e);
        }

        return new ArrayList<>(projectMap.values());
    }


    @Override
    public ProjectInitiateDTO getProjectById(String id) {

        final String sql = "SELECT p.*,pi.image_url,c.first_name FROM  project p LEFT JOIN project_images pi ON p.project_id = pi.project_id LEFT JOIN client c ON p.client_id=c.client_id WHERE p.project_id = ?" ;
        try(

                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);


                ){
            preparedStatement.setString(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            ProjectInitiateDTO project = null;
            List<String> imageUrls = new ArrayList<>();
            while (resultSet.next()){
                if(project==null){
                    project = new ProjectInitiateDTO(
                            resultSet.getString("project_id"),
                            resultSet.getString("name"),
                            resultSet.getString("status"),
                            resultSet.getBigDecimal("budget"),
                            resultSet.getString("description"),
                            resultSet.getString("location"),
                            resultSet.getBigDecimal("estimated_value"),
                            resultSet.getDate("start_date").toLocalDate(),
                            resultSet.getDate("due_date").toLocalDate(),
                            resultSet.getString("client_id"),
                            resultSet.getString("qs_id"),
                            resultSet.getString("pm_id"),
                            resultSet.getString("ss_id"),
                            resultSet.getString("category"),
                            imageUrls,
                            resultSet.getString("first_name")

                    );
                }
                String imageUrl = resultSet.getString("image_url");
                if(imageUrl != null ){
                    imageUrls.add(imageUrl);
                }
            }
            return  project;

        }catch (SQLException e){
            throw new RuntimeException("Error getting project: " + e.getMessage(),e);
        }

    }

    @Override
    public List<ProjectInitiateDTO> getPendingProjects() {
        Map<String, ProjectInitiateDTO> projectMap = new LinkedHashMap<>();
        final String sql = "SELECT p.*, pi.image_url, c.first_name " +
                "FROM project p " +
                "LEFT JOIN project_images pi ON p.project_id = pi.project_id " +
                "LEFT JOIN client c ON p.client_id = c.client_id " +
                "WHERE p.status = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, "pending");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String projectId = resultSet.getString("project_id");

                ProjectInitiateDTO project = projectMap.get(projectId);
                if (project == null) {
                    List<String> imageUrls = new ArrayList<>();

                    project = new ProjectInitiateDTO(
                            projectId,
                            resultSet.getString("name"),
                            resultSet.getString("status"),
                            resultSet.getBigDecimal("budget"),
                            resultSet.getString("description"),
                            resultSet.getString("location"),
                            resultSet.getBigDecimal("estimated_value"),
                            resultSet.getDate("start_date").toLocalDate(),
                            resultSet.getDate("due_date").toLocalDate(),
                            resultSet.getString("client_id"),
                            resultSet.getString("qs_id"),
                            resultSet.getString("pm_id"),
                            resultSet.getString("ss_id"),
                            resultSet.getString("category"),
                            imageUrls,
                            resultSet.getString("first_name")
                    );

                    projectMap.put(projectId, project);
                }

                String imageUrl = resultSet.getString("image_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    project.getImage_url().add(imageUrl);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting pending projects: " + e.getMessage(), e);
        }

        return new ArrayList<>(projectMap.values());
    }


    @Override
    public void startProject(String projectId, ProjectStartDTO projectStartDTO) throws SQLException {
        Connection connection = null;
        PreparedStatement projectStmt = null;
        PreparedStatement updateEmpStmt = null;
        final String projectSql = "UPDATE project SET qs_id = ?, pm_id = ?, ss_id = ? , status = ? WHERE project_id = ? ";
        final String updateEmpSql = "UPDATE employee SET availability = 'Assigned' WHERE employee_id = ?";

        try{
            connection = databaseConnection.getConnection();
            projectStmt = connection.prepareStatement(projectSql);
            projectStmt.setString(1,sanitize(projectStartDTO.getQsId()));
            projectStmt.setString(2,sanitize(projectStartDTO.getPmId()));
            projectStmt.setString(3,sanitize(projectStartDTO.getSsId()));
            projectStmt.setString(4,sanitize(projectStartDTO.getStatus()));
            projectStmt.setString(5,projectId);
            int rowUpdated = projectStmt.executeUpdate();
            if(rowUpdated == 0){
                throw new RuntimeException("Error Editing: " );
            }

            updateEmpStmt = connection.prepareStatement(updateEmpSql);
            updateEmpStmt.setString(1,sanitize(projectStartDTO.getQsId()));
            updateEmpStmt.executeUpdate();

            updateEmpStmt.setString(1,sanitize(projectStartDTO.getPmId()));
            updateEmpStmt.executeUpdate();

            updateEmpStmt.setString(1,sanitize(projectStartDTO.getSsId()));
            updateEmpStmt.executeUpdate();


        }catch (SQLException e){
            if(connection != null){
                try{
                    connection.rollback();

                }catch(SQLException rollbackEx){
                    throw new RuntimeException("Error Rollback Connection: " + e.getMessage(),rollbackEx);
                }
            }
            throw new RuntimeException("Error starting project: " + e.getMessage(),e);
        }finally {
            closeResources(connection,projectStmt,updateEmpStmt);
        }
    }

    private String sanitizeForCategory(String category) {
        return (category == null || category.trim().isEmpty()) ? "residential" : category.trim();
    }

    private LocalDate sanitizeForDate(LocalDate value) {
        return (value == null) ? LocalDate.now() : value;
    }

    private BigDecimal sanitizeForDecimal(BigDecimal value) {
        return (value == null) ? new BigDecimal("000.00") : value;
    }


    private String generateProjectId() {
        String sql = "SELECT project_id FROM project ORDER BY project_id DESC LIMIT 1";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
        ){
            if(resultSet.next()){
                String lastId = resultSet.getString("project_id");
                String numberPart = lastId.substring(4);
                int nextNumber = Integer.parseInt(numberPart) + 1;
                return String.format("PRJ_%03d", nextNumber);
            }else{
                return "PRJ_001";
            }
        }catch (SQLException e){
            throw new RuntimeException("Error generating Project Id :"+e.getMessage(), e);
        }

    }

    //Helper method
    private String sanitize(String value) {
        return (value == null || value.trim().isEmpty()) ? "none" : value.trim();
    }

    //Helper method
    private void closeResources(AutoCloseable... resources)
    {
       for (AutoCloseable resource : resources){
            if(resource != null){
                try{
                    resource.close();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    //helper method to create client id
    private String generateClinetId() {
        String sql = "SELECT client_id FROM client ORDER BY client_id DESC LIMIT 1";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
                ){
            if(resultSet.next()){
                String lastId = resultSet.getString("client_id");
                String numberPart = lastId.substring(4);
                int nextNumber = Integer.parseInt(numberPart) + 1;
                return String.format("CLI_%03d", nextNumber);
            }else{
                return "CLI_001";
            }
        }catch (SQLException e){
            throw new RuntimeException("Error generating Clinet ID :"+e.getMessage(), e);
        }

    }


}
