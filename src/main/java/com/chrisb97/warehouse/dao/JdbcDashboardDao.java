package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.config.ConnectionFactory;
import com.chrisb97.warehouse.exception.DatabaseException;
import com.chrisb97.warehouse.model.DashboardStats;
import java.sql.*;

public class JdbcDashboardDao implements DashboardDao {
    private final ConnectionFactory connectionFactory;
    public JdbcDashboardDao(ConnectionFactory connectionFactory){this.connectionFactory=connectionFactory;}
    @Override public DashboardStats loadStats(){
        String sql="""
            SELECT (SELECT COUNT(*) FROM products WHERE active=TRUE) active_products,
                   (SELECT COALESCE(SUM(quantity),0) FROM products WHERE active=TRUE) total_quantity,
                   (SELECT COUNT(*) FROM locations WHERE active=TRUE) active_locations,
                   (SELECT COUNT(*) FROM products WHERE active=TRUE AND quantity<=minimum_stock) low_stock
            """;
        try(Connection c=connectionFactory.getConnection();PreparedStatement s=c.prepareStatement(sql);ResultSet rs=s.executeQuery()){rs.next();return new DashboardStats(rs.getLong(1),rs.getLong(2),rs.getLong(3),rs.getLong(4));}
        catch(SQLException e){throw new DatabaseException("Could not load dashboard statistics.",e);}
    }
}
