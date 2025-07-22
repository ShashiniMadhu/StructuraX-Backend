
package com.structurax.root.structurax.root.dao;

import java.util.List;

import com.structurax.root.structurax.root.dto.Project1DTO;

public interface SQSDAO {
    Project1DTO getProjectById(String id);
    boolean assignQsToProject(String pid, String eid);

    List<com.structurax.root.structurax.root.dao.Impl.SQSDAOImpl.ProjectInfo> getProjectsWithoutQs();
    java.util.List<com.structurax.root.structurax.root.dto.EmployeeDTO> getQSOfficers();
}
