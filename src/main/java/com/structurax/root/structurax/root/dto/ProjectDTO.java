package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private String projectId;
    private String name;
    private String description;
    private String location;
    private String status;
    private String category;
    private LocalDate startDate;
    private LocalDate dueDate;
    private BigDecimal estimatedValue;
    private BigDecimal budget;
    private String clientId;
    private String qsId;
    private String pmId;
    private String ssId;

    private ClientDTO client;



}
