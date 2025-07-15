package com.structurax.root.structurax.root.dao;

import com.structurax.root.structurax.root.dto.ClientDTO;
import com.structurax.root.structurax.root.dto.ProjectDTO;
import com.structurax.root.structurax.root.dto.ProjectInitiateDTO;

import java.util.List;

public interface DirectorDAO {
    ClientDTO createClient (ClientDTO clientDTO,String otp);
    List<ClientDTO> getClientWithPlan();

    List<ClientDTO> getClientWithoutPlan();
    ProjectInitiateDTO initializeProject(ProjectInitiateDTO projectInitiateDTO);
}
