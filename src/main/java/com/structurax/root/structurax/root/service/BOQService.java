package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;

public interface BOQService {
    String createBOQ(BOQDTO boq);
    void addBOQItem(BOQitemDTO item);
    BOQDTO getBOQById(String boqId);
    List<BOQitemDTO> getBOQItemsByBOQId(String boqId);
    boolean updateBOQWithItems(BOQWithItemsDTO boqWithItems);
}
