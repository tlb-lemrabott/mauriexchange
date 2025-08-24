package com.mauriexchange.code.service;

import com.mauriexchange.code.dto.ExchangeRateDto;
import com.mauriexchange.code.entity.ExchangeRate;
import com.mauriexchange.code.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {
    
    private final ExchangeRateRepository exchangeRateRepository;
    
    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '-' + #toCurrency + '-' + #date")
    public List<ExchangeRateDto> getExchangeRates(String fromCurrency, String toCurrency, LocalDate date) {
        log.info("Fetching exchange rates for {} to {} on {}", fromCurrency, toCurrency, date);
        
        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyPairAndDate(fromCurrency, toCurrency, date);
        
        return rates.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "latestExchangeRate", key = "#fromCurrency + '-' + #toCurrency")
    public Optional<ExchangeRateDto> getLatestExchangeRate(String fromCurrency, String toCurrency) {
        log.info("Fetching latest exchange rate for {} to {}", fromCurrency, toCurrency);
        
        return exchangeRateRepository.findLatestByCurrencyPair(fromCurrency, toCurrency)
                .map(this::mapToDto);
    }
    
    public List<ExchangeRateDto> getExchangeRatesByDate(LocalDate date) {
        log.info("Fetching all exchange rates for date: {}", date);
        
        List<ExchangeRate> rates = exchangeRateRepository.findByDate(date);
        
        return rates.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<String> getSupportedFromCurrencies() {
        return exchangeRateRepository.findAllFromCurrencies();
    }
    
    public List<String> getSupportedToCurrencies() {
        return exchangeRateRepository.findAllToCurrencies();
    }
    
    private ExchangeRateDto mapToDto(ExchangeRate exchangeRate) {
        return ExchangeRateDto.builder()
                .id(exchangeRate.getId())
                .bankName(exchangeRate.getBank().getName())
                .bankCode(exchangeRate.getBank().getCode())
                .fromCurrency(exchangeRate.getFromCurrency())
                .toCurrency(exchangeRate.getToCurrency())
                .buyRate(exchangeRate.getBuyRate())
                .sellRate(exchangeRate.getSellRate())
                .rateDate(exchangeRate.getRateDate())
                .rateTimestamp(exchangeRate.getRateTimestamp())
                .source(exchangeRate.getSource())
                .createdAt(exchangeRate.getCreatedAt())
                .build();
    }
}
