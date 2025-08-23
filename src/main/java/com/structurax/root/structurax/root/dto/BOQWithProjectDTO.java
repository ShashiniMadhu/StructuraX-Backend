package com.structurax.root.structurax.root.dto;

import java.util.List;

/**
 * DTO for BOQ with project information - used by SQS to view BOQs with project context
 */
public class BOQWithProjectDTO {
    private BOQDTO boq;
    private List<BOQitemDTO> items;
    private String projectName;
    private String projectLocation;
    private String projectCategory;
    private String qsName; // Name of the QS assigned to this project
    
    public BOQWithProjectDTO() {}
    
    public BOQWithProjectDTO(BOQDTO boq, List<BOQitemDTO> items, String projectName, 
                            String projectLocation, String projectCategory, String qsName) {
        this.boq = boq;
        this.items = items;
        this.projectName = projectName;
        this.projectLocation = projectLocation;
        this.projectCategory = projectCategory;
        this.qsName = qsName;
    }
    
    // Getters and setters
    public BOQDTO getBoq() {
        return boq;
    }
    
    public void setBoq(BOQDTO boq) {
        this.boq = boq;
    }
    
    public List<BOQitemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<BOQitemDTO> items) {
        this.items = items;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getProjectLocation() {
        return projectLocation;
    }
    
    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }
    
    public String getProjectCategory() {
        return projectCategory;
    }
    
    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }
    
    public String getQsName() {
        return qsName;
    }
    
    public void setQsName(String qsName) {
        this.qsName = qsName;
    }
}
