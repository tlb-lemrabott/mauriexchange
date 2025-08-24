package com.mauriexchange.code.controller;

import com.mauriexchange.code.dto.ApiResponseDto;
import com.mauriexchange.code.dto.ExchangeRateDto;
import com.mauriexchange.code.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Exchange Rates", description = "APIs for managing and retrieving exchange rates")
public class ExchangeRateController {
    
    private final ExchangeRateService exchangeRateService;
    
    @GetMapping
    @Operation(summary = "Get exchange rates for a currency pair on a specific date")
    public ResponseEntity<ApiResponseDto<List<ExchangeRateDto>>> getExchangeRates(
            @Parameter(description = "Source currency code (e.g., USD, EUR)")
            @RequestParam String fromCurrency,
            
            @Parameter(description = "Target currency code (e.g., MRU)")
            @RequestParam String toCurrency,
            
            @Parameter(description = "Date for exchange rates (format: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /exchange-rates - fromCurrency: {}, toCurrency: {}, date: {}", fromCurrency, toCurrency, date);
        
        List<ExchangeRateDto> rates = exchangeRateService.getExchangeRates(fromCurrency, toCurrency, date);
        
        return ResponseEntity.ok(ApiResponseDto.success(rates, "Exchange rates retrieved successfully"));
    }
    
    @GetMapping("/latest")
    @Operation(summary = "Get the latest exchange rate for a currency pair")
    public ResponseEntity<ApiResponseDto<ExchangeRateDto>> getLatestExchangeRate(
            @Parameter(description = "Source currency code (e.g., USD, EUR)")
            @RequestParam String fromCurrency,
            
            @Parameter(description = "Target currency code (e.g., MRU)")
            @RequestParam String toCurrency) {
        
        log.info("GET /exchange-rates/latest - fromCurrency: {}, toCurrency: {}", fromCurrency, toCurrency);
        
        return exchangeRateService.getLatestExchangeRate(fromCurrency, toCurrency)
                .map(rate -> ResponseEntity.ok(ApiResponseDto.success(rate, "Latest exchange rate retrieved successfully")))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/date/{date}")
    @Operation(summary = "Get all exchange rates for a specific date")
    public ResponseEntity<ApiResponseDto<List<ExchangeRateDto>>> getExchangeRatesByDate(
            @Parameter(description = "Date for exchange rates (format: yyyy-MM-dd)")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.info("GET /exchange-rates/date/{}", date);
        
        List<ExchangeRateDto> rates = exchangeRateService.getExchangeRatesByDate(date);
        
        return ResponseEntity.ok(ApiResponseDto.success(rates, "Exchange rates for date retrieved successfully"));
    }
    
    @GetMapping("/currencies/from")
    @Operation(summary = "Get all supported source currencies")
    public ResponseEntity<ApiResponseDto<List<String>>> getSupportedFromCurrencies() {
        log.info("GET /exchange-rates/currencies/from");
        
        List<String> currencies = exchangeRateService.getSupportedFromCurrencies();
        
        return ResponseEntity.ok(ApiResponseDto.success(currencies, "Supported source currencies retrieved successfully"));
    }
    
    @GetMapping("/currencies/to")
    @Operation(summary = "Get all supported target currencies")
    public ResponseEntity<ApiResponseDto<List<String>>> getSupportedToCurrencies() {
        log.info("GET /exchange-rates/currencies/to");
        
        List<String> currencies = exchangeRateService.getSupportedToCurrencies();
        
        return ResponseEntity.ok(ApiResponseDto.success(currencies, "Supported target currencies retrieved successfully"));
    }
}
