export * from './conversionAPI.service';
import { ConversionAPIService } from './conversionAPI.service';
export * from './currencyExchangeAPI.service';
import { CurrencyExchangeAPIService } from './currencyExchangeAPI.service';
export * from './exchangeRatesAPI.service';
import { ExchangeRatesAPIService } from './exchangeRatesAPI.service';
export const APIS = [ConversionAPIService, CurrencyExchangeAPIService, ExchangeRatesAPIService];
