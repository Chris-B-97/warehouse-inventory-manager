package com.chrisb97.warehouse.validation;

import com.chrisb97.warehouse.exception.ValidationException;
import com.chrisb97.warehouse.model.Product;

public final class ProductValidator {
    private ProductValidator() { }
    public static void validate(Product product) {
        if(product==null) throw new ValidationException("Product is required.");
        if(product.getSku()==null||product.getSku().isBlank()) throw new ValidationException("SKU is required.");
        if(product.getName()==null||product.getName().isBlank()) throw new ValidationException("Product name is required.");
        if(product.getQuantity()<0) throw new ValidationException("Quantity cannot be negative.");
        if(product.getMinimumStock()<0) throw new ValidationException("Minimum stock cannot be negative.");
    }
}
