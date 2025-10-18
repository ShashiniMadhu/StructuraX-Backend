package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteVisitDTO {

    private Integer visitId;
    private String projectId;
    private String type; // Site Inspection, Progress Review, Quality Check, Client Meeting
    private LocalDate date;
    private String time;
    private String status; // Requested, Pending, Approved, Rejected, Completed
    private String requestedBy;
    private String purpose;
    private Integer clientId;
    private LocalDate createdDate;
}