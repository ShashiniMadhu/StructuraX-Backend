package com.structurax.root.structurax.root.dao;

import java.util.List;

import com.structurax.root.structurax.root.dto.Project1DTO;
import com.structurax.root.structurax.root.dto.ProjectWithClientAndBOQDTO;

public interface QSDAO {
    List<Project1DTO> getProjectsByQSId(String qsId);
    List<ProjectWithClientAndBOQDTO> getProjectsWithClientAndBOQByQSId(String qsId);
}
