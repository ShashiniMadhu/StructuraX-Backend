package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class QuotationResponseDTO {
    private Integer responseId;
    private Integer qId;
    private Integer supplierId;
    private String supplierName;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private Date responseDate;
    private Date validUntil;
    private Date deliveryDate;
    private String additionalNote;
    private Date respondDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public QuotationResponseDTO() {
    }

    // Constructor with essential fields
    public QuotationResponseDTO(Integer responseId, Integer qId, Integer supplierId,
                               BigDecimal totalAmount, String status, Date responseDate) {
        this.responseId = responseId;
        this.qId = qId;
        this.supplierId = supplierId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.responseDate = responseDate;
    }

    // Getters and Setters
    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }

    public Integer getQId() {
        return qId;
    }

    public void setQId(Integer qId) {
        this.qId = qId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public Date getRespondDate() {
        return respondDate;
    }

    public void setRespondDate(Date respondDate) {
        this.respondDate = respondDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "QuotationResponseDTO{" +
                "responseId=" + responseId +
                ", qId=" + qId +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                ", responseDate=" + responseDate +
                ", validUntil=" + validUntil +
                ", deliveryDate=" + deliveryDate +
                ", additionalNote='" + additionalNote + '\'' +
                ", respondDate=" + respondDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
