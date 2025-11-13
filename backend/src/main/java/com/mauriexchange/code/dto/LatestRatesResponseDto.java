package com.mauriexchange.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LatestRatesResponseDto {
    private String date; // latest date across currencies
    private List<Item> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private String code;
        private String name;
        private Double officialRate;
        private Double buyRate;
        private Double sellRate;
        private String change24h; // like +0.12%
    }
}

