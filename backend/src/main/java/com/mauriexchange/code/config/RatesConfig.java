package com.mauriexchange.code.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RatesConfig {

    @Value("${app.rates.margin:0.01}")
    private double margin;

    public double getMargin() {
        return margin;
    }
}

