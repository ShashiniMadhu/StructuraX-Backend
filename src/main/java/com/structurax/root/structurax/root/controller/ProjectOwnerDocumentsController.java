package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import com.structurax.root.structurax.root.service.ProjectDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project-owner/documents")
public class ProjectOwnerDocumentsController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerDocumentsController.class);
    private final ProjectDocumentService documentService;

    public ProjectOwnerDocumentsController(ProjectDocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Get all documents for a specific project
     * GET /api/project-owner/documents/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> getDocumentsByProject(@PathVariable String projectId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching documents for project_id={}", projectId);
            List<ProjectDocumentsDTO> documents = documentService.getDocumentsByProjectId(projectId);
            response.put("success", true);
            response.put("documents", documents);
            response.put("totalCount", documents.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching documents: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching documents: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get a single document by document ID
     * GET /api/project-owner/documents/{documentId}
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> getDocumentById(@PathVariable Integer documentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching document by document_id={}", documentId);
            ProjectDocumentsDTO document = documentService.getDocumentById(documentId);

            if (document != null) {
                response.put("success", true);
                response.put("document", document);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Document not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("Error fetching document: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get documents by project ID and type
     * GET /api/project-owner/documents/project/{projectId}/type/{type}
     */
    @GetMapping("/project/{projectId}/type/{type}")
    public ResponseEntity<Map<String, Object>> getDocumentsByProjectAndType(
            @PathVariable String projectId,
            @PathVariable String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching documents for project_id={} and type={}", projectId, type);
            List<ProjectDocumentsDTO> documents = documentService.getDocumentsByProjectIdAndType(projectId, type);
            response.put("success", true);
            response.put("documents", documents);
            response.put("totalCount", documents.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching documents: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error fetching documents: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Upload a new document
     * POST /api/project-owner/documents/upload
     *
     * Form-data parameters:
     * - projectId: String
     * - type: String (contract, permit, drawing, legal, specification, invoice, report, other)
     * - description: String
     * - file: MultipartFile
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @RequestParam("projectId") String projectId,
            @RequestParam("type") String type,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Uploading document for project_id={}, type={}", projectId, type);

            ProjectDocumentsDTO document = documentService.uploadDocument(projectId, type, description, file);

            response.put("success", true);
            response.put("message", "Document uploaded successfully");
            response.put("document", document);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error uploading document: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error uploading document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update document details (description only)
     * PUT /api/project-owner/documents/{documentId}
     */
    @PutMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> updateDocument(
            @PathVariable Integer documentId,
            @RequestBody ProjectDocumentsDTO documentDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Updating document with document_id={}", documentId);
            documentDTO.setDocumentId(documentId);
            documentService.updateDocument(documentDTO);

            response.put("success", true);
            response.put("message", "Document updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating document: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error updating document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a document
     * DELETE /api/project-owner/documents/{documentId}
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable Integer documentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Deleting document with document_id={}", documentId);
            documentService.deleteDocument(documentId);

            response.put("success", true);
            response.put("message", "Document deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error deleting document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Download a document file
     * GET /api/project-owner/documents/download/{documentId}
     */
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Integer documentId) {
        try {
            logger.info("Downloading document with document_id={}", documentId);

            String filePath = documentService.getDocumentFilePath(documentId);

            if (filePath == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                String filename = path.getFileName().toString();

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                logger.error("File not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Error downloading document: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error downloading document: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * View/preview a document file
     * GET /api/project-owner/documents/view/{documentId}
     */
    @GetMapping("/view/{documentId}")
    public ResponseEntity<Resource> viewDocument(@PathVariable Integer documentId) {
        try {
            logger.info("Viewing document with document_id={}", documentId);

            String filePath = documentService.getDocumentFilePath(documentId);

            if (filePath == null) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Determine content type based on file extension
                String filename = path.getFileName().toString();
                String contentType = determineContentType(filename);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                logger.error("File not found or not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            logger.error("Error viewing document: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error viewing document: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to determine content type based on file extension
     */
    private String determineContentType(String filename) {
        String lowerFilename = filename.toLowerCase();

        if (lowerFilename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".doc") || lowerFilename.endsWith(".docx")) {
            return "application/msword";
        } else if (lowerFilename.endsWith(".xls") || lowerFilename.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else {
            return "application/octet-stream";
        }
    }
}