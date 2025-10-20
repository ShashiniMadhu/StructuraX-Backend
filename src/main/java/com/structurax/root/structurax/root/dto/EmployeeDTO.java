package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    @JsonProperty("employee_id")
    @Pattern(regexp = "^EMP_\\d{3}$", message = "Employee ID must follow format EMP_XXX")
    private String employeeId;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("name")
    private String name;

    @JsonProperty("role")
    private String role;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("joined_date")
    private LocalDate joinedDate;

    @JsonProperty("availability")
    private String availability;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    // Keep the old constructor for backward compatibility
    public EmployeeDTO(String employeeId, String name, String email, String phoneNumber, String address, String type, LocalDate localJoinedDate, Object o, String availability, String profileImageUrl) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = type;
        this.joinedDate = localJoinedDate;
        this.availability = availability;
        this.profileImageUrl = profileImageUrl;
    }
}