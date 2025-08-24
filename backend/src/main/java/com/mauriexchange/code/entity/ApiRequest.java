package com.mauriexchange.code.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "endpoint", nullable = false)
    private String endpoint;
    
    @Column(name = "method", nullable = false, length = 10)
    private String method;
    
    @Column(name = "user_ip", length = 45)
    private String userIp;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "response_time_ms")
    private Integer responseTimeMs;
    
    @Column(name = "status_code")
    private Integer statusCode;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
