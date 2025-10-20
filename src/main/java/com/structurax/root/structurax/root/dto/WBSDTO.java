package com.structurax.root.structurax.root.dto;

public class WBSDTO {
    private int taskId;
    private String projectId;
    private int parentId;
    private String name;
    private String status;
    private Boolean milestone;

    // Default constructor
    public WBSDTO() {
    }

    // Parameterized constructor
    public WBSDTO(int taskId, String projectId, int parentId, String name, String status, Boolean milestone) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.parentId = parentId;
        this.name = name;
        this.status = status;
        this.milestone = milestone;
    }

    public WBSDTO(int taskId, String projectId, int parentId, String status, Boolean milestone) {
    }

    // Getters and Setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getMilestone() {
        return milestone;
    }

    public void setMilestone(Boolean milestone) {
        this.milestone = milestone;
    }

    @Override
    public String toString() {
        return "WBSDTO{" +
                "taskId=" + taskId +
                ", projectId='" + projectId + '\'' +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", milestone=" + milestone +
                '}';
    }
}
