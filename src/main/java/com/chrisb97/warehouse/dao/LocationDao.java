package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.model.Location;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface LocationDao {
    List<Location> findAll(String search);
    List<Location> findAllActive();
    Optional<Location> findById(long id);
    Optional<Location> findById(Connection connection, long id);
    Location save(Location location);
    void update(Location location);
    void setActive(long id, boolean active);
    int currentQuantity(Connection connection, long locationId, long excludedProductId);
}
