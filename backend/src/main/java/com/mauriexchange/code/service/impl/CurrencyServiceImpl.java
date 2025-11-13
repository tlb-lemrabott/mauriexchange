package com.mauriexchange.code.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mauriexchange.code.config.PaginationConfig;
import com.mauriexchange.code.dto.CurrencyResponseDto;
import com.mauriexchange.code.dto.PaginatedResponseDto;
import com.mauriexchange.code.dto.OfficialRateResponseDto;
import com.mauriexchange.code.dto.LatestRatesResponseDto;
import com.mauriexchange.code.dto.ConversionResponseDto;
import com.mauriexchange.code.entity.Currency;
import com.mauriexchange.code.entity.CurrencyData;
import com.mauriexchange.code.exception.DataNotFoundException;
import com.mauriexchange.code.exception.DataProcessingException;
import com.mauriexchange.code.service.CurrencyService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {
    
    private final PaginationConfig paginationConfig;
    
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
    public PaginatedResponseDto<CurrencyResponseDto> getAllCurrenciesPaginated(int page, int size) {
        if (currencyData == null || currencyData.getData() == null) {
            throw new DataNotFoundException("No currency data available");
        }
        
        // Validate and adjust page size
        int validatedSize = Math.min(size, paginationConfig.getMaxPageSize());
        validatedSize = Math.max(validatedSize, 1);
        
        // Validate page number
        int validatedPage = Math.max(page, 0);
        
        List<CurrencyResponseDto> allCurrencies = currencyData.getData().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return createPaginatedResponse(allCurrencies, validatedPage, validatedSize);
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
    public PaginatedResponseDto<CurrencyResponseDto> getCurrenciesByNamePaginated(String name, int page, int size) {
        if (currencyData == null || currencyData.getData() == null) {
            return PaginatedResponseDto.<CurrencyResponseDto>builder()
                    .data(List.of())
                    .metadata(PaginatedResponseDto.PaginationMetadata.builder()
                            .page(page)
                            .size(size)
                            .totalElements(0)
                            .totalPages(0)
                            .hasNext(false)
                            .hasPrevious(false)
                            .isFirst(true)
                            .isLast(true)
                            .build())
                    .build();
        }
        
        // Validate and adjust page size
        int validatedSize = Math.min(size, paginationConfig.getMaxPageSize());
        validatedSize = Math.max(validatedSize, 1);
        
        // Validate page number
        int validatedPage = Math.max(page, 0);
        
        String searchName = name.toLowerCase();
        List<CurrencyResponseDto> filteredCurrencies = currencyData.getData().stream()
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
        
        return createPaginatedResponse(filteredCurrencies, validatedPage, validatedSize);
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

    @Override
    public Optional<OfficialRateResponseDto> getOfficialRateByCodeAndDate(String code, String date) {
        Optional<CurrencyResponseDto> currencyOpt = getCurrencyByCode(code);
        if (currencyOpt.isEmpty()) {
            return Optional.empty();
        }

        List<CurrencyResponseDto.ExchangeRateDto> rates = currencyOpt.get().getExchangeRates();
        if (rates == null || rates.isEmpty()) {
            return Optional.empty();
        }

        return rates.stream()
                .filter(r -> date.equals(r.getDay()))
                .findFirst()
                .flatMap(r -> {
                    try {
                        Double value = r.getValue() != null ? Double.parseDouble(r.getValue()) : null;
                        if (value == null) return Optional.empty();
                        return Optional.of(OfficialRateResponseDto.builder()
                                .code(currencyOpt.get().getCode())
                                .officialRate(value)
                                .date(date)
                                .source("CACHE")
                                .build());
                    } catch (NumberFormatException ex) {
                        log.warn("Unable to parse rate value '{}' for {} on {}", r.getValue(), code, date);
                        return Optional.empty();
                    }
                });
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

    private PaginatedResponseDto<CurrencyResponseDto> createPaginatedResponse(
            List<CurrencyResponseDto> allData, int page, int size) {
        
        long totalElements = allData.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        // Calculate start and end indices
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, (int) totalElements);
        
        // Get the page data
        List<CurrencyResponseDto> pageData = allData.subList(startIndex, endIndex);
        
        // Build metadata
        PaginatedResponseDto.PaginationMetadata metadata = PaginatedResponseDto.PaginationMetadata.builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages - 1)
                .hasPrevious(page > 0)
                .isFirst(page == 0)
                .isLast(page == totalPages - 1 || totalPages == 0)
                .build();
        
        return PaginatedResponseDto.<CurrencyResponseDto>builder()
                .data(pageData)
                .metadata(metadata)
                .build();
    }

    @Override
    public LatestRatesResponseDto getLatestRates(double margin) {
        if (currencyData == null || currencyData.getData() == null) {
            throw new DataNotFoundException("No currency data available");
        }

        java.util.concurrent.atomic.AtomicReference<String> globalLatestDate = new java.util.concurrent.atomic.AtomicReference<>(null);

        List<LatestRatesResponseDto.Item> items = currencyData.getData().stream()
                .map(cur -> {
                    Currency.CurrencyAttributes attrs = cur.getAttributes();
                    List<CurrencyResponseDto.ExchangeRateDto> rates = null;
                    if (attrs != null && attrs.getMoneyTodayChanges() != null &&
                            attrs.getMoneyTodayChanges().getData() != null) {
                        rates = attrs.getMoneyTodayChanges().getData().stream()
                                .map(this::convertExchangeRateToDto)
                                .collect(java.util.stream.Collectors.toList());
                    }

                    if (rates == null || rates.isEmpty()) {
                        return LatestRatesResponseDto.Item.builder()
                                .code(attrs != null ? attrs.getCode() : null)
                                .name(attrs != null ? attrs.getNameFr() : null)
                                .officialRate(null)
                                .buyRate(null)
                                .sellRate(null)
                                .change24h(null)
                                .build();
                    }

                    // Find latest and previous entries by day
                    CurrencyResponseDto.ExchangeRateDto latest = rates.stream()
                            .max(java.util.Comparator.comparing(CurrencyResponseDto.ExchangeRateDto::getDay))
                            .orElse(null);
                    String latestDay = latest != null ? latest.getDay() : null;

                    if (latestDay != null) {
                        globalLatestDate.updateAndGet(prev -> (prev == null || latestDay.compareTo(prev) > 0) ? latestDay : prev);
                    }

                    Double latestVal = null;
                    if (latest != null && latest.getValue() != null) {
                        try { latestVal = Double.parseDouble(latest.getValue()); } catch (NumberFormatException ignored) {}
                    }

                    // previous: max day less than latestDay
                    Double prevVal = null;
                    if (latestDay != null) {
                        prevVal = rates.stream()
                                .filter(r -> r.getDay() != null && r.getDay().compareTo(latestDay) < 0)
                                .max(java.util.Comparator.comparing(CurrencyResponseDto.ExchangeRateDto::getDay))
                                .map(r -> {
                                    try { return r.getValue() != null ? Double.parseDouble(r.getValue()) : null; }
                                    catch (NumberFormatException e) { return null; }
                                })
                                .orElse(null);
                    }

                    Double buy = latestVal != null ? latestVal * (1 - margin) : null;
                    Double sell = latestVal != null ? latestVal * (1 + margin) : null;

                    String change = null;
                    if (latestVal != null && prevVal != null && prevVal != 0.0) {
                        double pct = ((latestVal - prevVal) / prevVal) * 100.0;
                        change = String.format("%+,.2f%%", pct);
                    }

                    return LatestRatesResponseDto.Item.builder()
                            .code(attrs != null ? attrs.getCode() : null)
                            .name(attrs != null ? attrs.getNameFr() : null)
                            .officialRate(latestVal)
                            .buyRate(buy)
                            .sellRate(sell)
                            .change24h(change)
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());

        // If no rates had any dates, fallback to null
        return LatestRatesResponseDto.builder()
                .date(globalLatestDate.get())
                .data(items)
                .build();
    }

    private Optional<RateOnDate> findLatestRateForCode(String code) {
        Optional<CurrencyResponseDto> currencyOpt = getCurrencyByCode(code);
        if (currencyOpt.isEmpty()) return Optional.empty();
        List<CurrencyResponseDto.ExchangeRateDto> rates = currencyOpt.get().getExchangeRates();
        if (rates == null || rates.isEmpty()) return Optional.empty();
        return rates.stream()
                .filter(r -> r.getDay() != null && r.getValue() != null)
                .max(java.util.Comparator.comparing(CurrencyResponseDto.ExchangeRateDto::getDay))
                .flatMap(r -> {
                    try {
                        Double v = Double.parseDouble(r.getValue());
                        return Optional.of(new RateOnDate(v, r.getDay()));
                    } catch (NumberFormatException ex) {
                        return Optional.empty();
                    }
                });
    }

    private record RateOnDate(Double value, String date) {}

    @Override
    public ConversionResponseDto convert(String from, String to, double amount) {
        String fromCode = from.toUpperCase();
        String toCode = to.toUpperCase();

        // MRU is the base unit in our dataset
        Optional<RateOnDate> fromRateOpt = "MRU".equalsIgnoreCase(fromCode) ?
                Optional.of(new RateOnDate(1.0, null)) : findLatestRateForCode(fromCode);
        Optional<RateOnDate> toRateOpt = "MRU".equalsIgnoreCase(toCode) ?
                Optional.of(new RateOnDate(1.0, null)) : findLatestRateForCode(toCode);

        if (fromRateOpt.isEmpty()) {
            throw new DataNotFoundException("No latest official rate found for currency: " + fromCode);
        }
        if (toRateOpt.isEmpty()) {
            throw new DataNotFoundException("No latest official rate found for currency: " + toCode);
        }

        double rateFromMRU = fromRateOpt.get().value(); // MRU per 1 from
        double rateToMRU = toRateOpt.get().value();     // MRU per 1 to

        // Effective rate from -> to
        double rate = rateFromMRU / rateToMRU;
        double converted = amount * rate;

        // pick a reference date: max of the two when present
        String date = null;
        String df = fromRateOpt.get().date();
        String dt = toRateOpt.get().date();
        if (df != null && dt != null) {
            date = df.compareTo(dt) >= 0 ? df : dt;
        } else if (df != null) {
            date = df;
        } else if (dt != null) {
            date = dt;
        }

        return ConversionResponseDto.builder()
                .from(fromCode)
                .to(toCode)
                .amount(amount)
                .rate(rate)
                .convertedAmount(converted)
                .date(date)
                .build();
    }
}
