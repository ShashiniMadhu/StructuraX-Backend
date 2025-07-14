package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ClientDTO;

import java.util.List;

public interface DirectorDAO {
    ClientDTO createClient (ClientDTO clientDTO,String otp);
    List<ClientDTO> getClientByType(String type);
}
