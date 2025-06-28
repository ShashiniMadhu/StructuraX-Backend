package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;

public class PaymentPlanDTO {

    private Integer paymentPlanId;
    private String milestone;
    private BigDecimal amount;
    private String status;

    public PaymentPlanDTO(int paymentPlanId, String milestone, BigDecimal amount, String status) {
        this.paymentPlanId = paymentPlanId;
        this.milestone = milestone;
        this.amount = amount;
        this.status = status;
    }



    // Getters and Setters
    public Integer getPaymentPlanId() {
        return paymentPlanId;
    }

    public void setPaymentPlanId(Integer paymentPlanId) {
        this.paymentPlanId = paymentPlanId;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
