package com.mauriexchange.code.repository;

import com.mauriexchange.code.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency AND er.rateDate = :rateDate ORDER BY er.rateTimestamp DESC")
    List<ExchangeRate> findByCurrencyPairAndDate(@Param("fromCurrency") String fromCurrency, 
                                                 @Param("toCurrency") String toCurrency, 
                                                 @Param("rateDate") LocalDate rateDate);
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.bank.id = :bankId AND er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency AND er.rateDate = :rateDate ORDER BY er.rateTimestamp DESC")
    List<ExchangeRate> findByBankAndCurrencyPairAndDate(@Param("bankId") Long bankId,
                                                        @Param("fromCurrency") String fromCurrency, 
                                                        @Param("toCurrency") String toCurrency, 
                                                        @Param("rateDate") LocalDate rateDate);
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency ORDER BY er.rateDate DESC, er.rateTimestamp DESC LIMIT 1")
    Optional<ExchangeRate> findLatestByCurrencyPair(@Param("fromCurrency") String fromCurrency, 
                                                   @Param("toCurrency") String toCurrency);
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.bank.id = :bankId AND er.fromCurrency = :fromCurrency AND er.toCurrency = :toCurrency ORDER BY er.rateDate DESC, er.rateTimestamp DESC LIMIT 1")
    Optional<ExchangeRate> findLatestByBankAndCurrencyPair(@Param("bankId") Long bankId,
                                                          @Param("fromCurrency") String fromCurrency, 
                                                          @Param("toCurrency") String toCurrency);
    
    @Query("SELECT DISTINCT er.fromCurrency FROM ExchangeRate er ORDER BY er.fromCurrency")
    List<String> findAllFromCurrencies();
    
    @Query("SELECT DISTINCT er.toCurrency FROM ExchangeRate er ORDER BY er.toCurrency")
    List<String> findAllToCurrencies();
    
    @Query("SELECT er FROM ExchangeRate er WHERE er.rateDate = :date ORDER BY er.bank.name, er.fromCurrency, er.toCurrency")
    List<ExchangeRate> findByDate(@Param("date") LocalDate date);
}
