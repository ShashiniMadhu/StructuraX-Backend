package com.structurax.root.structurax.root.service;

import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.ProjectInitiateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DirectorService {

    ClientDTO createClient(ClientDTO clientDTO);
    List<ClientDTO> getClientWithPlan();
    List<ClientDTO> getClientWithoutPlan();
    ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO);

}
