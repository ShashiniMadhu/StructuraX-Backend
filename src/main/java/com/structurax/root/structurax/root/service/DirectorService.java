package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ClientDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectorService {

    ClientDTO createClient(ClientDTO clientDTO);
    List<ClientDTO> getClientByType(String type);

}
