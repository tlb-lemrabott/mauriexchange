package com.mauriexchange.code.service;

import com.mauriexchange.code.dto.CurrencyResponseDto;
import com.mauriexchange.code.dto.PaginatedResponseDto;
import com.mauriexchange.code.dto.OfficialRateResponseDto;
import com.mauriexchange.code.dto.LatestRatesResponseDto;
import com.mauriexchange.code.dto.ConversionResponseDto;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    
    /**
     * Get all currencies
     * @return List of all currencies
     */
    List<CurrencyResponseDto> getAllCurrencies();
    
    /**
     * Get all currencies with pagination
     * @param page Page number (0-based)
     * @param size Page size
     * @return Paginated response containing currencies
     */
    PaginatedResponseDto<CurrencyResponseDto> getAllCurrenciesPaginated(int page, int size);
    
    /**
     * Get currency by ID
     * @param id Currency ID
     * @return Optional containing currency if found
     */
    Optional<CurrencyResponseDto> getCurrencyById(Long id);
    
    /**
     * Get currency by code
     * @param code Currency code (e.g., USD, EUR)
     * @return Optional containing currency if found
     */
    Optional<CurrencyResponseDto> getCurrencyByCode(String code);
    
    /**
     * Get currencies by name (French or Arabic)
     * @param name Name to search for
     * @return List of matching currencies
     */
    List<CurrencyResponseDto> getCurrenciesByName(String name);
    
    /**
     * Get currencies by name with pagination
     * @param name Name to search for
     * @param page Page number (0-based)
     * @param size Page size
     * @return Paginated response containing matching currencies
     */
    PaginatedResponseDto<CurrencyResponseDto> getCurrenciesByNamePaginated(String name, int page, int size);
    
    /**
     * Get latest exchange rates for a specific currency
     * @param currencyId Currency ID
     * @param limit Number of latest rates to return
     * @return List of exchange rates
     */
    List<CurrencyResponseDto.ExchangeRateDto> getLatestExchangeRates(Long currencyId, int limit);
    
    /**
     * Get exchange rates for a specific date range
     * @param currencyId Currency ID
     * @param startDate Start date (YYYY-MM-DD)
     * @param endDate End date (YYYY-MM-DD)
     * @return List of exchange rates
     */
    List<CurrencyResponseDto.ExchangeRateDto> getExchangeRatesByDateRange(
            Long currencyId, String startDate, String endDate);

    /**
     * Get official rate for a currency code at a specific date.
     * @param code ISO currency code
     * @param date Date in YYYY-MM-DD
     * @return Optional response containing official rate info
     */
    Optional<OfficialRateResponseDto> getOfficialRateByCodeAndDate(String code, String date);

    /**
     * Get latest rates for all currencies and compute buy/sell and 24h change.
     * @param margin margin fraction (e.g., 0.01 for ±1%)
     * @return Aggregated response with latest date and items
     */
    LatestRatesResponseDto getLatestRates(double margin);

    /**
     * Convert amount from one currency to another using latest available official rates.
     * If either currency is MRU, its rate is considered 1 by definition.
     */
    ConversionResponseDto convert(String from, String to, double amount);
}
