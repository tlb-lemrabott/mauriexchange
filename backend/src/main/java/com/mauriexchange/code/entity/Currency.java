package com.mauriexchange.code.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    
    private Long id;
    private CurrencyAttributes attributes;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyAttributes {
        
        @JsonProperty("name_fr")
        private String nameFr;
        
        @JsonProperty("name_ar")
        private String nameAr;
        
        private Integer unity;
        private String code;
        
        @JsonProperty("createdAt")
        private LocalDateTime createdAt;
        
        @JsonProperty("updatedAt")
        private LocalDateTime updatedAt;
        
        @JsonProperty("publishedAt")
        private LocalDateTime publishedAt;
        
        @JsonProperty("money_today_changes")
        private MoneyTodayChanges moneyTodayChanges;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoneyTodayChanges {
        private List<ExchangeRate> data;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExchangeRate {
        private Long id;
        private ExchangeRateAttributes attributes;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExchangeRateAttributes {
        private String day;
        private String value;
        
        @JsonProperty("createdAt")
        private LocalDateTime createdAt;
        
        @JsonProperty("updatedAt")
        private LocalDateTime updatedAt;
        
        @JsonProperty("publishedAt")
        private LocalDateTime publishedAt;
        
        @JsonProperty("end_date")
        private String endDate;
    }
}
