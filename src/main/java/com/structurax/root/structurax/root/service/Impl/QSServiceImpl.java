package com.structurax.root.structurax.root.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.structurax.root.structurax.root.dao.QSDAO;
import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientAndBOQDTO;
import com.structurax.root.structurax.root.dto.RequestSiteResourcesDTO;
import com.structurax.root.structurax.root.service.QSService;

@Service
public class QSServiceImpl implements QSService {

    @Autowired
    private QSDAO qsdao;

    @Override
    public List<Project1DTO> getProjectsByQSId(String qsId) {
        return qsdao.getProjectsByQSId(qsId);
    }
    
    @Override
    public List<ProjectWithClientAndBOQDTO> getProjectsWithClientAndBOQByQSId(String qsId) {
        return qsdao.getProjectsWithClientAndBOQByQSId(qsId);
    }
    
    @Override
    public List<RequestSiteResourcesDTO> getRequestsByQSId(String qsId) {
        return qsdao.getRequestsByQSId(qsId);
    }
    
    @Override
    public boolean updateRequestQSApproval(Integer requestId, String qsApproval) {
        return qsdao.updateRequestQSApproval(requestId, qsApproval);
    }
}
