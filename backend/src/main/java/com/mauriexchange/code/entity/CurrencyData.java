package com.mauriexchange.code.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyData {
    
    private List<Currency> data;
    private Object meta; // Using Object for flexibility since meta structure is not fully defined
}
