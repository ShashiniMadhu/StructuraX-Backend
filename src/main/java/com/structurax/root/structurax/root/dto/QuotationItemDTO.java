package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;

public class QuotationItemDTO {
    private Integer itemId;
    private Integer qId;
    private String name;
    private String description;
    private BigDecimal amount;
    private Integer quantity;

    public QuotationItemDTO() {
    }

    public QuotationItemDTO(Integer itemId, Integer qId, String name, String description, BigDecimal amount, Integer quantity) {
        this.itemId = itemId;
        this.qId = qId;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.quantity = quantity;
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

    @Override
    public String toString() {
        return "QuotationItemDTO{" +
                "itemId=" + itemId +
                ", qId=" + qId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", quantity=" + quantity +
                '}';
    }
}
