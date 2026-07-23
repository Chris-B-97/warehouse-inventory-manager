package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.model.MovementType;
import com.chrisb97.warehouse.model.StockMovement;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface StockMovementDao {
    void insert(Connection connection, StockMovement movement);
    List<StockMovement> findRecent(int limit);
    List<StockMovement> search(Long productId, MovementType type, LocalDate from, LocalDate to);
}
