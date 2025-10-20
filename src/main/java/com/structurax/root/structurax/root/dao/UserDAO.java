package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.UserDTO;
import org.apache.catalina.User;

import java.util.Optional;

public interface UserDAO {

    Optional<UserDTO> findByEmail(String email);
    void save(UserDTO user);
    Optional<UserDTO> findByResetToken(String token);

    String findEmployeeIdByUserId(Integer userId);
    String findClientIdByUserId(Integer userId);
    String findSupplierIdByUserId(Integer userId);
    String findAdminIdByUserId(Integer userId);
    Optional<UserDTO> getUserProfileByAnyId(String id);
}
