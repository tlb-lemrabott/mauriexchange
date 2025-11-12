package com.mauriexchange.code.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.pagination")
public class PaginationConfig {
    
    private int defaultPageSize = 20;
    private int maxPageSize = 100;
}

