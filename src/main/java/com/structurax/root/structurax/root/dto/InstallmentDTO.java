package com.structurax.root.structurax.root.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
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

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private double amount;

    @Future(message = "Due date must be in the future")
    private Date dueDate;
    private String status; // Default: "Pending"
    private Date paidDate;

}
