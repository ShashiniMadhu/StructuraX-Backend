package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private String adminId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private Date joinedDate;
    private String password;

    private String role="Admin";
}
