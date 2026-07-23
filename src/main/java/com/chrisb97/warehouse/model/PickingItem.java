package com.chrisb97.warehouse.model;

public record PickingItem(Product product, int requestedQuantity) { }
