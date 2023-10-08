package com.bank.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    private String sourceAccountNumber;
    private String recipientAccountNumber;
    private BigDecimal transferredAmount;
}
