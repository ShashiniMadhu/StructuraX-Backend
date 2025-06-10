package com.structurax.root.structurax.root.dto;

public class EmployeeDTO {
    private Integer emp_id;
    private String name;
    private String email;

    public EmployeeDTO(Integer emp_id, String emp_name, String email) {
        this.emp_id = emp_id;
        this.name = emp_name;
        this.email = email;
    }

    public Integer getEmp_id() {
        return emp_id;
    }

    public String getname() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmp_id(Integer emp_id) {
        this.emp_id = emp_id;
    }

    public void setname(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
