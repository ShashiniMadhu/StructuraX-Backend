package com.structurax.root.structurax.root.dto;

import java.util.List;

public class BOQWithItemsDTO {
    private BOQDTO boq;
    private List<BOQitemDTO> items;

    public BOQWithItemsDTO() {}

    public BOQWithItemsDTO(BOQDTO boq, List<BOQitemDTO> items) {
        this.boq = boq;
        this.items = items;
    }

    public BOQDTO getBoq() {
        return boq;
    }

    public void setBoq(BOQDTO boq) {
        this.boq = boq;
    }

    public List<BOQitemDTO> getItems() {
        return items;
    }

    public void setItems(List<BOQitemDTO> items) {
        this.items = items;
    }
}
