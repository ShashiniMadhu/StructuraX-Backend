package com.structurax.root.structurax.root.dto;

import java.sql.Date;
import java.util.List;

public class PaymentPlanDTO {

    private int paymentPlanId;
    private Date createdDate;
    private double totalAmount;
    private Date startDate;
    private Date endDate;
    private int numberOfInstallments;
    private int projectId;
    private List<InstallmentDTO> installments;

    public PaymentPlanDTO(){}

    // ✅ Proper no-args constructor (needed by Jackson)
    public PaymentPlanDTO(int paymentPlanId, Date createdDate, double totalAmount, Date startDate, Date endDate, int numberOfInstallments, int projectId,Object o) {
    }

    // ✅ Full constructor
    public PaymentPlanDTO(int paymentPlanId ,Date createdDate, double totalAmount, Date startDate, Date endDate, int numberOfInstallments,int projectId, List<InstallmentDTO> installments) {
        this.paymentPlanId = paymentPlanId;
        this.createdDate = createdDate;
        this.totalAmount = totalAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfInstallments = numberOfInstallments;
        this.projectId= projectId;
        this.installments = installments;
    }

    // ✅ Constructor without installments
    public PaymentPlanDTO(int paymentPlanId, Date createdDate, double totalAmount, Date startDate, Date endDate, int numberOfInstallments,int projectId) {
        this(paymentPlanId, createdDate, totalAmount, startDate, endDate, numberOfInstallments,projectId, null);
    }

    // Getters and setters (unchanged)
    public int getPaymentPlanId() {
        return paymentPlanId;
    }

    public void setPaymentPlanId(int paymentPlanId) {
        this.paymentPlanId = paymentPlanId;
    }



    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(int numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public List<InstallmentDTO> getInstallments() {
        return installments;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setInstallments(List<InstallmentDTO> installments) {
        this.installments = installments;
    }
}
