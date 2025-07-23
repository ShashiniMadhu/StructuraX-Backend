package com.structurax.root.structurax.root.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponseDTO {

    private String adminId;
    private String email;
    private String role="Admin";
    private String token;
}
