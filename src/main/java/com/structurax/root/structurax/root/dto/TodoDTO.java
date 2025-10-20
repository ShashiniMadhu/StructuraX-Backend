package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class TodoDTO {

    @JsonProperty("task_id")
    private int taskId;
    @JsonProperty("employee_id")
    private String employeeId;
    private String status;
    private String description;
    private Date date;


    public TodoDTO(int taskId, String employeeId, String status, String description, Date date) {
        this.taskId = taskId;
        this.employeeId = employeeId;
        this.status = status;
        this.description = description;
        this.date = date;
    }
}