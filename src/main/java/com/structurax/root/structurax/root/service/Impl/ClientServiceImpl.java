package com.structurax.root.structurax.root.service.Impl;


import com.structurax.root.structurax.root.dao.ClientDAO;
import com.structurax.root.structurax.root.dao.EmployeeDAO;
import com.structurax.root.structurax.root.dto.*;
import com.structurax.root.structurax.root.service.ClientService;
import com.structurax.root.structurax.root.service.EmployeeService;
import com.structurax.root.structurax.root.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClientDAO clientDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ClientResponseDTO login(ClientLoginDTO loginDTO) {
        ClientOneDTO client = clientDAO.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), client.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateTokenForClient(client.getEmail(), client.getRole(), client.getClientId());

        return new ClientResponseDTO(
                client.getClientId(),

                client.getEmail(),
                client.getRole(),
                token
        );
    }
}
