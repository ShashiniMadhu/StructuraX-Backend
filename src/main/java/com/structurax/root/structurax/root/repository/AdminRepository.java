package com.structurax.root.structurax.root.repository;

import com.structurax.root.structurax.root.dto.EmployeeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepository {

    private List<EmployeeDTO> employee = new ArrayList<>();

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO){
        employee.add(employeeDTO);
        return employeeDTO;
    }

    public List<EmployeeDTO> getAllEmployees(){
        return employee;
    }

    public EmployeeDTO getEmployeeById(Integer id){
        Optional<EmployeeDTO> employeeDTOOptional = employee.stream().filter(emp->emp.getEmp_id() == id).findFirst();
        return employeeDTOOptional.orElse(null);
    }

    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO){
        Optional<EmployeeDTO> employeeDTO1 = employee.stream().filter(emp-> emp.getEmp_id().equals(employeeDTO.getEmp_id())).findFirst();
        EmployeeDTO updatedEmployee = employeeDTO1.orElse(null);
        if (updatedEmployee != null) {
            updatedEmployee.setname(employeeDTO.getname());
            // Replace this line:
            // students.add(0, studentDTO);

            // With this line:
            employee.replaceAll(emp -> emp.getEmp_id().equals(employeeDTO.getname()) ? updatedEmployee : emp);
        }
        return updatedEmployee;
    }

    public EmployeeDTO deleteEmployeeById(Integer id){
        EmployeeDTO employeeDTO = employee.get(id);
        employee.remove(employeeDTO);
        return employeeDTO;
    }
}
