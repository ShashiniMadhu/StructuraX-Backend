package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    private String supplier_id;

    private String supplier_name;

    private String address;

    private String phone;

    private Date joined_date;

    private String status;

    private String email;

    private String password;

    private String role="Supplier";
}
