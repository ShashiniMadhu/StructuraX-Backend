package com.structurax.root.structurax.root.dto;

import java.time.LocalDate;

public class PurchaseOrderDTO {
    private Integer orderId;
    private String projectId;
    private Integer supplierId;
    private Integer responseId;
    private String paymentStatus; // pending, partial, paid
    private LocalDate estimatedDeliveryDate;
    private LocalDate orderDate;
    private Boolean orderStatus; // 0 or 1 (false or true)
    
    public PurchaseOrderDTO() {}
    
    // Getters and setters
    public Integer getOrderId() { 
        return orderId; 
    }
    
    public void setOrderId(Integer orderId) { 
        this.orderId = orderId; 
    }
    
    public String getProjectId() { 
        return projectId; 
    }
    
    public void setProjectId(String projectId) { 
        this.projectId = projectId; 
    }
    
    public Integer getSupplierId() { 
        return supplierId; 
    }
    
    public void setSupplierId(Integer supplierId) { 
        this.supplierId = supplierId; 
    }
    
    public Integer getResponseId() { 
        return responseId; 
    }
    
    public void setResponseId(Integer responseId) { 
        this.responseId = responseId; 
    }
    
    public String getPaymentStatus() { 
        return paymentStatus; 
    }
    
    public void setPaymentStatus(String paymentStatus) { 
        this.paymentStatus = paymentStatus; 
    }
    
    public LocalDate getEstimatedDeliveryDate() { 
        return estimatedDeliveryDate; 
    }
    
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) { 
        this.estimatedDeliveryDate = estimatedDeliveryDate; 
    }
    
    public LocalDate getOrderDate() { 
        return orderDate; 
    }
    
    public void setOrderDate(LocalDate orderDate) { 
        this.orderDate = orderDate; 
    }
    
    public Boolean getOrderStatus() { 
        return orderStatus; 
    }
    
    public void setOrderStatus(Boolean orderStatus) { 
        this.orderStatus = orderStatus; 
    }
}
