package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.LegalDocumentDTO;
import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import com.structurax.root.structurax.root.service.LegalOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/legal_officer")

@CrossOrigin(origins = "http://localhost:5174")

public class LegalOfficerController {

    @Autowired
    private LegalOfficerService legalOfficerService;

    @GetMapping("/document")
    public List<ProjectDocumentsDTO> getLegalDocuments() {
        return legalOfficerService.getAllLegalDocuments();
    }

    @PostMapping(value = "/add_document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> adddocument(
            @RequestParam("projectId") String projectId,
            @RequestParam("date") String dateStr,  // changed to String
            @RequestParam("type") String type,
            @RequestParam("description") String description,
            @RequestParam(value = "document_url", required = false) MultipartFile documentUrl
    ) {
        try {
            String imageUrl = null;
            if (documentUrl != null && !documentUrl.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + documentUrl.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) uploadPath.mkdirs();

                File dest = new File(uploadDir + fileName);
                documentUrl.transferTo(dest);
                imageUrl = "/uploads/" + fileName;
            }

            // Parse the date string to LocalDate
            LocalDate date = LocalDate.parse(dateStr); // Make sure frontend sends `yyyy-MM-dd`

            // Construct DTO
            LegalDocumentDTO legalDocumentDTO = new LegalDocumentDTO();
            legalDocumentDTO.setProjectId(projectId);
            legalDocumentDTO.setDate(date);
            legalDocumentDTO.setType(type);
            legalDocumentDTO.setDescription(description);
            legalDocumentDTO.setDocumentUrl(imageUrl);

            LegalDocumentDTO newDocument = legalOfficerService.adddocument(legalDocumentDTO);
            return ResponseEntity.ok(newDocument);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/legal_documents/{projectId}")
    public List<LegalDocumentDTO> getLegalDocumentsByProjectId(@PathVariable String projectId) {
        return legalOfficerService.getLegalDocumentsByProjectId(projectId);
    }



}
