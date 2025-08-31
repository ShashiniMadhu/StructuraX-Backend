package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;

public class OrderItemDTO {
    private Integer orderId;
    private Integer itemId;
    private String description;
    private BigDecimal unitPrice;
    private Integer quantity;
    
    public OrderItemDTO() {}
    
    // Getters and setters
    public Integer getOrderId() { 
        return orderId; 
    }
    
    public void setOrderId(Integer orderId) { 
        this.orderId = orderId; 
    }
    
    public Integer getItemId() { 
        return itemId; 
    }
    
    public void setItemId(Integer itemId) { 
        this.itemId = itemId; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public BigDecimal getUnitPrice() { 
        return unitPrice; 
    }
    
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice; 
    }
    
    public Integer getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
    }
}
