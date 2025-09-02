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
    
    @Test
    void testGetAllCurrenciesPaginated() {
        // Test pagination functionality
        var paginatedResponse = currencyService.getAllCurrenciesPaginated(0, 10);
        assertNotNull(paginatedResponse);
        assertNotNull(paginatedResponse.getData());
        assertNotNull(paginatedResponse.getMetadata());
        
        // Should have 10 items on first page (or less if total < 10)
        assertTrue(paginatedResponse.getData().size() <= 10);
        assertEquals(0, paginatedResponse.getMetadata().getPage());
        assertEquals(10, paginatedResponse.getMetadata().getSize());
        assertTrue(paginatedResponse.getMetadata().isFirst());
        
        // Test second page
        var secondPage = currencyService.getAllCurrenciesPaginated(1, 10);
        assertNotNull(secondPage);
        assertNotNull(secondPage.getData());
        assertEquals(1, secondPage.getMetadata().getPage());
        assertFalse(secondPage.getMetadata().isFirst());
    }
    
    @Test
    void testSearchCurrenciesByNamePaginated() {
        // Test search pagination functionality
        var paginatedResponse = currencyService.getCurrenciesByNamePaginated("norvégienne", 0, 5);
        assertNotNull(paginatedResponse);
        assertNotNull(paginatedResponse.getData());
        assertNotNull(paginatedResponse.getMetadata());
        
        // Should find at least one currency with "norvégienne" in the name
        assertTrue(paginatedResponse.getData().size() > 0);
        assertEquals(0, paginatedResponse.getMetadata().getPage());
        assertEquals(5, paginatedResponse.getMetadata().getSize());
    }
}
