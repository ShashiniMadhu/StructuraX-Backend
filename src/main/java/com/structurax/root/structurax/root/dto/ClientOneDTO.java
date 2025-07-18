package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientOneDTO {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    private String password;

    @JsonProperty("contact_number")
    private String contactNumber;

    private String type; // e.g., individual, company, etc.

    private String address;

    @JsonProperty("is_have_plan")
    private boolean isHavePlan;

    private String design_link;


}
