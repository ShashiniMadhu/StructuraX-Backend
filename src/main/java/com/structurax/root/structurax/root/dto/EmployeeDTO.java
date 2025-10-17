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

    @JsonProperty("user_id")
    private int userId;

    private String availability;
    public EmployeeDTO(String employeeId, String name, String email, String phoneNumber, String address, String type, LocalDate localJoinedDate, Object o, String availability, String profileImageUrl) {
    }


    public void setName(String name) {
    }

    public void setEmail(String email) {
    }

    public void setPhoneNumber(String phoneNumber) {
    }

    public void setJoinedDate(LocalDate localDate) {
    }

    public void setType(String type) {
    }

    public void setPassword(String password) {
    }

    public void setAvailability(String availability) {
    }

    public void setProfileImageUrl(String profileImageUrl) {
    }
}