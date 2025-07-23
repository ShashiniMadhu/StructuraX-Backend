package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    private String clientId;

    private String email;
    private String role="Project_Owner";
    private String token;
}
