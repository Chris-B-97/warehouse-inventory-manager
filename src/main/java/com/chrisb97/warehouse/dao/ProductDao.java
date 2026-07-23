package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.model.Product;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    List<Product> findAll(String search, String category, boolean lowStockOnly);
    List<Product> findAllActive();
    Optional<Product> findById(long id);
    Optional<Product> findByIdForUpdate(Connection connection, long id);
    Product save(Product product);
    void update(Product product);
    void deactivate(long id);
    void updateStockAndLocation(Connection connection, long productId, int quantity, Long locationId);
    long countProductsAtLocation(long locationId);
}
