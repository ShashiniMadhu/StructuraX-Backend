package com.structurax.root.structurax.root.dto;

import java.math.BigDecimal;

public class PurchaseOrderItemDTO {
    private Integer orderItemId;
    private Integer orderId;
    private String productName;
    private String specifications;
    private Integer requestedQuantity;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;
    
    public PurchaseOrderItemDTO() {}
    
    // Getters and setters
    public Integer getOrderItemId() { 
        return orderItemId; 
    }
    
    public void setOrderItemId(Integer orderItemId) { 
        this.orderItemId = orderItemId; 
    }
    
    public Integer getOrderId() { 
        return orderId; 
    }
    
    public void setOrderId(Integer orderId) { 
        this.orderId = orderId; 
    }
    
    public String getProductName() { 
        return productName; 
    }
    
    public void setProductName(String productName) { 
        this.productName = productName; 
    }
    
    public String getSpecifications() { 
        return specifications; 
    }
    
    public void setSpecifications(String specifications) { 
        this.specifications = specifications; 
    }
    
    public Integer getRequestedQuantity() { 
        return requestedQuantity; 
    }
    
    public void setRequestedQuantity(Integer requestedQuantity) { 
        this.requestedQuantity = requestedQuantity; 
    }
    
    public BigDecimal getUnitCost() { 
        return unitCost; 
    }
    
    public void setUnitCost(BigDecimal unitCost) { 
        this.unitCost = unitCost; 
    }
    
    public BigDecimal getLineTotal() { 
        return lineTotal; 
    }
    
    public void setLineTotal(BigDecimal lineTotal) { 
        this.lineTotal = lineTotal; 
    }
}
