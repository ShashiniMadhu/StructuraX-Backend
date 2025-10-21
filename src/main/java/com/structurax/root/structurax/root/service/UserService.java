package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ForgotPasswordRequest;
import com.structurax.root.structurax.root.dto.ResetPasswordRequest;
import com.structurax.root.structurax.root.dto.UserDTO;
import com.structurax.root.structurax.root.dto.UserLoginDTO;
import com.structurax.root.structurax.root.dto.UserResponseDTO;
import java.util.List;


public interface UserService {

    UserResponseDTO login(UserLoginDTO loginDTO);
    void generateResetToken(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    UserDTO getUserProfileByAnyId(String id);
    List<UserDTO> getAllUsers();
}
