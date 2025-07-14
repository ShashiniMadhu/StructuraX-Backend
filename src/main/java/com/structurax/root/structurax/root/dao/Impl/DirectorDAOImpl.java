package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.DirectorDAO;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.util.DatabaseConnection;
import com.structurax.root.structurax.root.util.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            preparedStatement.setString(2, clientDTO.getFirstName());
            preparedStatement.setString(3, clientDTO.getLastName());
            preparedStatement.setString(4, clientDTO.getEmail());
            preparedStatement.setString(5, hashedPassword);

            preparedStatement.setString(6, clientDTO.getContactNumber());
            preparedStatement.setString(7, clientDTO.getType());
            preparedStatement.setBoolean(8, clientDTO.isHavePlan());
            preparedStatement.setString(9, clientDTO.getAddress());

            int rowAffected = preparedStatement.executeUpdate();
            if(rowAffected == 0){
                throw new RuntimeException("Failed to create client");
            }
        }catch (SQLException e){
            throw new RuntimeException("Error inserting employee: " + e.getMessage(),e);

        }finally {
            closeResources(preparedStatement,connection);
        }

        return clientDTO;
    }

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

    @Override
    public List<ClientDTO> getClientByType(String type) {
        return List.of();
    }
}
