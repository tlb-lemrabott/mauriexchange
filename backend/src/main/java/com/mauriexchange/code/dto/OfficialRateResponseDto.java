package com.mauriexchange.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficialRateResponseDto {
    private String code;
    private Double officialRate;
    private String date;
    private String source; // e.g., BCM or CACHE
}

