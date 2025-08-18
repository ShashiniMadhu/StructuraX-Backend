package com.structurax.root.structurax.root.dto;

public class QuotationSupplierDTO {
    private Integer qId;
    private Integer supplierId;
    private String supplierName;

    public QuotationSupplierDTO() {
    }

    public QuotationSupplierDTO(Integer qId, Integer supplierId) {
        this.qId = qId;
        this.supplierId = supplierId;
    }

    public QuotationSupplierDTO(Integer qId, Integer supplierId, String supplierName) {
        this.qId = qId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return "QuotationSupplierDTO{" +
                "qId=" + qId +
                ", supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}
