# Warehouse Inventory Manager

Warehouse Inventory Manager is a desktop portfolio application for managing products, storage locations, stock movements and simple warehouse picking routes.

> This project was created as a portfolio application to demonstrate Java, JavaFX, JDBC, MySQL, software architecture and business-process modelling skills.

It is not presented as a production WMS. The goal is to provide a stable, understandable first version that can be demonstrated and explained in a junior Java developer interview.

## Features

- Dashboard with active products, total units, active locations and low-stock count
- Product creation, editing, deactivation, search and filters
- Location creation, editing, activation and capacity information
- Transactional stock entry, stock exit, adjustment and transfer
- Complete stock movement history with product, type and date filters
- Low-stock detection with `quantity <= minimumStock`
- Product search by name, SKU or location code
- Picking list creation with stock validation
- Grouping of products stored at the same location
- Manhattan-distance route estimation using a nearest-neighbour heuristic
- JUnit 5 tests for validation, stock rules and route optimisation

## Technologies

- Java 21
- JavaFX 21 with FXML and centralised CSS
- MySQL 8+
- JDBC with `PreparedStatement`
- Maven
- JUnit 5

No Spring Boot, Hibernate, JPA, Lombok or web framework is used.

## Architecture

The project follows a lightweight MVC-inspired structure:

- **Model**: domain objects such as `Product`, `Location` and `StockMovement`
- **DAO**: SQL and JDBC mapping
- **Service**: validation, business rules, transactions and route optimisation
- **Controller**: JavaFX event handling and view coordination
- **FXML/CSS**: UI structure and visual design
- **Config**: database configuration and application service wiring

`AppContext` performs simple manual dependency wiring. It avoids a framework while keeping controllers independent from concrete JDBC construction.

## Project structure

```text
warehouse-inventory-manager/
├── database/
│   ├── schema.sql
│   └── demo-data.sql
├── screenshots/
├── src/main/java/com/chrisb97/warehouse/
│   ├── config/
│   ├── controller/
│   ├── dao/
│   ├── exception/
│   ├── model/
│   ├── service/
│   ├── util/
│   └── validation/
├── src/main/resources/
│   ├── css/
│   ├── fxml/
│   └── database.properties.example
├── src/test/java/com/chrisb97/warehouse/
├── pom.xml
└── README.md
```

## Data model

### Product
A product has a unique SKU, name, category, quantity, minimum stock level, active status and an optional location. A disabled product remains referenced by its historical movements.

### Location
A location has a unique code such as `A-1-02`, aisle A-E, side 1-2, position, coordinates, capacity and active status.

### StockMovement
A movement records type, changed quantity, previous quantity, new quantity, location, reason, optional reference and timestamp.

The first version stores each product in one location at a time. Multiple storage locations per product are intentionally left as a future improvement.

## Installation

### 1. Requirements

- JDK 21
- Maven 3.9+
- MySQL 8+
- IntelliJ IDEA, Eclipse or VS Code with Java support

### 2. Create the database

From a terminal:

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p warehouse_manager < database/demo-data.sql
```

Alternatively, run both scripts in MySQL Workbench in this order.

### 3. Configure the connection

Copy:

```text
src/main/resources/database.properties.example
```

to:

```text
src/main/resources/database.properties
```

Then enter local credentials:

```properties
db.url=jdbc:mysql://localhost:3306/warehouse_manager?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Europe/Berlin
db.user=warehouse_app
db.password=your_local_password
```

The real file is excluded by `.gitignore` and must never be committed.

A dedicated local user can be created with:

```sql
CREATE USER 'warehouse_app'@'localhost' IDENTIFIED BY 'change_this_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON warehouse_manager.* TO 'warehouse_app'@'localhost';
FLUSH PRIVILEGES;
```

### 4. Run the application

```bash
mvn clean javafx:run
```

### 5. Run tests

```bash
mvn clean test
```

## Opening in IntelliJ IDEA

1. Choose **Open** and select the project folder.
2. Allow IntelliJ to import the Maven project.
3. Select JDK 21 as the project SDK.
4. Create `database.properties` from the example file.
5. Run the Maven goal `javafx:run`, or run `Launcher.java` through Maven-aware configuration.

## Picking route optimisation

The entrance is fixed at `(0, 0)`. Each location has X/Y coordinates.

The service:

1. groups products at the same location;
2. starts at the entrance;
3. selects the nearest unvisited location using Manhattan distance;
4. repeats until every location is visited;
5. optionally adds the distance back to the entrance.

Manhattan distance:

```text
|x1 - x2| + |y1 - y2|
```

This is a simple, understandable nearest-neighbour heuristic. It does not guarantee the globally shortest route and is not an industrial route-planning algorithm.

## Transactions and data integrity

Stock entry, stock exit, adjustment and transfer execute as JDBC transactions:

1. lock the selected product row with `SELECT ... FOR UPDATE`;
2. validate stock and location capacity;
3. update quantity/location;
4. insert the movement record;
5. commit;
6. roll back everything if an error occurs.

All dynamic SQL values are passed through `PreparedStatement`.

## Tests

Automated tests cover:

- product validation;
- negative quantity rejection;
- low-stock boundary detection;
- stock-out above available quantity;
- Manhattan distance;
- nearest-neighbour ordering;
- grouping multiple products at one location.

Manual testing should cover the JavaFX forms, database connection, CRUD actions and complete transaction flows with MySQL.

## Screenshots

Planned screenshots:

- Dashboard: `screenshots/dashboard.png`
- Product management: `screenshots/products.png`
- Stock operation: `screenshots/stock-operation.png`
- Movement history: `screenshots/movement-history.png`
- Picking route: `screenshots/picking-route.png`

## Current limitations

- One storage location per product
- No authentication or role permissions
- No barcode hardware integration
- No order or supplier module
- Capacity is represented as total units, not volume or weight
- Route optimisation uses static two-dimensional coordinates
- No reporting/export module

## Future improvements

- Multiple storage locations per product
- Barcode scanning
- User authentication and permissions
- Order management
- Supplier management
- REST API
- Advanced route optimisation
- Docker configuration
- Reporting and export

## Suggested Git history

1. `Initial project structure`
2. `Add database schema and configuration`
3. `Implement product and location management`
4. `Implement transactional stock movement services`
5. `Add JavaFX user interface`
6. `Add picking route optimisation`
7. `Add tests and documentation`

## Interview topics to understand

- Why services own business rules while DAOs own SQL
- How JDBC transactions and rollback protect consistency
- Why products are deactivated instead of deleted
- How `SELECT ... FOR UPDATE` helps avoid concurrent stock changes
- Why prepared statements improve security
- How FXML separates layout from controller logic
- Why nearest neighbour is fast and understandable but not always optimal
- How the schema could evolve to support multiple locations per product

## Author

Chris B. — Junior Java Developer portfolio project
