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
import java.util.List;

@Repository
public class DirectorDAOImpl implements DirectorDAO {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Override
    public ClientDTO createClient(ClientDTO clientDTO,String otp) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        //BCrypt encoder


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(otp);

        if(clientDTO.getClientId() == null || clientDTO.getClientId().trim().isEmpty()){
            clientDTO.setClientId(generateClinetId());
        }

        try{
            final String sql = "INSERT INTO client (client_id,first_name,last_name,email,password,contact_number,type,is_have_plan,address)" +
                    "VALUES (?,?,?,?,?,?,?,?,?)";
            connection = databaseConnection.getConnection();
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
            closeResources(preparedStatement,connection);
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
            final String sql = "INSERT INTO project (project_id, name, status, budget, description, location, estimated_value, start_date, due_date, client_id, qs_id, pm_id,category)"+
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, projectInitiateDTO.getProjectId());
            preparedStatement.setString(2, sanitize(projectInitiateDTO.getName()));
            preparedStatement.setString(3, "ongoing");
            preparedStatement.setBigDecimal(4, sanitizeForDecimal(projectInitiateDTO.getBudget()));
            preparedStatement.setString(5, sanitize(projectInitiateDTO.getDescription()));
            preparedStatement.setString(6, sanitize(projectInitiateDTO.getLocation()));
            preparedStatement.setBigDecimal(7, sanitizeForDecimal(projectInitiateDTO.getEstimatedValue()));
            preparedStatement.setDate(8, java.sql.Date.valueOf(sanitizeForDate(projectInitiateDTO.getStartDate())));
            preparedStatement.setDate(9, java.sql.Date.valueOf(sanitizeForDate(projectInitiateDTO.getDueDate())));
            preparedStatement.setString(10, sanitize(projectInitiateDTO.getClientId()));
            preparedStatement.setString(11, sanitize(projectInitiateDTO.getQsId()));
            preparedStatement.setString(12, sanitize(projectInitiateDTO.getPmId()));
            preparedStatement.setString(13, sanitizeForCategory(projectInitiateDTO.getCategory()));

            int rowAffected = preparedStatement.executeUpdate();
            if(rowAffected == 0){
                throw new RuntimeException("Failed to initialize project: ");
            }



        }catch (SQLException e){
            throw new RuntimeException("Error inserting project: " + e.getMessage(),e);
        }finally {
            closeResources(preparedStatement,connection);
        }
        return projectInitiateDTO;
    }

    @Override
    public List<ProjectInitiateDTO> getAllProjects() {
        List<ProjectInitiateDTO> projectList =  new ArrayList<>();
        final String sql = "SELECT p.*,pi.image_url FROM  project p LEFT JOIN project_images pi ON p.project_id = pi.project_id";

        try(
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();

                ){
            while (resultSet.next()){
                ProjectInitiateDTO project = new ProjectInitiateDTO(
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
                        resultSet.getString("category"),
                        resultSet.getString("image_url")

                );
                projectList.add(project);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error getting projects: " + e.getMessage(),e);
        }

        return  projectList;
    }

    @Override
    public ProjectInitiateDTO getProjectById(String id) {

        final String sql = "SELECT p.*,pi.image_url FROM  project p LEFT JOIN project_images pi ON p.project_id = pi.project_id WHERE p.project_id = ?" ;
        try(

                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);


                ){
            preparedStatement.setString(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return new ProjectInitiateDTO(
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
                        resultSet.getString("category"),
                        resultSet.getString("image_url")
                );
            }else {
                return null;
            }
        }catch (SQLException e){
            throw new RuntimeException("Error getting project: " + e.getMessage(),e);
        }

    }

    @Override
    public List<ProjectInitiateDTO> getPendingProjects() {
        List<ProjectInitiateDTO> projectList =  new ArrayList<>();
        final String sql = "SELECT p.*,pi.image_url FROM  project p LEFT JOIN project_images pi ON p.project_id = pi.project_id WHERE p.status = ? " ;
        try(

                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);


        ){
            preparedStatement.setString(1,"pending");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ProjectInitiateDTO project = new ProjectInitiateDTO(
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
                        resultSet.getString("category"),
                        resultSet.getString("image_url")

                );
                projectList.add(project);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error getting projects: " + e.getMessage(),e);
        }
        return projectList;
    }

    @Override
    public void startProject(String projectId, ProjectStartDTO projectStartDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        final String sql = "UPDATE project SET qs_id = ?, pm_id = ?, status = ? WHERE project_id = ? ";

        try{
            connection = databaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,sanitize(projectStartDTO.getQsId()));
            preparedStatement.setString(2,sanitize(projectStartDTO.getPmId()));
            preparedStatement.setString(3,sanitize(projectStartDTO.getStatus()));
            preparedStatement.setString(4,projectId);
            int rowUpdated = preparedStatement.executeUpdate();
            if(rowUpdated == 0){
                throw new RuntimeException("Error Editing: " );
            }

        }catch (SQLException e){
            throw new RuntimeException("Error starting project: " + e.getMessage(),e);
        }finally {
            closeResources(connection,preparedStatement);
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
