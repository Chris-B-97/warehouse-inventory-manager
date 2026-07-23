# Warehouse Inventory Manager

Warehouse Inventory Manager is a JavaFX desktop application for managing products, warehouse locations, stock movements and simple picking routes.

This project was created as a portfolio application to demonstrate Java, JavaFX, JDBC, MySQL, Maven, software architecture and business-process modelling skills.

It is not presented as a production-ready Warehouse Management System. The goal is to provide a stable, understandable and demonstrable application that can be explained during a junior Java developer interview.

---

## Features

### Dashboard

- Number of active products
- Total quantity currently in stock
- Number of active warehouse locations
- Number of low-stock products
- Latest stock movements
- Low-stock product overview

### Product management

- Display all products
- Create new products
- Edit existing products
- Deactivate products without deleting their movement history
- Search by product name, SKU or location
- Filter by category
- Filter low-stock products
- Display current quantity and assigned location

### Location management

- Display warehouse locations
- Create and edit locations
- Activate or deactivate locations
- Search by location code
- Display products assigned to a location
- Store maximum capacity information
- Store X and Y coordinates for route calculation

### Stock operations

- Stock entry
- Stock exit
- Stock adjustment
- Product transfer between locations
- Stock availability validation
- Location capacity validation
- Transactional updates with rollback on failure
- Automatic movement-history creation

### Movement history

- Product
- SKU
- Movement type
- Changed quantity
- Previous quantity
- New quantity
- Location
- Reason
- Reference
- Date and time
- Filters by product, movement type and date

### Low-stock detection

A product is considered low in stock when:

```text
quantity <= minimumStock
