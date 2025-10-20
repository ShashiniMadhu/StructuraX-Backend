package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.ForgotPasswordRequest;
import com.structurax.root.structurax.root.dto.ResetPasswordRequest;
import com.structurax.root.structurax.root.dto.UserDTO;
import com.structurax.root.structurax.root.dto.UserLoginDTO;
import com.structurax.root.structurax.root.dto.UserResponseDTO;
import com.structurax.root.structurax.root.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService){
        this.userService= userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody UserLoginDTO loginDTO) {
        UserResponseDTO response = userService.login(loginDTO);
        System.out.println("Email: " + loginDTO.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.generateResetToken(request);
        return ResponseEntity.ok("Password reset link sent (check console or email)");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful");
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable String id) {
        try {
            UserDTO userProfile = userService.getUserProfileByAnyId(id);
            return ResponseEntity.ok(userProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
