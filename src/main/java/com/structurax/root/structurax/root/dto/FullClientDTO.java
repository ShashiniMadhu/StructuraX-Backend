package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullClientDTO {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @JsonProperty("client_type")
    private String clientType; // 'company', 'individual', 'government'

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("budget")
    private Double budget;

    @JsonProperty("location")
    private String location;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("due_date")
    private LocalDate dueDate;
}