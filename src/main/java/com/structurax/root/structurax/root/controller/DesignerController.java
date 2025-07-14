package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.Constants.Constants;
import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.DesignDTO;
import com.structurax.root.structurax.root.dto.DesignFullDTO;
import com.structurax.root.structurax.root.service.AdminService;
import com.structurax.root.structurax.root.service.DesignerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@CrossOrigin("http://localhost:5173/")
@RestController
@RequestMapping(value = "/designer")
public class DesignerController {

    @Autowired
    private DesignerService designerService;

    @GetMapping(value = "get_design/{id}" , produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getDesignById(@PathVariable String id){
        try{
            DesignFullDTO design = designerService.getDesignById(id);
            return ResponseEntity.ok(design);
        }catch (Exception e) {
            return new ResponseEntity<>("Error fetching design: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "all_designs" , produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getAllDesigns(){
        try{
            final List designDTOs = designerService.getAllDesigns();
            return ResponseEntity.ok(designDTOs);
        }catch (Exception e) {
            return new ResponseEntity<>("Error fetching designs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "delete_design/{id}")
    public ResponseEntity<?> deleteDesign(@PathVariable String id){
        try{
            DesignDTO design = designerService.deleteDesign(id);
            return ResponseEntity.ok(design);
        }catch(Exception e){
            return new ResponseEntity<>("Error deleting design: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "update_design/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> updateDesign(@PathVariable String id, @RequestBody DesignFullDTO updatedDesign) {
        try {
            // Set the ID from path parameter to ensure consistency
            updatedDesign.setDesignId(id);

            DesignFullDTO design = designerService.updateDesign(id, updatedDesign);
            return ResponseEntity.ok(design);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating design: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "get_clients" , produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> getClientsWithoutPlan(){
        try{
            final List<ClientDTO> clientDTOS = designerService.getClientsWithoutPlan();
            return ResponseEntity.ok(clientDTOS);
        }catch(Exception e){
            return new ResponseEntity<>("Error fetching clients: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "initializing_design" , consumes = Constants.APPLICATION_JSON , produces = Constants.APPLICATION_JSON)
    public ResponseEntity<?> initializingDesign(@RequestBody DesignDTO designDTO){
        try{
            final DesignDTO design = designerService.initializingDesign(designDTO);
            return ResponseEntity.ok(design);
        }catch(Exception e){
            return new ResponseEntity<>("Error initializing design: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
