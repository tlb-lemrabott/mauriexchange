//package com.mauriexchange.code.controller;
//
//
//import com.mauriexchange.code.config.PaginationConfig;
//import com.mauriexchange.code.dto.ApiResponseDto;
//import com.mauriexchange.code.dto.CurrencyResponseDto;
//import com.mauriexchange.code.dto.PaginatedResponseDto;
//import com.mauriexchange.code.exception.DataNotFoundException;
//import com.mauriexchange.code.service.CurrencyService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/currencies")
//@RequiredArgsConstructor
//@Tag(name = "Currency Exchange API", description = "API for managing currency exchange data")
//public class DetailedCurrencyController {
//
//    private final CurrencyService currencyService;
//    private final PaginationConfig paginationConfig;
//
//    @GetMapping
//    @Operation(
//            summary = "Get all currencies",
//            description = "Retrieve all available currencies with their exchange rate history"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved currencies",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "404", description = "No currency data available"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<List<CurrencyResponseDto>>> getAllCurrencies() {
//        log.info("Fetching all currencies");
//        List<CurrencyResponseDto> currencies = currencyService.getAllCurrencies();
//        return ResponseEntity.ok(ApiResponseDto.success(currencies,
//                "Successfully retrieved " + currencies.size() + " currencies"));
//    }
//
//    @GetMapping("/paginated")
//    @Operation(
//            summary = "Get all currencies with pagination",
//            description = "Retrieve currencies with pagination support. Page numbers are 0-based."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved currencies",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "404", description = "No currency data available"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<PaginatedResponseDto<CurrencyResponseDto>>> getAllCurrenciesPaginated(
//            @Parameter(description = "Page number (0-based)", example = "0")
//            @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Page size", example = "10")
//            @RequestParam(defaultValue = "20") int size) {
//        log.info("Fetching currencies with pagination - page: {}, size: {}", page, size);
//
//        // Use default page size from configuration if not specified
//        int pageSize = size > 0 ? size : paginationConfig.getDefaultPageSize();
//
//        PaginatedResponseDto<CurrencyResponseDto> paginatedResponse =
//                currencyService.getAllCurrenciesPaginated(page, pageSize);
//
//        return ResponseEntity.ok(ApiResponseDto.success(paginatedResponse,
//                "Successfully retrieved currencies with pagination"));
//    }
//
//    @GetMapping("/{id}")
//    @Operation(
//            summary = "Get currency by ID",
//            description = "Retrieve a specific currency by its unique identifier"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved currency",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "404", description = "Currency not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<CurrencyResponseDto>> getCurrencyById(
//            @Parameter(description = "Currency ID", example = "44")
//            @PathVariable Long id) {
//        log.info("Fetching currency with ID: {}", id);
//
//        Optional<CurrencyResponseDto> currency = currencyService.getCurrencyById(id);
//        if (currency.isEmpty()) {
//            throw new DataNotFoundException("Currency not found with ID: " + id);
//        }
//
//        return ResponseEntity.ok(ApiResponseDto.success(currency.get(),
//                "Successfully retrieved currency"));
//    }
//
//    @GetMapping("/code/{code}")
//    @Operation(
//            summary = "Get currency by code",
//            description = "Retrieve a specific currency by its ISO code (e.g., USD, EUR, NOK)"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved currency",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "404", description = "Currency not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<CurrencyResponseDto>> getCurrencyByCode(
//            @Parameter(description = "Currency code", example = "NOK")
//            @PathVariable String code) {
//        log.info("Fetching currency with code: {}", code);
//
//        Optional<CurrencyResponseDto> currency = currencyService.getCurrencyByCode(code);
//        if (currency.isEmpty()) {
//            throw new DataNotFoundException("Currency not found with code: " + code);
//        }
//
//        return ResponseEntity.ok(ApiResponseDto.success(currency.get(),
//                "Successfully retrieved currency"));
//    }
//
//    @GetMapping("/search")
//    @Operation(
//            summary = "Search currencies by name",
//            description = "Search for currencies by their French or Arabic name"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved currencies",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<List<CurrencyResponseDto>>> searchCurrenciesByName(
//            @Parameter(description = "Name to search for", example = "norvégienne")
//            @RequestParam String name) {
//        log.info("Searching currencies with name: {}", name);
//
//        List<CurrencyResponseDto> currencies = currencyService.getCurrenciesByName(name);
//        return ResponseEntity.ok(ApiResponseDto.success(currencies,
//                "Found " + currencies.size() + " currencies matching '" + name + "'"));
//    }
//
//    @GetMapping("/search/paginated")
//    @Operation(
//            summary = "Search currencies by name with pagination",
//            description = "Search for currencies by their French or Arabic name with pagination support"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved currencies",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<PaginatedResponseDto<CurrencyResponseDto>>> searchCurrenciesByNamePaginated(
//            @Parameter(description = "Name to search for", example = "norvégienne")
//            @RequestParam String name,
//            @Parameter(description = "Page number (0-based)", example = "0")
//            @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Page size", example = "10")
//            @RequestParam(defaultValue = "20") int size) {
//        log.info("Searching currencies with name: {} - page: {}, size: {}", name, page, size);
//
//        // Use default page size from configuration if not specified
//        int pageSize = size > 0 ? size : paginationConfig.getDefaultPageSize();
//
//        PaginatedResponseDto<CurrencyResponseDto> paginatedResponse =
//                currencyService.getCurrenciesByNamePaginated(name, page, pageSize);
//
//        return ResponseEntity.ok(ApiResponseDto.success(paginatedResponse,
//                "Found currencies matching '" + name + "' with pagination"));
//    }
//
//    @GetMapping("/{currencyId}/exchange-rates/latest")
//    @Operation(
//            summary = "Get latest exchange rates",
//            description = "Retrieve the most recent exchange rates for a specific currency"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rates",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "404", description = "Currency not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<List<CurrencyResponseDto.ExchangeRateDto>>> getLatestExchangeRates(
//            @Parameter(description = "Currency ID", example = "44")
//            @PathVariable Long currencyId,
//            @Parameter(description = "Number of latest rates to return", example = "10")
//            @RequestParam(defaultValue = "10") int limit) {
//        log.info("Fetching latest {} exchange rates for currency ID: {}", limit, currencyId);
//
//        List<CurrencyResponseDto.ExchangeRateDto> rates =
//                currencyService.getLatestExchangeRates(currencyId, limit);
//
//        return ResponseEntity.ok(ApiResponseDto.success(rates,
//                "Successfully retrieved " + rates.size() + " latest exchange rates"));
//    }
//
//    @GetMapping("/{currencyId}/exchange-rates/range")
//    @Operation(
//            summary = "Get exchange rates by date range",
//            description = "Retrieve exchange rates for a specific currency within a date range"
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rates",
//                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
//            @ApiResponse(responseCode = "404", description = "Currency not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<ApiResponseDto<List<CurrencyResponseDto.ExchangeRateDto>>> getExchangeRatesByDateRange(
//            @Parameter(description = "Currency ID", example = "44")
//            @PathVariable Long currencyId,
//            @Parameter(description = "Start date (YYYY-MM-DD)", example = "2016-06-14")
//            @RequestParam String startDate,
//            @Parameter(description = "End date (YYYY-MM-DD)", example = "2016-06-16")
//            @RequestParam String endDate) {
//        log.info("Fetching exchange rates for currency ID: {} from {} to {}",
//                currencyId, startDate, endDate);
//
//        List<CurrencyResponseDto.ExchangeRateDto> rates =
//                currencyService.getExchangeRatesByDateRange(currencyId, startDate, endDate);
//
//        return ResponseEntity.ok(ApiResponseDto.success(rates,
//                "Successfully retrieved " + rates.size() + " exchange rates for the specified date range"));
//    }
//}
