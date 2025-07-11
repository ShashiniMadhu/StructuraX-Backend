package com.structurax.root.structurax.root.dto;

public class SiteResourceDTO {

    private Integer id;
    private String materialName;
    private Integer quantity;
    private String priority;
    private Integer requestId;

    public SiteResourceDTO() {}

    // ✅ Proper full constructor
    public SiteResourceDTO(int id, String materialName, int quantity, String priority, int requestId) {
        this.id = id;
        this.materialName = materialName;
        this.quantity = quantity;
        this.priority = priority;
        this.requestId = requestId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }
}
