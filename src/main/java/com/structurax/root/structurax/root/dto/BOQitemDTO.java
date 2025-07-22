package com.structurax.root.structurax.root.dto;

public class BOQitemDTO {
    private String itemId;
    private String boqId;
    private String itemDescription;
    private double rate;
    private String unit;
    private double quantity;
    private double amount;

    public BOQitemDTO() {}

    public BOQitemDTO(String itemId, String boqId, String itemDescription, double rate, String unit, double quantity, double amount) {
        this.itemId = itemId;
        this.boqId = boqId;
        this.itemDescription = itemDescription;
        this.rate = rate;
        this.unit = unit;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBoqId() {
        return boqId;
    }

    public void setBoqId(String boqId) {
        this.boqId = boqId;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
