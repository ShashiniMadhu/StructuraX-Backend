package com.structurax.root.structurax.root.dao;


import com.structurax.root.structurax.root.dto.LegalDocumentDTO;

import com.structurax.root.structurax.root.dto.LegalProcessDTO;
import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import java.util.List;

public interface LegalOfficerDAO {
    List<ProjectDocumentsDTO> findAllLegalDocuments();

    List<LegalDocumentDTO> findLegalDocumentsByProjectId(String projectId);
    LegalDocumentDTO adddocument(LegalDocumentDTO dto);
    LegalProcessDTO addLegalProcess(LegalProcessDTO dto);
    List<LegalProcessDTO> findLegalProcessesByProjectId(String projectId);
    LegalProcessDTO updateLegalProcess(LegalProcessDTO dto);
    boolean deleteLegalProcess(int id);
    LegalProcessDTO findLegalProcessById(int id);


}
