package com.structurax.root.structurax.root.dto;

public class QuotationSupplierDTO {
    private Integer qId;
    private Integer supplierId;

    public QuotationSupplierDTO() {
    }

    public QuotationSupplierDTO(Integer qId, Integer supplierId) {
        this.qId = qId;
        this.supplierId = supplierId;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "QuotationSupplierDTO{" +
                "qId=" + qId +
                ", supplierId=" + supplierId +
                '}';
    }
}
