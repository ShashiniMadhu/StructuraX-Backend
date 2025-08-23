package com.structurax.root.structurax.root.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPlanDTO {

    private int paymentPlanId;
    private String projectId;
    private Date createdDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    private double totalAmount;
    private Date startDate;
    private Date endDate;
    private int numberOfInstallments;

    private List<InstallmentDTO> installments;

    public PaymentPlanDTO(int paymentPlanId, String projectId, Date createdDate, double totalAmount, Date startDate, Date endDate, int numberOfInstallments) {
        this.paymentPlanId = paymentPlanId;
        this.projectId = projectId;
        this.createdDate = createdDate;
        this.totalAmount = totalAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfInstallments = numberOfInstallments;
    }


}