package com.mauriexchange.code.service;

import com.mauriexchange.code.dto.CurrencyConversionRequestDto;
import com.mauriexchange.code.dto.CurrencyConversionResponseDto;
import com.mauriexchange.code.entity.Bank;
import com.mauriexchange.code.entity.CurrencyConversion;
import com.mauriexchange.code.entity.ExchangeRate;
import com.mauriexchange.code.repository.BankRepository;
import com.mauriexchange.code.repository.CurrencyConversionRepository;
import com.mauriexchange.code.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionService {
    
    private final ExchangeRateRepository exchangeRateRepository;
    private final BankRepository bankRepository;
    private final CurrencyConversionRepository currencyConversionRepository;
    
    public CurrencyConversionResponseDto convertCurrency(CurrencyConversionRequestDto request, String userIp, String userAgent) {
        log.info("Converting {} {} to {}", request.getAmount(), request.getFromCurrency(), request.getToCurrency());
        
        // Get the best exchange rate (lowest sell rate for buying, highest buy rate for selling)
        Optional<ExchangeRate> bestRate = getBestExchangeRate(request.getFromCurrency(), request.getToCurrency(), request.getBankCode());
        
        if (bestRate.isEmpty()) {
            throw new RuntimeException("No exchange rate found for " + request.getFromCurrency() + " to " + request.getToCurrency());
        }
        
        ExchangeRate rate = bestRate.get();
        
        // Calculate converted amount (using sell rate for buying foreign currency)
        BigDecimal exchangeRate = rate.getSellRate();
        BigDecimal convertedAmount = request.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        
        // Save conversion record
        CurrencyConversion conversion = CurrencyConversion.builder()
                .fromCurrency(request.getFromCurrency())
                .toCurrency(request.getToCurrency())
                .amount(request.getAmount())
                .convertedAmount(convertedAmount)
                .exchangeRate(exchangeRate)
                .bank(rate.getBank())
                .userIp(userIp)
                .userAgent(userAgent)
                .build();
        
        currencyConversionRepository.save(conversion);
        
        return CurrencyConversionResponseDto.builder()
                .fromCurrency(request.getFromCurrency())
                .toCurrency(request.getToCurrency())
                .amount(request.getAmount())
                .convertedAmount(convertedAmount)
                .exchangeRate(exchangeRate)
                .bankName(rate.getBank().getName())
                .bankCode(rate.getBank().getCode())
                .conversionTime(LocalDateTime.now())
                .rateType("sell")
                .build();
    }
    
    private Optional<ExchangeRate> getBestExchangeRate(String fromCurrency, String toCurrency, String bankCode) {
        if (bankCode != null && !bankCode.isEmpty()) {
            // Get rate from specific bank
            Optional<Bank> bank = bankRepository.findByCode(bankCode);
            if (bank.isPresent()) {
                return exchangeRateRepository.findLatestByBankAndCurrencyPair(bank.get().getId(), fromCurrency, toCurrency);
            }
        }
        
        // Get best rate from all banks (lowest sell rate)
        return exchangeRateRepository.findLatestByCurrencyPair(fromCurrency, toCurrency);
    }
    
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency, String bankCode) {
        Optional<ExchangeRate> rate = getBestExchangeRate(fromCurrency, toCurrency, bankCode);
        return rate.map(ExchangeRate::getSellRate)
                .orElseThrow(() -> new RuntimeException("No exchange rate found for " + fromCurrency + " to " + toCurrency));
    }
}
