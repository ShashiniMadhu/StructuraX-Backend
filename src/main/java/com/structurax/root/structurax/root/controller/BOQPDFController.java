package com.structurax.root.structurax.root.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.structurax.root.structurax.root.service.BOQPDFService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/boq")
public class BOQPDFController {
    @Autowired
    private BOQPDFService boqpdfService;

    /**
     * Download BOQ PDF with enhanced formatting (default)
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadBOQPdf(@PathVariable("id") String boqId) {
        byte[] pdfBytes = boqpdfService.generateBoqPdf(boqId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "boq_" + boqId + "_enhanced.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    /**
     * Download BOQ PDF with different format options
     * @param boqId BOQ ID
     * @param format Format type: "simple", "detailed", "enhanced" (default)
     */
    @GetMapping("/{id}/download/format")
    public ResponseEntity<byte[]> downloadBOQPdfWithFormat(
            @PathVariable("id") String boqId, 
            @RequestParam(value = "type", defaultValue = "enhanced") String format) {
        
        byte[] pdfBytes;
        String filename;
        
        switch (format.toLowerCase()) {
            case "simple":
                pdfBytes = boqpdfService.generateSimpleBoqPdf(boqId);
                filename = "boq_" + boqId + "_simple.pdf";
                break;
            case "detailed":
                pdfBytes = boqpdfService.generateDetailedBoqPdf(boqId);
                filename = "boq_" + boqId + "_detailed.pdf";
                break;
            case "enhanced":
            default:
                pdfBytes = boqpdfService.generateBoqPdf(boqId);
                filename = "boq_" + boqId + "_enhanced.pdf";
                break;
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
