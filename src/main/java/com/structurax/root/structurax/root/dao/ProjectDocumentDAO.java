package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;

import java.util.List;

public interface ProjectDocumentDAO {

    /**
     * Get all documents for a specific project
     */
    List<ProjectDocumentsDTO> getDocumentsByProjectId(String projectId);

    /**
     * Get a single document by document ID
     */
    ProjectDocumentsDTO getDocumentById(Integer documentId);

    /**
     * Get documents by project ID and type
     */
    List<ProjectDocumentsDTO> getDocumentsByProjectIdAndType(String projectId, String type);

    /**
     * Upload/create a new document
     */
    ProjectDocumentsDTO createDocument(ProjectDocumentsDTO documentDTO);

    /**
     * Update document details (description, status)
     */
    void updateDocument(ProjectDocumentsDTO documentDTO);

    /**
     * Delete a document
     */
    void deleteDocument(Integer documentId);
}
