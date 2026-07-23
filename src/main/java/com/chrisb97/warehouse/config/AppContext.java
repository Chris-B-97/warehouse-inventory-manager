package com.chrisb97.warehouse.config;

import com.chrisb97.warehouse.dao.*;
import com.chrisb97.warehouse.service.*;

import java.sql.Connection;
import java.sql.SQLException;

public final class AppContext {
    private static final AppContext INSTANCE = new AppContext();

    private final ConnectionFactory connectionFactory;
    private final ProductDao productDao;
    private final LocationDao locationDao;
    private final StockMovementDao movementDao;
    private final DashboardDao dashboardDao;
    private final ProductService productService;
    private final LocationService locationService;
    private final StockService stockService;
    private final MovementService movementService;
    private final DashboardService dashboardService;
    private final RouteOptimizationService routeOptimizationService;
    private final PickingService pickingService;

    private AppContext() {
        connectionFactory = new ConnectionFactory(DatabaseConfig.load());
        productDao = new JdbcProductDao(connectionFactory);
        locationDao = new JdbcLocationDao(connectionFactory);
        movementDao = new JdbcStockMovementDao(connectionFactory);
        dashboardDao = new JdbcDashboardDao(connectionFactory);
        productService = new ProductService(productDao);
        locationService = new LocationService(locationDao, productDao);
        stockService = new StockService(connectionFactory, productDao, locationDao, movementDao);
        movementService = new MovementService(movementDao);
        dashboardService = new DashboardService(dashboardDao, movementDao, productDao);
        routeOptimizationService = new RouteOptimizationService();
        pickingService = new PickingService(productDao, routeOptimizationService);
    }

    public static AppContext getInstance() { return INSTANCE; }
    public ProductService products() { return productService; }
    public LocationService locations() { return locationService; }
    public StockService stock() { return stockService; }
    public MovementService movements() { return movementService; }
    public DashboardService dashboard() { return dashboardService; }
    public PickingService picking() { return pickingService; }

    public void verifyDatabaseConnection() {
        try (Connection ignored = connectionFactory.getConnection()) {
            // Opening the connection is sufficient for the startup check.
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to close the database test connection.", exception);
        }
    }
}
