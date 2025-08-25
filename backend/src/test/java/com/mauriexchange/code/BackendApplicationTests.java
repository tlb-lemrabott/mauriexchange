package com.mauriexchange.code;

import com.mauriexchange.code.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.data.source.path=../database/bcm-source_db.json"
})
class BackendApplicationTests {

    @Autowired
    private CurrencyService currencyService;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }

    @Test
    void testGetAllCurrencies() {
        // Test that we can retrieve all currencies
        var currencies = currencyService.getAllCurrencies();
        assertNotNull(currencies);
        assertFalse(currencies.isEmpty());
    }

    @Test
    void testGetCurrencyByCode() {
        // Test getting currency by code (NOK should exist based on the sample data)
        var currency = currencyService.getCurrencyByCode("NOK");
        assertTrue(currency.isPresent());
        assertEquals("NOK", currency.get().getCode());
        assertEquals("Couronne norvégienne", currency.get().getNameFr());
    }
}
