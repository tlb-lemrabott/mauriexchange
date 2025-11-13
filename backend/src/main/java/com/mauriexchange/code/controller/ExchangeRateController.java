package com.mauriexchange.code.controller;

import com.mauriexchange.code.dto.ApiResponseDto;
import com.mauriexchange.code.dto.OfficialRateResponseDto;
import com.mauriexchange.code.dto.LatestRatesResponseDto;
import com.mauriexchange.code.config.RatesConfig;
import com.mauriexchange.code.dto.ConversionResponseDto;
import com.mauriexchange.code.exception.BadRequestException;
import com.mauriexchange.code.exception.DataNotFoundException;
import com.mauriexchange.code.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Exchange Rates API", description = "API for official exchange rate lookups")
public class ExchangeRateController {

    private final CurrencyService currencyService;
    private final RatesConfig ratesConfig;

    @GetMapping("/{code}")
    @Operation(
            summary = "Get official rate by date",
            description = "Fetch official rate for a currency code at a specific date (YYYY-MM-DD)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved official rate",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "404", description = "Rate not found for given code/date"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDto<OfficialRateResponseDto>> getOfficialRateByDate(
            @Parameter(description = "Currency code", example = "USD")
            @PathVariable String code,
            @Parameter(description = "Date (YYYY-MM-DD)", example = "2025-10-18")
            @RequestParam String date) {
        log.info("Fetching official rate for code: {} at date: {}", code, date);

        // Validate date format
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Invalid date format. Expected YYYY-MM-DD");
        }

        Optional<OfficialRateResponseDto> result = currencyService.getOfficialRateByCodeAndDate(code, date);
        if (result.isEmpty()) {
            throw new DataNotFoundException("Official rate not found for code: " + code + " at date: " + date);
        }

        // If in future we fetch live BCM, service can set source to "BCM".
        return ResponseEntity.ok(ApiResponseDto.success(result.get()));
    }

    @GetMapping("/latest")
    @Operation(
            summary = "Get latest rates for all currencies",
            description = "Returns all currencies with their latest official rate and derived buy/sell rates"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved latest rates",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDto<LatestRatesResponseDto>> getLatestRates() {
        double margin = ratesConfig.getMargin();
        LatestRatesResponseDto payload = currencyService.getLatestRates(margin);
        return ResponseEntity.ok(ApiResponseDto.success(payload));
    }

}
