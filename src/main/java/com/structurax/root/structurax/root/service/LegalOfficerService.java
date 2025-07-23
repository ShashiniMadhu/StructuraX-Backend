package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.LegalDocumentDTO;

import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import java.util.List;

public interface LegalOfficerService {
    List<ProjectDocumentsDTO> getAllLegalDocuments();

    List<LegalDocumentDTO> getLegalDocumentsByProjectId(String projectId);
    LegalDocumentDTO adddocument(LegalDocumentDTO dto);


}
