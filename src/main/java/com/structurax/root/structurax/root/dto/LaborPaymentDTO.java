package com.structurax.root.structurax.root.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaborPaymentDTO {

    private int paymentId;
    private String projectId;
    private BigDecimal amount;
    private String comment;
    private LocalDate date;
    private MultipartFile receipt;
}
