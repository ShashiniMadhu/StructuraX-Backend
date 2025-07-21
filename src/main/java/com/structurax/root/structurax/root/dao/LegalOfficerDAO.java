package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import java.util.List;

public interface LegalOfficerDAO {
    List<ProjectDocumentsDTO> findAllLegalDocuments();
}
