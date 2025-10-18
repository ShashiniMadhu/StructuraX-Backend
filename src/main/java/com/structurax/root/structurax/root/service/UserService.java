package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ForgotPasswordRequest;
import com.structurax.root.structurax.root.dto.ResetPasswordRequest;
import com.structurax.root.structurax.root.dto.UserLoginDTO;
import com.structurax.root.structurax.root.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO login(UserLoginDTO loginDTO);
    void generateResetToken(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);



}
