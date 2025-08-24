package com.mauriexchange.code.repository;

import com.mauriexchange.code.entity.CurrencyConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, Long> {
    
    @Query("SELECT cc FROM CurrencyConversion cc WHERE cc.fromCurrency = :fromCurrency AND cc.toCurrency = :toCurrency ORDER BY cc.createdAt DESC")
    List<CurrencyConversion> findByCurrencyPair(@Param("fromCurrency") String fromCurrency, 
                                               @Param("toCurrency") String toCurrency);
    
    @Query("SELECT cc FROM CurrencyConversion cc WHERE cc.userIp = :userIp ORDER BY cc.createdAt DESC")
    List<CurrencyConversion> findByUserIp(@Param("userIp") String userIp);
    
    @Query("SELECT cc FROM CurrencyConversion cc WHERE cc.createdAt >= :since ORDER BY cc.createdAt DESC")
    List<CurrencyConversion> findByCreatedAtSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(cc) FROM CurrencyConversion cc WHERE cc.userIp = :userIp AND cc.createdAt >= :since")
    long countByUserIpAndCreatedAtSince(@Param("userIp") String userIp, @Param("since") LocalDateTime since);
}
