package com.chrisb97.warehouse.service;

import com.chrisb97.warehouse.exception.ValidationException;

public final class StockRules {
    private StockRules() { }
    public static int stockOutResult(int available,int requested){if(requested<=0)throw new ValidationException("Quantity must be greater than zero.");if(requested>available)throw new ValidationException("Requested quantity exceeds available stock.");return available-requested;}
}
