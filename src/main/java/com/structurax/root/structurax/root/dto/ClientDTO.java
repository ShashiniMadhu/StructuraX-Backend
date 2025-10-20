package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
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

    @JsonProperty("is_have_plan")
    private boolean isHavePlan;

    private String address;


    private String design_link;




    // Explicitly define getter and setter for isHavePlan
    public boolean isHavePlan() {
        return isHavePlan;
    }

    public void setIsHavePlan(boolean isHavePlan) {
        this.isHavePlan = isHavePlan;
    }

    // Explicit getters and setters for compatibility
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesign_link() {
        return design_link;
    }

    public void setDesign_link(String design_link) {
        this.design_link = design_link;
    }
}

