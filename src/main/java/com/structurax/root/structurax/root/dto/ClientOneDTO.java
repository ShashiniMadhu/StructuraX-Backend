package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOneDTO {
//    @JsonProperty("client_id")
//    private String clientId;

    @JsonProperty("name")
    private String name;

//    @JsonProperty("last_name")
//    private String lastName;

    private String email;



    @JsonProperty("phone_number")
    private String phoneNumber;

    private String type; // e.g., individual, company, etc.

    private String address;

    @JsonProperty("is_have_plan")
    private boolean isHavePlan;

    private String design_link;

//    private String role="Project_Owner";


}
