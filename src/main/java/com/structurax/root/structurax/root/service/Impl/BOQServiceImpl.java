package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.BOQDAO;
import com.structurax.root.structurax.root.dto.BOQDTO;
import com.structurax.root.structurax.root.dto.BOQWithItemsDTO;
import com.structurax.root.structurax.root.dto.BOQitemDTO;
import com.structurax.root.structurax.root.service.BOQService;

@Service
public class BOQServiceImpl implements BOQService {
    @Autowired
    private BOQDAO boqDAO;

    @Override
    public String createBOQ(BOQDTO boq) {
        return boqDAO.insertBOQ(boq);
    }

    @Override
    public void addBOQItem(BOQitemDTO item) {
        boqDAO.insertBOQItem(item);
    }

    @Override
    public BOQDTO getBOQById(String boqId) {
        return boqDAO.getBOQById(boqId);
    }

    @Override
    public List<BOQitemDTO> getBOQItemsByBOQId(String boqId) {
        return boqDAO.getBOQItemsByBOQId(boqId);
    }

    @Override
    public boolean updateBOQWithItems(BOQWithItemsDTO boqWithItems) {
        try {
            boqDAO.updateBOQ(boqWithItems.getBoq());
            boqDAO.deleteBOQItemsByBOQId(boqWithItems.getBoq().getBoqId());
            if (boqWithItems.getItems() != null) {
                for (BOQitemDTO item : boqWithItems.getItems()) {
                    item.setBoqId(boqWithItems.getBoq().getBoqId());
                    boqDAO.insertBOQItem(item);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
