package com.mauriexchange.code.controller;

import com.mauriexchange.code.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    void getSupportedFromCurrencies_ShouldReturnCurrencies() throws Exception {
        when(exchangeRateService.getSupportedFromCurrencies())
                .thenReturn(Arrays.asList("USD", "EUR", "GBP"));

        mockMvc.perform(get("/api/v1/exchange-rates/currencies/from"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("USD"))
                .andExpect(jsonPath("$.data[1]").value("EUR"))
                .andExpect(jsonPath("$.data[2]").value("GBP"));
    }

    @Test
    void getSupportedToCurrencies_ShouldReturnCurrencies() throws Exception {
        when(exchangeRateService.getSupportedToCurrencies())
                .thenReturn(Arrays.asList("MRU", "XOF"));

        mockMvc.perform(get("/api/v1/exchange-rates/currencies/to"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").value("MRU"))
                .andExpect(jsonPath("$.data[1]").value("XOF"));
    }
}
