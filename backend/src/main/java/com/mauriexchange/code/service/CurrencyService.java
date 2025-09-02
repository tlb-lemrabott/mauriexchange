package com.mauriexchange.code.service;

import com.mauriexchange.code.dto.CurrencyResponseDto;
import com.mauriexchange.code.dto.PaginatedResponseDto;

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
}
