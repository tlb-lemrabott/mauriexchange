package com.mauriexchange.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionResponseDto {
    private String from;
    private String to;
    private Double amount;
    private Double rate; // effective rate from->to
    private Double convertedAmount;
    private String date; // reference date of rate(s)
}

