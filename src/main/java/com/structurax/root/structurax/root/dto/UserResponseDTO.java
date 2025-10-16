package com.structurax.root.structurax.root.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private int userId;
    private String fName;
    private String email;
    private String type;
    private String token;

    private String employeeId;
    private String clientId;
    private String supplierId;
    private String adminId;
}
