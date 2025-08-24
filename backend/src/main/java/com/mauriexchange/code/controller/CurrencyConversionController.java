package com.mauriexchange.code.controller;

import com.mauriexchange.code.dto.ApiResponseDto;
import com.mauriexchange.code.dto.CurrencyConversionRequestDto;
import com.mauriexchange.code.dto.CurrencyConversionResponseDto;
import com.mauriexchange.code.service.CurrencyConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/convert")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Currency Conversion", description = "APIs for currency conversion calculations")
public class CurrencyConversionController {
    
    private final CurrencyConversionService currencyConversionService;
    
    @PostMapping
    @Operation(summary = "Convert currency using current exchange rates")
    public ResponseEntity<ApiResponseDto<CurrencyConversionResponseDto>> convertCurrency(
            @Valid @RequestBody CurrencyConversionRequestDto request,
            HttpServletRequest httpRequest) {
        
        log.info("POST /convert - fromCurrency: {}, toCurrency: {}, amount: {}", 
                request.getFromCurrency(), request.getToCurrency(), request.getAmount());
        
        String userIp = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        try {
            CurrencyConversionResponseDto response = currencyConversionService.convertCurrency(request, userIp, userAgent);
            return ResponseEntity.ok(ApiResponseDto.success(response, "Currency converted successfully"));
        } catch (Exception e) {
            log.error("Error converting currency: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to convert currency: " + e.getMessage()));
        }
    }
    
    @GetMapping("/rate")
    @Operation(summary = "Get current exchange rate for a currency pair")
    public ResponseEntity<ApiResponseDto<Object>> getExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam(required = false) String bankCode) {
        
        log.info("GET /convert/rate - fromCurrency: {}, toCurrency: {}, bankCode: {}", 
                fromCurrency, toCurrency, bankCode);
        
        try {
            var rate = currencyConversionService.getExchangeRate(fromCurrency, toCurrency, bankCode);
            return ResponseEntity.ok(ApiResponseDto.success(rate, "Exchange rate retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting exchange rate: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Failed to get exchange rate: " + e.getMessage()));
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
