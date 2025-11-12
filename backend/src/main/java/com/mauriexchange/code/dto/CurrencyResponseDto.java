package com.mauriexchange.code.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyResponseDto {
    
    private Long id;
    private String nameFr;
    private String nameAr;
    private Integer unity;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private List<ExchangeRateDto> exchangeRates;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExchangeRateDto {
        private Long id;
        private String day;
        private String value;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime publishedAt;
        private String endDate;
    }
}
