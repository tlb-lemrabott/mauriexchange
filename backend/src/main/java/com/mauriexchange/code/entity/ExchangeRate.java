package com.mauriexchange.code.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
    
    @Column(name = "from_currency", nullable = false, length = 3)
    private String fromCurrency;
    
    @Column(name = "to_currency", nullable = false, length = 3)
    private String toCurrency;
    
    @Column(name = "buy_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal buyRate;
    
    @Column(name = "sell_rate", nullable = false, precision = 10, scale = 4)
    private BigDecimal sellRate;
    
    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;
    
    @Column(name = "rate_timestamp", nullable = false)
    private LocalDateTime rateTimestamp;
    
    @Column(name = "source", length = 100)
    private String source;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
