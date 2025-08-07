package com.structurax.root.structurax.root.dao;

import java.util.List;

import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithProjectDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;

public interface BOQDAO {
    String insertBOQ(BOQDTO boq);
    void insertBOQItem(BOQitemDTO item);
    BOQDTO getBOQById(String boqId);
    List<BOQitemDTO> getBOQItemsByBOQId(String boqId);
    void updateBOQ(BOQDTO boq);
    void deleteBOQItemsByBOQId(String boqId);
    
    // SQS specific methods
    List<BOQDTO> getAllBOQs();
    boolean updateBOQStatus(String boqId, BOQDTO.Status status);
    List<BOQWithProjectDTO> getAllBOQsWithProjectInfo();
}
