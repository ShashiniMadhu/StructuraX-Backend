package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborAttendanceDTO {

    private Integer id;
    private String project_id;
    private Date date;
    private String hiring_type;
    private String labor_type;
    private Integer count;
    private String company;

}
