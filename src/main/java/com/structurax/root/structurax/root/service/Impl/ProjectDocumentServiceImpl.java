package com.structurax.root.structurax.root.service.Impl;

import com.structurax.root.structurax.root.dao.ProjectDocumentDAO;
import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import com.structurax.root.structurax.root.service.ProjectDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProjectDocumentServiceImpl implements ProjectDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDocumentServiceImpl.class);
    private static final String UPLOAD_DIR = "uploads/project-documents/";

    private final ProjectDocumentDAO projectDocumentDAO;

    public ProjectDocumentServiceImpl(ProjectDocumentDAO projectDocumentDAO) {
        this.projectDocumentDAO = projectDocumentDAO;
        // Create upload directory if it doesn't exist
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Created upload directory: {}", UPLOAD_DIR);
            } else {
                logger.warn("Failed to create upload directory: {}", UPLOAD_DIR);
            }
        }
    }

    @Override
    public List<ProjectDocumentsDTO> getDocumentsByProjectId(String projectId) {
        logger.info("Fetching all documents for project_id: {}", projectId);
        return projectDocumentDAO.getDocumentsByProjectId(projectId);
    }

    @Override
    public ProjectDocumentsDTO getDocumentById(Integer documentId) {
        logger.info("Fetching document with document_id: {}", documentId);
        return projectDocumentDAO.getDocumentById(documentId);
    }

    @Override
    public List<ProjectDocumentsDTO> getDocumentsByProjectIdAndType(String projectId, String type) {
        logger.info("Fetching documents for project_id: {} and type: {}", projectId, type);
        return projectDocumentDAO.getDocumentsByProjectIdAndType(projectId, type);
    }

    @Override
    public ProjectDocumentsDTO uploadDocument(String projectId, String type, String description, MultipartFile file) {
        logger.info("Uploading document for project_id: {}, type: {}", projectId, type);

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = timestamp + "_" + originalFilename;
            String filePath = UPLOAD_DIR + filename;

            // Save file to disk
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            logger.info("File saved successfully: {}", filePath);

            // Create DTO and save to database
            ProjectDocumentsDTO documentDTO = new ProjectDocumentsDTO();
            documentDTO.setProjectId(projectId);
            documentDTO.setDocumentUrl(filePath);
            documentDTO.setType(type);
            documentDTO.setDescription(description);

            return projectDocumentDAO.createDocument(documentDTO);

        } catch (IOException e) {
            logger.error("Error uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateDocument(ProjectDocumentsDTO documentDTO) {
        logger.info("Updating document with document_id: {}", documentDTO.getDocumentId());
        projectDocumentDAO.updateDocument(documentDTO);
    }

    @Override
    public void deleteDocument(Integer documentId) {
        logger.info("Deleting document with document_id: {}", documentId);

        // Get document details to delete the file
        ProjectDocumentsDTO document = projectDocumentDAO.getDocumentById(documentId);

        if (document != null) {
            // Delete file from disk
            try {
                Path filePath = Paths.get(document.getDocumentUrl());
                Files.deleteIfExists(filePath);
                logger.info("File deleted successfully: {}", document.getDocumentUrl());
            } catch (IOException e) {
                logger.error("Error deleting file: {}", e.getMessage(), e);
            }

            // Delete from database
            projectDocumentDAO.deleteDocument(documentId);
        } else {
            logger.warn("Document not found with document_id: {}", documentId);
        }
    }

    @Override
    public String getDocumentFilePath(Integer documentId) {
        logger.info("Fetching file path for document_id: {}", documentId);
        ProjectDocumentsDTO document = projectDocumentDAO.getDocumentById(documentId);

        if (document != null) {
            return document.getDocumentUrl();
        } else {
            logger.warn("Document not found with document_id: {}", documentId);
            return null;
        }
    }
}