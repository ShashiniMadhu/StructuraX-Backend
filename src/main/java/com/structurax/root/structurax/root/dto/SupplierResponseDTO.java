package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor@NoArgsConstructor
public class SupplierResponseDTO {

    private int supplierId;

    private String email;
    private String role="Supplier";
    private String token;
}
