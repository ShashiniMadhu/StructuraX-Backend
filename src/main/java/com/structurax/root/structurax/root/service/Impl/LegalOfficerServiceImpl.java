package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.LegalOfficerDAO;
import com.structurax.root.structurax.root.dto.LegalDocumentDTO;
import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import com.structurax.root.structurax.root.service.LegalOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegalOfficerServiceImpl implements LegalOfficerService {

    @Autowired
    private LegalOfficerDAO legalOfficerDAO;

    @Override
    public List<ProjectDocumentsDTO> getAllLegalDocuments() {
        return legalOfficerDAO.findAllLegalDocuments();
    }

    @Override
    public LegalDocumentDTO adddocument(LegalDocumentDTO dto){
        return legalOfficerDAO.adddocument(dto);
    }

    @Override
    public List<LegalDocumentDTO> getLegalDocumentsByProjectId(String projectId) {
        return legalOfficerDAO.findLegalDocumentsByProjectId(projectId);
    }

}
