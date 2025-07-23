package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ClientLoginDTO;
import com.structurax.root.structurax.root.dto.ClientOneDTO;
import com.structurax.root.structurax.root.dto.ClientResponseDTO;


public interface ClientService {

    ClientResponseDTO login(ClientLoginDTO clientOneDTO);
}
