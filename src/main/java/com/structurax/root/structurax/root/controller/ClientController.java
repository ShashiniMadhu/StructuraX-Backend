package com.structurax.root.structurax.root.controller;

import com.structurax.root.structurax.root.dto.ClientLoginDTO;
import com.structurax.root.structurax.root.dto.ClientResponseDTO;

import com.structurax.root.structurax.root.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/login")
    public ResponseEntity<ClientResponseDTO> login(@RequestBody ClientLoginDTO loginDTO) {
        ClientResponseDTO response = clientService.login(loginDTO);
        return ResponseEntity.ok(response);
    }
}
