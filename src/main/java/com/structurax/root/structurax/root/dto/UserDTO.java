package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private int userId;

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
    @JsonProperty("password")
    private String password;

    @NotNull(message = "Availability status is required")
    @Pattern(regexp = "^(Assigned|Available|Deactive)$", message = "Availability must be one of: Assigned, Available, Deactive")
    @JsonProperty("availability")
    private String availability;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    private String resetToken;
    private LocalDateTime tokenExpiry;

    // Custom constructor for database queries (without password)
    public UserDTO(int userId, String name, String email, String phoneNumber, String address, String type, LocalDate joinedDate, String availability, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.type = type;
        this.joinedDate = joinedDate;
        this.availability = availability;
        this.profileImageUrl = profileImageUrl;
    }

    // Explicit getters for compatibility when Lombok is not processing annotations.
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public String getPassword() {
        return password;
    }

    public String getAvailability() {
        return availability;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getUserId() {
        return userId;
    }

    public String getResetToken() {
        return resetToken;
    }

    public LocalDateTime getTokenExpiry() {
        return tokenExpiry;
    }
}

