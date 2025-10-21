package com.structurax.root.structurax.root.service;

import java.util.List;

import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.BOQWithProjectDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;

public interface BOQService {
    String createBOQ(BOQDTO boq);
    void addBOQItem(BOQitemDTO item);
    BOQDTO getBOQById(String boqId);
    BOQWithItemsDTO getBOQWithItemsByProjectId(String projectId);
    List<BOQitemDTO> getBOQItemsByBOQId(String boqId);
    boolean updateBOQWithItems(BOQWithItemsDTO boqWithItems);
    
    // SQS specific methods
    List<BOQWithItemsDTO> getAllBOQs();
    boolean updateBOQStatus(String boqId, BOQDTO.Status status);
    List<BOQWithProjectDTO> getAllBOQsWithProjectInfo();
}
