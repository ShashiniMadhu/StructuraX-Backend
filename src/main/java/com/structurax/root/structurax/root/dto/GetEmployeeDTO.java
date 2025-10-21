package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetEmployeeDTO {
    private String employeeId;
    private int userId;
    private String name;
    private String type;
    private int projectCount;


    public GetEmployeeDTO(String employeeId, int userId, String name, String type) {
        this.employeeId = employeeId;
        this.userId = userId;
        this.name = name;
        this.type = type;
    }

}
