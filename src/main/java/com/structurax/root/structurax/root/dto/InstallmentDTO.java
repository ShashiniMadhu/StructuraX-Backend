package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;  // Use java.sql.Date consistently

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDTO {
    private int installmentId;
    private int paymentPlanId;
    //private String milestone;
    private double amount;
    private Date dueDate;
    private String status; // Default: "Pending"
    private Date paidDate;

}
