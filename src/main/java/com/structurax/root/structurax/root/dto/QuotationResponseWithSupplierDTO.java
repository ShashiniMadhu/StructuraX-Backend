package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class QuotationResponseWithSupplierDTO {
    private Integer responseId;
    private Integer qId;
    private Integer supplierId;
    private String supplierName;
    private String supplierEmail;
    private String supplierPhone;
    private String supplierAddress;
    private BigDecimal totalAmount;
    private LocalDate deliveryDate;
    private String additionalNote;
    private LocalDate respondDate;
    private String status; // 'pending', 'accepted', 'rejected'

    public QuotationResponseWithSupplierDTO() {
    }

    public QuotationResponseWithSupplierDTO(Integer responseId, Integer qId, Integer supplierId, 
                                          String supplierName, String supplierEmail, String supplierPhone, 
                                          String supplierAddress, BigDecimal totalAmount, LocalDate deliveryDate, 
                                          String additionalNote, LocalDate respondDate, String status) {
        this.responseId = responseId;
        this.qId = qId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;
        this.supplierAddress = supplierAddress;
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

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public LocalDate getRespondDate() {
        return respondDate;
    }

    public void setRespondDate(LocalDate respondDate) {
        this.respondDate = respondDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QuotationResponseWithSupplierDTO{" +
                "responseId=" + responseId +
                ", qId=" + qId +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", supplierEmail='" + supplierEmail + '\'' +
                ", supplierPhone='" + supplierPhone + '\'' +
                ", supplierAddress='" + supplierAddress + '\'' +
                ", totalAmount=" + totalAmount +
                ", deliveryDate=" + deliveryDate +
                ", additionalNote='" + additionalNote + '\'' +
                ", respondDate=" + respondDate +
                ", status='" + status + '\'' +
                '}';
    }
}
