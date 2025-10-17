package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class QuotationItemDTO {
    private Integer itemId;
    private Integer qId;
    private String name;
    private String description;
    private BigDecimal amount;
    private Integer quantity;

    // Enhanced fields for detailed item information
    private String unit;
    private String category;
    private String specifications;
    private String brand;
    private String model;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String itemCode;
    private String priority;
    private Date requiredDate;
    private String notes;
    private String status;
    private Date createdDate;
    private Date updatedDate;

    // Default constructor
    public QuotationItemDTO() {
    }

    // Original constructor for backward compatibility
    public QuotationItemDTO(Integer itemId, Integer qId, String name, String description, BigDecimal amount, Integer quantity) {
        this.itemId = itemId;
        this.qId = qId;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.quantity = quantity;
    }

    // Enhanced constructor with all details
    public QuotationItemDTO(Integer itemId, Integer qId, String name, String description,
                           BigDecimal amount, Integer quantity, String unit, String category,
                           String specifications, String brand, String model, BigDecimal unitPrice,
                           String itemCode, String priority, Date requiredDate) {
        this.itemId = itemId;
        this.qId = qId;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.specifications = specifications;
        this.brand = brand;
        this.model = model;
        this.unitPrice = unitPrice;
        this.itemCode = itemCode;
        this.priority = priority;
        this.requiredDate = requiredDate;
        this.calculateTotalPrice();
    }

    // Getters and Setters
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getQId() {
        return qId;
    }

    public void setQId(Integer qId) {
        this.qId = qId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    // Helper methods
    public void calculateTotalPrice() {
        if (this.quantity != null && this.unitPrice != null) {
            this.totalPrice = this.unitPrice.multiply(new BigDecimal(this.quantity));
        } else if (this.quantity != null && this.amount != null) {
            // Fallback to using amount as unit price
            this.totalPrice = this.amount.multiply(new BigDecimal(this.quantity));
            this.unitPrice = this.amount;
        }
    }

    public String getFormattedPrice() {
        if (totalPrice != null) {
            return String.format("Rs. %.2f", totalPrice);
        } else if (amount != null) {
            return String.format("Rs. %.2f", amount);
        }
        return "Price not available";
    }

    public String getItemSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(name);
        if (brand != null && !brand.trim().isEmpty()) {
            summary.append(" (").append(brand);
            if (model != null && !model.trim().isEmpty()) {
                summary.append(" ").append(model);
            }
            summary.append(")");
        }
        if (quantity != null && unit != null) {
            summary.append(" - ").append(quantity).append(" ").append(unit);
        }
        return summary.toString();
    }

    @Override
    public String toString() {
        return "QuotationItemDTO{" +
                "itemId=" + itemId +
                ", qId=" + qId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", category='" + category + '\'' +
                ", specifications='" + specifications + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", itemCode='" + itemCode + '\'' +
                ", priority='" + priority + '\'' +
                ", requiredDate=" + requiredDate +
                ", status='" + status + '\'' +
                '}';
    }
}
