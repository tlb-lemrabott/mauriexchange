package com.mauriexchange.code.controller;

import com.mauriexchange.code.dto.ApiResponseDto;
import com.mauriexchange.code.dto.ConversionResponseDto;
import com.mauriexchange.code.exception.BadRequestException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Conversion API", description = "API for currency conversion using latest official rates")
public class ConversionController {

    private final CurrencyService currencyService;

    @GetMapping("/convert")
    @Operation(
            summary = "Convert between currencies",
            description = "Converts an amount from one currency to another using latest official rates"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully converted",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "404", description = "Rate not found for one of the currencies"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDto<ConversionResponseDto>> convert(
            @Parameter(description = "Source currency code", example = "USD")
            @RequestParam String from,
            @Parameter(description = "Target currency code", example = "MRU")
            @RequestParam String to,
            @Parameter(description = "Amount to convert", example = "100")
            @RequestParam Double amount) {
        log.info("Convert from {} to {} amount {}", from, to, amount);

        if (from == null || from.isBlank() || to == null || to.isBlank()) {
            throw new BadRequestException("'from' and 'to' parameters are required");
        }
        if (amount == null || amount < 0) {
            throw new BadRequestException("'amount' must be a non-negative number");
        }

        ConversionResponseDto result = currencyService.convert(from, to, amount);
        return ResponseEntity.ok(ApiResponseDto.success(result));
    }
}

