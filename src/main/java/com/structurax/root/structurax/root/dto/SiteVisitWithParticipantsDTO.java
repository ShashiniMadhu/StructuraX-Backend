package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteVisitWithParticipantsDTO {
    
    @JsonProperty("visit_id")
    private Integer visitId;
    
    @JsonProperty("project_id")
    private String projectId;
    
    private LocalDate date;
    
    private String description;
    
    private String status;
    
    private List<VisitParticipantDTO> participants;
}
