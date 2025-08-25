package com.mauriexchange.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mauriexchange.code.dto.CurrencyResponseDto;
import com.mauriexchange.code.entity.Currency;
import com.mauriexchange.code.entity.CurrencyData;
import com.mauriexchange.code.exception.DataNotFoundException;
import com.mauriexchange.code.exception.DataProcessingException;
import com.mauriexchange.code.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CurrencyServiceImpl implements CurrencyService {
    
    @Value("${app.data.source.path}")
    private String dataSourcePath;
    
    private CurrencyData currencyData;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    
    @PostConstruct
    public void init() {
        loadDataFromFile();
    }
    
    private void loadDataFromFile() {
        try {
            log.info("Loading currency data from: {}", dataSourcePath);
            Path path = Paths.get(dataSourcePath);
            
            if (!Files.exists(path)) {
                throw new DataProcessingException("Data source file not found: " + dataSourcePath);
            }
            
            String jsonContent = Files.readString(path);
            currencyData = objectMapper.readValue(jsonContent, CurrencyData.class);
            
            log.info("Successfully loaded {} currencies", 
                    currencyData.getData() != null ? currencyData.getData().size() : 0);
                    
        } catch (IOException e) {
            log.error("Error loading currency data: {}", e.getMessage(), e);
            throw new DataProcessingException("Failed to load currency data", e);
        }
    }
    
    @Override
    public List<CurrencyResponseDto> getAllCurrencies() {
        if (currencyData == null || currencyData.getData() == null) {
            throw new DataNotFoundException("No currency data available");
        }
        
        return currencyData.getData().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<CurrencyResponseDto> getCurrencyById(Long id) {
        if (currencyData == null || currencyData.getData() == null) {
            return Optional.empty();
        }
        
        return currencyData.getData().stream()
                .filter(currency -> currency.getId().equals(id))
                .findFirst()
                .map(this::convertToDto);
    }
    
    @Override
    public Optional<CurrencyResponseDto> getCurrencyByCode(String code) {
        if (currencyData == null || currencyData.getData() == null) {
            return Optional.empty();
        }
        
        return currencyData.getData().stream()
                .filter(currency -> currency.getAttributes() != null && 
                        currency.getAttributes().getCode() != null && 
                        currency.getAttributes().getCode().equalsIgnoreCase(code))
                .findFirst()
                .map(this::convertToDto);
    }
    
    @Override
    public List<CurrencyResponseDto> getCurrenciesByName(String name) {
        if (currencyData == null || currencyData.getData() == null) {
            return List.of();
        }
        
        String searchName = name.toLowerCase();
        return currencyData.getData().stream()
                .filter(currency -> {
                    Currency.CurrencyAttributes attrs = currency.getAttributes();
                    if (attrs == null) return false;
                    
                    return (attrs.getNameFr() != null && 
                           attrs.getNameFr().toLowerCase().contains(searchName)) ||
                           (attrs.getNameAr() != null && 
                           attrs.getNameAr().toLowerCase().contains(searchName));
                })
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CurrencyResponseDto.ExchangeRateDto> getLatestExchangeRates(Long currencyId, int limit) {
        Optional<CurrencyResponseDto> currency = getCurrencyById(currencyId);
        if (currency.isEmpty()) {
            throw new DataNotFoundException("Currency not found with ID: " + currencyId);
        }
        
        List<CurrencyResponseDto.ExchangeRateDto> rates = currency.get().getExchangeRates();
        if (rates == null || rates.isEmpty()) {
            return List.of();
        }
        
        return rates.stream()
                .sorted((r1, r2) -> r2.getDay().compareTo(r1.getDay()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CurrencyResponseDto.ExchangeRateDto> getExchangeRatesByDateRange(
            Long currencyId, String startDate, String endDate) {
        Optional<CurrencyResponseDto> currency = getCurrencyById(currencyId);
        if (currency.isEmpty()) {
            throw new DataNotFoundException("Currency not found with ID: " + currencyId);
        }
        
        List<CurrencyResponseDto.ExchangeRateDto> rates = currency.get().getExchangeRates();
        if (rates == null || rates.isEmpty()) {
            return List.of();
        }
        
        return rates.stream()
                .filter(rate -> {
                    String day = rate.getDay();
                    return day != null && day.compareTo(startDate) >= 0 && day.compareTo(endDate) <= 0;
                })
                .sorted((r1, r2) -> r1.getDay().compareTo(r2.getDay()))
                .collect(Collectors.toList());
    }
    
    private CurrencyResponseDto convertToDto(Currency currency) {
        List<CurrencyResponseDto.ExchangeRateDto> exchangeRates = null;
        Currency.CurrencyAttributes attrs = currency.getAttributes();
        
        if (attrs != null && attrs.getMoneyTodayChanges() != null && 
            attrs.getMoneyTodayChanges().getData() != null) {
            exchangeRates = attrs.getMoneyTodayChanges().getData().stream()
                    .map(this::convertExchangeRateToDto)
                    .collect(Collectors.toList());
        }
        
        return CurrencyResponseDto.builder()
                .id(currency.getId())
                .nameFr(attrs != null ? attrs.getNameFr() : null)
                .nameAr(attrs != null ? attrs.getNameAr() : null)
                .unity(attrs != null ? attrs.getUnity() : null)
                .code(attrs != null ? attrs.getCode() : null)
                .createdAt(attrs != null ? attrs.getCreatedAt() : null)
                .updatedAt(attrs != null ? attrs.getUpdatedAt() : null)
                .publishedAt(attrs != null ? attrs.getPublishedAt() : null)
                .exchangeRates(exchangeRates)
                .build();
    }
    
    private CurrencyResponseDto.ExchangeRateDto convertExchangeRateToDto(
            Currency.ExchangeRate exchangeRate) {
        Currency.ExchangeRateAttributes attrs = exchangeRate.getAttributes();
        
        return CurrencyResponseDto.ExchangeRateDto.builder()
                .id(exchangeRate.getId())
                .day(attrs.getDay())
                .value(attrs.getValue())
                .createdAt(attrs.getCreatedAt())
                .updatedAt(attrs.getUpdatedAt())
                .publishedAt(attrs.getPublishedAt())
                .endDate(attrs.getEndDate())
                .build();
    }
}
