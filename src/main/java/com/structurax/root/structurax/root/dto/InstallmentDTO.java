package com.structurax.root.structurax.root.dto;

import java.sql.Date;  // Use java.sql.Date consistently


public class InstallmentDTO {
    private int installmentId;
    private int paymentPlanId;
    private String milestone;
    private double amount;
    private Date dueDate;
    private String status; // Default: "Pending"

    // Default constructor
    public InstallmentDTO() {
        this.status = "Pending";  // default status
    }

    // Full constructor
    public InstallmentDTO(int installmentId, int paymentPlanId, String milestone,double amount, Date dueDate, String status) {
        this.installmentId = installmentId;
        this.paymentPlanId = paymentPlanId;
        this.milestone = milestone;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }



    // Getters and setters
    public int getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(int installmentId) {
        this.installmentId = installmentId;
    }

    public int getPaymentPlanId() {
        return paymentPlanId;
    }

    public void setPaymentPlanId(int paymentPlanId) {
        this.paymentPlanId = paymentPlanId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
