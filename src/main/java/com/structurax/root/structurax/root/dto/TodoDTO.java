package com.structurax.root.structurax.root.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class TodoDTO {

    private Integer taskId;
    private String employeeId;
    private String status;
    private String description;
    private Date date;


    public TodoDTO(Integer taskId, String employeeId, String status, String description, Date date) {
        this.taskId = taskId;
        this.employeeId = employeeId;
        this.status = status;
        this.description = description;
        this.date = date;
    }
}