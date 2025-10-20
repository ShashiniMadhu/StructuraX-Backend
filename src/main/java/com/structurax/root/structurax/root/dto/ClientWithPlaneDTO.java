package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientWithPlaneDTO {

    private String client_id;

    @JsonProperty("name")
    private String name;

    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String type; // e.g., individual, company, etc.

    @JsonProperty("is_have_plan")
    private boolean isHavePlan;

    private String address;


    private String design_link;
}
