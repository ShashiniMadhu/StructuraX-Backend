package com.structurax.root.structurax.root.dao.Impl;

import com.structurax.root.structurax.root.dao.AdminDAO;
import com.structurax.root.structurax.root.dto.EmployeeDTO;
import com.structurax.root.structurax.root.util.DatabseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AdminDAOImpl implements AdminDAO {
    @Autowired
    private DatabseConnection databseConnection;

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            final String sql = "INSERT INTO employee(name,email) VALUES(?,?)";
            connection = databseConnection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,employeeDTO.getname());
            preparedStatement.setString(2,employeeDTO.getEmail());
            preparedStatement.executeUpdate();
        }catch(SQLException e) {
            throw new RuntimeException(e);
        }finally{//resources deallocation
            try{
                if(connection != null) connection.close();
                if(preparedStatement != null) preparedStatement.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }//keep "finally" to close the connection.This will run although run or not
        return employeeDTO;    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return List.of();
    }

    @Override
    public EmployeeDTO getEmployeeById(Integer id) {
        return null;
    }

    @Override
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO) {
        return null;
    }

    @Override
    public EmployeeDTO deleteEmployeeById(Integer id) {
        return null;
    }
}
