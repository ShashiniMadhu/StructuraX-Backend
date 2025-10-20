package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProjectDocumentService {

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
     * Upload a new document
     */
    ProjectDocumentsDTO uploadDocument(String projectId, String type, String description, MultipartFile file);

    /**
     * Update document details
     */
    void updateDocument(ProjectDocumentsDTO documentDTO);

    /**
     * Delete a document
     */
    void deleteDocument(Integer documentId);

    /**
     * Get the file path for downloading a document
     */
    String getDocumentFilePath(Integer documentId);
}
