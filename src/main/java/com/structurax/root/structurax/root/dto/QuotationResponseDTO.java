package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class QuotationResponseDTO {

    @JsonProperty("responseId")
    private Integer responseId;

    @JsonProperty("qId")
    @JsonAlias({"quotationId"}) // Accept both qId and quotationId from JSON
    private Integer qId;

    @JsonProperty("supplierId")
    private Integer supplierId;

    @JsonProperty("supplierName")
    private String supplierName;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("responseDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date responseDate;

    @JsonProperty("validUntil")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date validUntil;

    @JsonProperty("deliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    @JsonProperty("deliveryTime")
    @JsonAlias({"deliveryTime"}) // Handle deliveryTime from frontend
    private Integer deliveryTime;

    @JsonProperty("additionalNote")
    private String additionalNote;

    @JsonProperty("respondDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date respondDate;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Timestamp createdAt;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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

    // All-args constructor
    public QuotationResponseDTO(Integer responseId, Integer qId, Integer supplierId,
                                String supplierName, BigDecimal totalAmount, String status,
                                String notes, Date responseDate, Date validUntil,
                                Date deliveryDate, String additionalNote, Date respondDate,
                                Timestamp createdAt, Timestamp updatedAt) {
        this.responseId = responseId;
        this.qId = qId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.notes = notes;
        this.responseDate = responseDate;
        this.validUntil = validUntil;
        this.deliveryDate = deliveryDate;
        this.additionalNote = additionalNote;
        this.respondDate = respondDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    // Additional setter to handle quotationId from frontend
    @JsonAlias("quotationId")
    public void setQuotationId(Integer quotationId) {
        this.qId = quotationId;
    }

    public Integer getQuotationId() {
        return qId;
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

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer deliveryTime) {
        this.deliveryTime = deliveryTime;
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
                ", deliveryTime=" + deliveryTime +
                ", additionalNote='" + additionalNote + '\'' +
                ", respondDate=" + respondDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}