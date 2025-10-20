package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEmployeeDTO {
    @JsonProperty("employee_id")
    @Pattern(regexp = "^EMP_\\d{3}$", message = "Employee ID must follow format EMP_XXX")
    private String employeeId;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    @Email(message = "Email should be valid")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("type")
    private String type;

    @JsonProperty("joined_date")
    private LocalDate joinedDate;

    @JsonProperty("availability")
    private String availability;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;
}
