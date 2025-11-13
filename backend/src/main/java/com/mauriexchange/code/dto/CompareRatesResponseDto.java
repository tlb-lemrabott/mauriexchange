package com.mauriexchange.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompareRatesResponseDto {
    private String code;
    private String fromDate;
    private String toDate;
    private Double rateFrom;
    private Double rateTo;
    private String changePercent;
}

