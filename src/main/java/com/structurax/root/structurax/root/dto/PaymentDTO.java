package com.structurax.root.structurax.root.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    @JsonProperty("payment_id")
    private int paymentId;
    @JsonProperty("invoice_id")
    private int invoiceId;
    @JsonProperty("due_date")
    private LocalDate duedate;
    @JsonProperty("paid_date")
    private LocalDate paiddate;
    private String status;
    private Double amount;
}
