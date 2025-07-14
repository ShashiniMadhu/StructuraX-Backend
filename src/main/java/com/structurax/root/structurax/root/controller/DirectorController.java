package com.structurax.root.structurax.root.controller;


import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.service.DirectorService;
import com.structurax.root.structurax.root.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/director")
@CrossOrigin("http://localhost:5173/")

public class DirectorController {

    @Autowired
    private DirectorService directorService;

    @Autowired
    private MailService mailService;

    @PostMapping("/add_client")
    public String addClient(@RequestBody ClientDTO clientDTO) {
        directorService.createClient( clientDTO);
        return "Client registered successfully";

    }





}
