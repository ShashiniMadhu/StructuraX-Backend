package com.structurax.root.structurax.root.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmationDTO {

    @JsonProperty("confirmation_id")
    private int confirmationId;
    @JsonProperty("payment_id")
    private int paymentId;
    @JsonProperty("project_id")
    private String projectId;
    private Double amount;
    @JsonProperty("document_id")
    private int documentId;
    private String status; // e.g., "Pending", "Approved", "Rejected"
    @JsonProperty("confirmation_date")
    private LocalDate confirmationDate;

    private List<ProjectDocumentsDTO> documents;
    private PaymentDTO payments;
}
