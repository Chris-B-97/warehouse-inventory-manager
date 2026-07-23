package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.config.ConnectionFactory;
import com.chrisb97.warehouse.exception.DatabaseException;
import com.chrisb97.warehouse.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductDao implements ProductDao {
    private static final String SELECT_COLUMNS = """
            SELECT p.id, p.sku, p.name, p.description, p.category, p.quantity, p.minimum_stock,
                   p.location_id, l.code AS location_code, p.created_at, p.updated_at, p.active
            FROM products p LEFT JOIN locations l ON l.id = p.location_id
            """;
    private final ConnectionFactory connectionFactory;

    public JdbcProductDao(ConnectionFactory connectionFactory) { this.connectionFactory = connectionFactory; }

    @Override
    public List<Product> findAll(String search, String category, boolean lowStockOnly) {
        StringBuilder sql = new StringBuilder(SELECT_COLUMNS).append(" WHERE 1=1 ");
        List<Object> parameters = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            sql.append(" AND (LOWER(p.name) LIKE ? OR LOWER(p.sku) LIKE ? OR LOWER(l.code) LIKE ?) ");
            String value = "%" + search.toLowerCase() + "%";
            parameters.add(value); parameters.add(value); parameters.add(value);
        }
        if (category != null && !category.isBlank() && !"All".equals(category)) {
            sql.append(" AND p.category = ? "); parameters.add(category);
        }
        if (lowStockOnly) sql.append(" AND p.quantity <= p.minimum_stock ");
        sql.append(" ORDER BY p.active DESC, p.name ");
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) statement.setObject(i + 1, parameters.get(i));
            try (ResultSet resultSet = statement.executeQuery()) { return mapList(resultSet); }
        } catch (SQLException exception) { throw new DatabaseException("Could not load products.", exception); }
    }

    @Override public List<Product> findAllActive() {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_COLUMNS + " WHERE p.active = TRUE ORDER BY p.name");
             ResultSet resultSet = statement.executeQuery()) { return mapList(resultSet); }
        catch (SQLException exception) { throw new DatabaseException("Could not load active products.", exception); }
    }

    @Override public Optional<Product> findById(long id) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_COLUMNS + " WHERE p.id = ?")) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty(); }
        } catch (SQLException exception) { throw new DatabaseException("Could not load product.", exception); }
    }

    @Override public Optional<Product> findByIdForUpdate(Connection connection, long id) {
        String sql = SELECT_COLUMNS + " WHERE p.id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) { return resultSet.next() ? Optional.of(map(resultSet)) : Optional.empty(); }
        } catch (SQLException exception) { throw new DatabaseException("Could not lock product.", exception); }
    }

    @Override public Product save(Product product) {
        String sql = "INSERT INTO products (sku,name,description,category,quantity,minimum_stock,location_id,active) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, product);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) { if (keys.next()) product.setId(keys.getLong(1)); }
            return product;
        } catch (SQLIntegrityConstraintViolationException exception) { throw new DatabaseException("SKU already exists.", exception); }
        catch (SQLException exception) { throw new DatabaseException("Could not create product.", exception); }
    }

    @Override public void update(Product product) {
        String sql = "UPDATE products SET sku=?,name=?,description=?,category=?,quantity=?,minimum_stock=?,location_id=?,active=? WHERE id=?";
        try (Connection connection = connectionFactory.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, product); statement.setLong(9, product.getId()); statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException exception) { throw new DatabaseException("SKU already exists.", exception); }
        catch (SQLException exception) { throw new DatabaseException("Could not update product.", exception); }
    }

    private void bind(PreparedStatement statement, Product product) throws SQLException {
        statement.setString(1, product.getSku()); statement.setString(2, product.getName());
        statement.setString(3, product.getDescription()); statement.setString(4, product.getCategory());
        statement.setInt(5, product.getQuantity()); statement.setInt(6, product.getMinimumStock());
        if (product.getLocationId() == null) statement.setNull(7, Types.BIGINT); else statement.setLong(7, product.getLocationId());
        statement.setBoolean(8, product.isActive());
    }

    @Override public void deactivate(long id) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE products SET active=FALSE WHERE id=?")) {
            statement.setLong(1, id); statement.executeUpdate();
        } catch (SQLException exception) { throw new DatabaseException("Could not deactivate product.", exception); }
    }

    @Override public void updateStockAndLocation(Connection connection, long productId, int quantity, Long locationId) {
        String sql = "UPDATE products SET quantity=?, location_id=? WHERE id=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            if (locationId == null) statement.setNull(2, Types.BIGINT); else statement.setLong(2, locationId);
            statement.setLong(3, productId); statement.executeUpdate();
        } catch (SQLException exception) { throw new DatabaseException("Could not update product stock.", exception); }
    }

    @Override public long countProductsAtLocation(long locationId) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM products WHERE location_id=? AND active=TRUE")) {
            statement.setLong(1, locationId);
            try (ResultSet rs = statement.executeQuery()) { rs.next(); return rs.getLong(1); }
        } catch (SQLException exception) { throw new DatabaseException("Could not count products at location.", exception); }
    }

    private List<Product> mapList(ResultSet rs) throws SQLException { List<Product> list=new ArrayList<>(); while(rs.next()) list.add(map(rs)); return list; }
    private Product map(ResultSet rs) throws SQLException {
        Long locationId = rs.getObject("location_id", Long.class);
        return new Product(rs.getLong("id"), rs.getString("sku"), rs.getString("name"), rs.getString("description"),
                rs.getString("category"), rs.getInt("quantity"), rs.getInt("minimum_stock"), locationId,
                rs.getString("location_code"), rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(), rs.getBoolean("active"));
    }
}
