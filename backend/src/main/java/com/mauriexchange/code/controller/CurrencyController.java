package com.mauriexchange.code.controller;

import com.mauriexchange.code.config.PaginationConfig;
import com.mauriexchange.code.dto.ApiResponseDto;
import com.mauriexchange.code.dto.CurrencyResponseDto;
import com.mauriexchange.code.dto.PaginatedResponseDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
@Tag(name = "Currency Exchange API", description = "API for managing currency exchange data")
public class CurrencyController {
    
    private final CurrencyService currencyService;
    private final PaginationConfig paginationConfig;
    
    @GetMapping("/code/{code}")
    @Operation(
        summary = "Get currency by code",
        description = "Retrieve a specific currency by its ISO code (e.g., USD, EUR, NOK)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved currency",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Currency not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDto<CurrencyResponseDto>> getCurrencyByCode(
            @Parameter(description = "Currency code", example = "NOK")
            @PathVariable String code) {
        log.info("Fetching currency with code: {}", code);
        
        Optional<CurrencyResponseDto> currency = currencyService.getCurrencyByCode(code);
        if (currency.isEmpty()) {
            throw new DataNotFoundException("Currency not found with code: " + code);
        }
        
        return ResponseEntity.ok(ApiResponseDto.success(currency.get(), 
                "Successfully retrieved currency"));
    }
}
