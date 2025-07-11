package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    @JsonProperty("employee_id")
    @Pattern(regexp = "^EMP_\\d{3}$", message = "Employee ID must follow format EMP_XXX")
    private String employeeId;

    @NotBlank(message = "Name is required")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @JsonProperty("address")
    private String address;

    @NotBlank(message = "Type is required")
    @JsonProperty("type")
    private String type;

    @NotNull(message = "Joined date is required")
    @JsonProperty("joined_date")
    private LocalDate joinedDate;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @JsonProperty("password")
    private String password;

    @NotNull(message = "Availability status is required")
    @JsonProperty("availability")
    private Boolean availability;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

}