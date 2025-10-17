package com.structurax.root.structurax.root.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Date;

public class QuotationResponseDTO {

    @JsonProperty("responseId")
    private Integer responseId;

    @JsonProperty("qId")
    @JsonAlias({"quotationId"})
    private Integer qId;

    @JsonProperty("supplierId")
    private Integer supplierId;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("deliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDate;

    @JsonProperty("additionalNote")
    private String additionalNote;

    @JsonProperty("respondDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date respondDate;

    @JsonProperty("deliveryTime")
    private Integer deliveryTime;

    // Default constructor
    public QuotationResponseDTO() {
    }

    // Constructor matching database columns
    public QuotationResponseDTO(Integer responseId, Integer qId, Integer supplierId,
                                BigDecimal totalAmount, Date deliveryDate, String additionalNote,
                                Date respondDate, String status) {
        this.responseId = responseId;
        this.qId = qId;
        this.supplierId = supplierId;
        this.totalAmount = totalAmount;
        this.deliveryDate = deliveryDate;
        this.additionalNote = additionalNote;
        this.respondDate = respondDate;
        this.status = status;
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

    // Alias methods for service compatibility
    @JsonAlias({"response_date"})
    public Date getResponseDate() {
        return respondDate;
    }

    public void setResponseDate(Date responseDate) {
        this.respondDate = responseDate;
    }

    @Override
    public String toString() {
        return "QuotationResponseDTO{" +
                "responseId=" + responseId +
                ", qId=" + qId +
                ", supplierId=" + supplierId +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", deliveryDate=" + deliveryDate +
                ", deliveryTime=" + deliveryTime +
                ", additionalNote='" + additionalNote + '\'' +
                ", respondDate=" + respondDate +
                '}';
    }
}