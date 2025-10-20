package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.DirectorDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.MailService;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import com.structurax.root.structurax.root.util.OtpUtil;
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

    @Autowired
    private MailService mailService;

    @Override
    public ClientOneDTO createClient(ClientOneDTO clientOneDTO, String otp) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        PreparedStatement checkStmt = null;

        //BCrypt encoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(otp);

        try{
            connection = databaseConnection.getConnection();
            final String checkSql = "SELECT COUNT(*) FROM users WHERE email = ?";
            checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, clientOneDTO.getEmail());
            ResultSet resultSet = checkStmt.executeQuery();
            if(resultSet.next() && resultSet.getInt(1) > 0){
                throw new RuntimeException("Client already exists");

            }
            final String userSql = "INSERT INTO users (name, email, phone_number, address, type, password) VALUES (?, ?, ?, ?, 'Client', ?)";

            preparedStatement = connection.prepareStatement(userSql,Statement.RETURN_GENERATED_KEYS);


            preparedStatement.setString(1, clientOneDTO.getName());
            preparedStatement.setString(2, clientOneDTO.getEmail());
            preparedStatement.setString(3, clientOneDTO.getPhoneNumber());
            preparedStatement.setString(4, clientOneDTO.getAddress());
            preparedStatement.setString(5, hashedPassword);

            int rowAffected = preparedStatement.executeUpdate();
            if(rowAffected == 0){
                throw new RuntimeException("Failed to create client");
            }
            //get the generated user_id
            ResultSet resultSet2 = preparedStatement.getGeneratedKeys();
            int userId = 0;
            if(resultSet2.next()){
                userId = resultSet2.getInt(1);

            }else{
                throw new RuntimeException("Failed to create client");
            }
            preparedStatement.close();

            //insert into client table with cliendDTO fields
            final String clientSql = "UPDATE client " +
                    "SET type = ?, is_have_plan = ? " +
                    "WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(clientSql);
            preparedStatement.setString(1, clientOneDTO.getType());
            preparedStatement.setBoolean(2, clientOneDTO.isHavePlan());
            preparedStatement.setInt(3, userId);
            int clientRow = preparedStatement.executeUpdate();
            if (clientRow == 0) {
                throw new RuntimeException("Failed to create client record");
            }
            if (clientOneDTO.getDesign_link() != null && !clientOneDTO.getDesign_link().isEmpty()) {


                // 5️⃣ Retrieve the actual client_id from client table
                final String getClientIdSql = "SELECT client_id FROM client WHERE user_id = ?";
                preparedStatement = connection.prepareStatement(getClientIdSql);
                preparedStatement.setInt(1, userId);
                ResultSet clientResult = preparedStatement.executeQuery();

                String clientId = null;
                if (clientResult.next()) {
                    clientId = clientResult.getString("client_id");
                } else {
                    throw new RuntimeException("Client ID not found for user_id " + userId);
                }
                preparedStatement.close();
                final String designSql = """
                INSERT INTO design (client_id, design_link)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE design_link = VALUES(design_link)
            """;

                preparedStatement = connection.prepareStatement(designSql);
                preparedStatement.setString(1, clientId);
                preparedStatement.setString(2, clientOneDTO.getDesign_link());

                int designRows = preparedStatement.executeUpdate();
                if (designRows == 0) {
                    throw new RuntimeException("Failed to insert/update design record for client_id " + clientId);
                }
            }
            // send OTP to email

            mailService.sendClientRegisterOtp(clientOneDTO.getEmail(),clientOneDTO.getName(), otp);
            preparedStatement.close();

            //insert design link to design table




        }catch (SQLException e){
            throw new RuntimeException("Error inserting client:  " + e.getMessage(),e);

        }finally {
            closeResources(preparedStatement,connection,checkStmt);
        }

        return clientOneDTO;
    }

    @Override
    public List<ClientWithPlaneDTO> getClientWithPlan() {
        final List<ClientWithPlaneDTO> clientList = new ArrayList<>();

        final String sql =
                "SELECT c.client_id, u.name AS client_name, u.email, u.phone_number, c.type, " +
                        "c.is_have_plan, u.address, d.design_link " +
                        "FROM client c " +
                        "JOIN users u ON c.user_id = u.user_id " +
                        "LEFT JOIN design d ON c.client_id = d.client_id " +
                        "WHERE c.is_have_plan = 1";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            while (resultSet.next()) {
                ClientWithPlaneDTO client = new ClientWithPlaneDTO(
                        resultSet.getString("client_id"),
                        resultSet.getString("client_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("type"),
                        resultSet.getBoolean("is_have_plan"),
                        resultSet.getString("address"),
                        resultSet.getString("design_link")
                );
                clientList.add(client);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching client: " + e.getMessage(), e);
        }

        return clientList;

    }

    @Override
    public List<ClientWithPlaneDTO> getClientWithoutPlan() {
        final List<ClientWithPlaneDTO> clientList = new ArrayList<>();

        final String sql =
                "SELECT c.client_id,u.name AS client_name, u.email, u.phone_number, c.type, " +
                        "c.is_have_plan, u.address " +
                        "FROM client c " +
                        "JOIN users u ON c.user_id = u.user_id " +
                        "WHERE c.is_have_plan = 0";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            while (resultSet.next()) {
                ClientWithPlaneDTO client = new ClientWithPlaneDTO(
                        resultSet.getString("client_id"),
                        resultSet.getString("client_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone_number"),
                        resultSet.getString("type"),
                        resultSet.getBoolean("is_have_plan"),
                        resultSet.getString("address"),
                        ""
                );
                clientList.add(client);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching client: " + e.getMessage(), e);
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
        final String sql =
                "SELECT p.*, u.name AS clientName, IFNULL(pi.images, '') AS images " +
                        "FROM project p " +
                        "JOIN client c ON p.client_id = c.client_id " +
                        "JOIN users u ON c.user_id = u.user_id " +
                        "LEFT JOIN ( " +
                        "   SELECT project_id, GROUP_CONCAT(image_url) AS images " +
                        "   FROM project_images " +
                        "   GROUP BY project_id " +
                        ") pi ON p.project_id = pi.project_id";

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
                            resultSet.getString("clientName")

                    );

                    projectMap.put(projectId, project);
                }

                String imageUrl = resultSet.getString("images");
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

        final String sql = "SELECT p.*,pi.image_url, u.name AS clientName FROM  project p LEFT JOIN project_images pi ON p.project_id = pi.project_id LEFT JOIN client c ON p.client_id=c.client_id LEFT JOIN users u ON c.user_id = u.user_id WHERE p.project_id = ?" ;
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
                            resultSet.getString("clientName")


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
        final String sql = "SELECT p.*, pi.image_url, u.name AS client_name " +
                "FROM project p " +
                "LEFT JOIN project_images pi ON p.project_id = pi.project_id " +
                "LEFT JOIN client c ON p.client_id = c.client_id " +
                "LEFT JOIN users u ON c.user_id = u.user_id " +
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
                            resultSet.getString("client_name")

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


        try{
            connection = databaseConnection.getConnection();
            projectStmt = connection.prepareStatement(projectSql);
            projectStmt.setString(1, projectStartDTO.getQsId());
            projectStmt.setString(2,projectStartDTO.getPmId());
            projectStmt.setString(3,projectStartDTO.getSsId());
            projectStmt.setString(4,projectStartDTO.getStatus());
            projectStmt.setString(5,projectId);
            int rowUpdated = projectStmt.executeUpdate();
            if(rowUpdated == 0){
                throw new RuntimeException("Error Editing: " );
            }


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


    @Override
    public List<AllProjectDocumentDTO> getAllDocumentsById(String projectId) {
        List<AllProjectDocumentDTO> documentList = new ArrayList<>();
        final String sql = "SELECT * FROM project_documents WHERE project_id = ?";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, projectId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    AllProjectDocumentDTO document = new AllProjectDocumentDTO(

                            resultSet.getString("project_id"),
                            resultSet.getString("document_url"),
                            resultSet.getString("type"),
                            resultSet.getString("description")
                    );
                    documentList.add(document);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving documents for project ID " + projectId + ": " + e.getMessage(), e);
        }

        return documentList;
    }

    @Override
    public List<GetEmployeeDTO> getAllEmployees() throws SQLException {
        List<GetEmployeeDTO> employees = new ArrayList<>();

        final String sql =
                "SELECT e.employee_id, e.user_id, u.name, u.type, " +
                        "       COALESCE(p.project_count, 0) AS project_count " +
                        "FROM employee e " +
                        "JOIN users u ON e.user_id = u.user_id " +
                        "LEFT JOIN ( " +
                        "    SELECT emp_id, COUNT(*) AS project_count " +
                        "    FROM ( " +
                        "        SELECT pm_id AS emp_id FROM project " +
                        "        UNION ALL " +
                        "        SELECT qs_id AS emp_id FROM project " +
                        "        UNION ALL " +
                        "        SELECT ss_id AS emp_id FROM project " +
                        "    ) all_roles " +
                        "    WHERE emp_id IS NOT NULL " +
                        "    GROUP BY emp_id " +
                        ") p ON e.employee_id = p.emp_id " +
                        "ORDER BY e.employee_id";

        try (
                Connection connection = databaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet rs = preparedStatement.executeQuery();
        ) {
            while (rs.next()) {
                GetEmployeeDTO employee = new GetEmployeeDTO(
                        rs.getString("employee_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("type")
                );
                employee.setProjectCount(rs.getInt("project_count")); // Add this field in DTO
                employees.add(employee);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching employees: " + e.getMessage(), e);
        }

        return employees;
    }

    @Override
    public EmployeeByIdDTO getEmployeeById(String empid) {

            final String sql = """
            SELECT 
                u.name,
                u.phone_number
                
            FROM employee e
            INNER JOIN users u ON e.user_id = u.user_id
            WHERE e.employee_id = ?
        """;

            try (
                    Connection connection = databaseConnection.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            ) {
                ps.setString(1, empid);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return new EmployeeByIdDTO(
                            rs.getString("name"),
                            rs.getString("phone_number")

                    );
                }
                return null;

            } catch (SQLException e) {
                throw new RuntimeException("Error fetching employee by ID: " + e.getMessage(), e);
            }
        }

    @Override
    public double calculateProjectProgress(String projectId) {
        String mainTaskQuery = """
            SELECT task_id, status
            FROM wbs
            WHERE project_id = ? AND parent_id IS NULL
        """;

        double totalWeight = 0.0;
        double weightedProgressSum = 0.0;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(mainTaskQuery)) {

            ps.setString(1, projectId); // <-- CHANGED (was ps.setInt)

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int mainTaskId = rs.getInt("task_id");
                String mainStatus = rs.getString("status");

                // Calculate weighted progress for each main task
                WeightedProgress wp = getMainTaskProgress(conn, mainTaskId, mainStatus);
                weightedProgressSum += wp.progress * wp.weight;
                totalWeight += wp.weight;
            }

            if (totalWeight == 0) return 0.0;

            return weightedProgressSum / totalWeight; // Percentage
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error calculating project progress for project: " + projectId, e);
        }
    }

    private WeightedProgress getMainTaskProgress(Connection conn, int mainTaskId, String mainStatus) throws SQLException {
        String subTaskQuery = """
            SELECT COUNT(*) AS total,
                   SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) AS completed
            FROM wbs
            WHERE parent_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(subTaskQuery)) {
            ps.setInt(1, mainTaskId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int totalSub = rs.getInt("total");
                int completedSub = rs.getInt("completed");

                if (totalSub > 0) {
                    double subProgress = (completedSub * 100.0) / totalSub;
                    double blended = subProgress * 0.8 +
                            ("completed".equalsIgnoreCase(mainStatus) ? 20 : 0);
                    return new WeightedProgress(blended, 1.0);
                } else {
                    double progress = "completed".equalsIgnoreCase(mainStatus) ? 100.0 : 0.0;
                    return new WeightedProgress(progress, 1.5);
                }
            }
        }
        return new WeightedProgress(0.0, 1.0);
    }

    private static class WeightedProgress {
        double progress;
        double weight;

        WeightedProgress(double progress, double weight) {
            this.progress = progress;
            this.weight = weight;
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


