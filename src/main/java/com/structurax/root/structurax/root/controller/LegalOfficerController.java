package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.ProjectDocumentsDTO;
import com.structurax.root.structurax.root.service.LegalOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/legal_officer")
@CrossOrigin(origins = "http://localhost:5173")
public class LegalOfficerController {

    @Autowired
    private LegalOfficerService legalOfficerService;

    @GetMapping("/document")
    public List<ProjectDocumentsDTO> getLegalDocuments() {
        return legalOfficerService.getAllLegalDocuments();
    }

}
