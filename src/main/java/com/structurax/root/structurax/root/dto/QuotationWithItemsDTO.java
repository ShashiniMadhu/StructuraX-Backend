package com.structurax.root.structurax.root.dto;

import java.util.List;

/**
 * DTO for Quotation with its items
 */
public class QuotationWithItemsDTO {
    private QuotationDTO quotation;
    private List<QuotationItemDTO> items;
    
    public QuotationWithItemsDTO() {}
    
    public QuotationWithItemsDTO(QuotationDTO quotation, List<QuotationItemDTO> items) {
        this.quotation = quotation;
        this.items = items;
    }
    
    // Getters and setters
    public QuotationDTO getQuotation() {
        return quotation;
    }
    
    public void setQuotation(QuotationDTO quotation) {
        this.quotation = quotation;
    }
    
    public List<QuotationItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<QuotationItemDTO> items) {
        this.items = items;
    }
    
    @Override
    public String toString() {
        return "QuotationWithItemsDTO{" +
                "quotation=" + quotation +
                ", items=" + items +
                '}';
    }
}
