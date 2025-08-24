package com.mauriexchange.code.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateDto {
    private Long id;
    private String bankName;
    private String bankCode;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private LocalDate rateDate;
    private LocalDateTime rateTimestamp;
    private String source;
    private LocalDateTime createdAt;
}
