package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.UserDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.MailService;
import com.structurax.root.structurax.root.service.UserService;
import com.structurax.root.structurax.root.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    public UserServiceImpl(UserDAO userDAO, PasswordEncoder passwordEncoder, MailService mailService){
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        UserDTO user = userDAO.findByEmail(loginDTO.getEmail())
                .orElseThrow(()-> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(loginDTO.getPassword(),user.getPassword())){
            throw  new RuntimeException("Invalid password");
        }

        String employeeId = null;
        String clientId = null;
        int supplierId = 0;
        String adminId = null;

        if(user.getType().equalsIgnoreCase("supplier")){
            employeeId = userDAO.findSupplierIdByUserId(user.getUserId());
        } else if (user.getType().equalsIgnoreCase("client")) {
            clientId = userDAO.findClientIdByUserId(user.getUserId());
        } else if(user.getType().equalsIgnoreCase("admin")){
            adminId = userDAO.findAdminIdByUserId(user.getUserId());
        }else{
            employeeId = userDAO.findEmployeeIdByUserId(user.getUserId());
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getType(),
                user.getUserId(),
                employeeId,
                clientId,
                supplierId,
                adminId
        );

        return new UserResponseDTO(
          user.getUserId(),
          user.getName(),
          user.getEmail(),
          user.getType(),
          token,
          employeeId,
          clientId,
          supplierId,
          adminId
        );

    }

    @Override
    public void generateResetToken(ForgotPasswordRequest request) {
        Optional<UserDTO> userDTO = userDAO.findByEmail(request.getEmail());
        if(userDTO.isPresent()){
            UserDTO user = userDTO.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(1));
            userDAO.save(user);

            String resetLink = "http://localhost:5173/reset-password?token=" + token;

            //send email to reset password
            mailService.sendPassswordResetEmail(user.getEmail(),user.getName(),resetLink);

            System.out.println("Reset link sent to the email: "+ resetLink);
        }else{
            throw new RuntimeException("No user found with this email.");
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        Optional<UserDTO> userDTO = userDAO.findByResetToken(request.getToken());
        if(userDTO.isPresent()){
            UserDTO user = userDTO.get();

            //check for null expiry to prevent NullPointerException
            if(user.getTokenExpiry()== null || user.getTokenExpiry().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Token expired or Invalid");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            System.out.println("New message hashed: " + user.getPassword());

            user.setResetToken(null);
            user.setTokenExpiry(null);
            userDAO.save(user);
        }else{
            throw new RuntimeException("Invalid Token!");
        }

    }

}
