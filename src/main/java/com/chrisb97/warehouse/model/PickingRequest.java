package com.chrisb97.warehouse.model;

public record PickingRequest(Product product, int requestedQuantity) { }
