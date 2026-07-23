package com.chrisb97.warehouse.model;

import java.time.LocalDateTime;

public class Product {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private int quantity;
    private int minimumStock;
    private Long locationId;
    private String locationCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    public Product() { }

    public Product(Long id, String sku, String name, String description, String category, int quantity,
                   int minimumStock, Long locationId, String locationCode, LocalDateTime createdAt,
                   LocalDateTime updatedAt, boolean active) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
        this.locationId = locationId;
        this.locationCode = locationCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    public boolean isLowStock() { return quantity <= minimumStock; }
    public String getStockStatus() { return isLowStock() ? "LOW" : "OK"; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getMinimumStock() { return minimumStock; }
    public void setMinimumStock(int minimumStock) { this.minimumStock = minimumStock; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public String getLocationCode() { return locationCode == null ? "Unassigned" : locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    @Override public String toString() { return sku + " — " + name; }
}
