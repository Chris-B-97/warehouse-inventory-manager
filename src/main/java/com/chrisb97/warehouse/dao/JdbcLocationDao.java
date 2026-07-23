package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.config.ConnectionFactory;
import com.chrisb97.warehouse.exception.DatabaseException;
import com.chrisb97.warehouse.model.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLocationDao implements LocationDao {
    private final ConnectionFactory connectionFactory;
    public JdbcLocationDao(ConnectionFactory connectionFactory) { this.connectionFactory = connectionFactory; }

    @Override public List<Location> findAll(String search) {
        String sql = "SELECT * FROM locations WHERE (?='' OR LOWER(code) LIKE ?) ORDER BY aisle,side,position";
        String value = search == null ? "" : search.trim().toLowerCase();
        try (Connection connection=connectionFactory.getConnection(); PreparedStatement statement=connection.prepareStatement(sql)) {
            statement.setString(1,value); statement.setString(2,"%"+value+"%");
            try(ResultSet rs=statement.executeQuery()){ return mapList(rs); }
        } catch(SQLException exception){ throw new DatabaseException("Could not load locations.",exception); }
    }
    @Override public List<Location> findAllActive() {
        try(Connection connection=connectionFactory.getConnection(); PreparedStatement statement=connection.prepareStatement("SELECT * FROM locations WHERE active=TRUE ORDER BY aisle,side,position"); ResultSet rs=statement.executeQuery()){ return mapList(rs); }
        catch(SQLException exception){ throw new DatabaseException("Could not load active locations.",exception); }
    }
    @Override public Optional<Location> findById(long id) { try(Connection connection=connectionFactory.getConnection()){ return findById(connection,id); } catch(SQLException e){ throw new DatabaseException("Could not close connection.",e); } }
    @Override public Optional<Location> findById(Connection connection,long id) {
        try(PreparedStatement statement=connection.prepareStatement("SELECT * FROM locations WHERE id=?")){ statement.setLong(1,id); try(ResultSet rs=statement.executeQuery()){ return rs.next()?Optional.of(map(rs)):Optional.empty(); } }
        catch(SQLException exception){ throw new DatabaseException("Could not load location.",exception); }
    }
    @Override public Location save(Location location) {
        String sql="INSERT INTO locations(code,aisle,side,position,x_coordinate,y_coordinate,max_capacity,active) VALUES(?,?,?,?,?,?,?,?)";
        try(Connection connection=connectionFactory.getConnection(); PreparedStatement statement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){ bind(statement,location); statement.executeUpdate(); try(ResultSet keys=statement.getGeneratedKeys()){ if(keys.next())location.setId(keys.getLong(1)); } return location; }
        catch(SQLIntegrityConstraintViolationException e){ throw new DatabaseException("Location code already exists.",e); }
        catch(SQLException e){ throw new DatabaseException("Could not create location.",e); }
    }
    @Override public void update(Location location) {
        String sql="UPDATE locations SET code=?,aisle=?,side=?,position=?,x_coordinate=?,y_coordinate=?,max_capacity=?,active=? WHERE id=?";
        try(Connection connection=connectionFactory.getConnection(); PreparedStatement statement=connection.prepareStatement(sql)){ bind(statement,location); statement.setLong(9,location.getId()); statement.executeUpdate(); }
        catch(SQLException e){ throw new DatabaseException("Could not update location.",e); }
    }
    private void bind(PreparedStatement s,Location l)throws SQLException{ s.setString(1,l.getCode());s.setString(2,l.getAisle());s.setInt(3,l.getSide());s.setInt(4,l.getPosition());s.setInt(5,l.getXCoordinate());s.setInt(6,l.getYCoordinate());s.setInt(7,l.getMaxCapacity());s.setBoolean(8,l.isActive()); }
    @Override public void setActive(long id,boolean active){ try(Connection c=connectionFactory.getConnection();PreparedStatement s=c.prepareStatement("UPDATE locations SET active=? WHERE id=?")){s.setBoolean(1,active);s.setLong(2,id);s.executeUpdate();}catch(SQLException e){throw new DatabaseException("Could not change location status.",e);} }
    @Override public int currentQuantity(Connection connection,long locationId,long excludedProductId){
        try(PreparedStatement s=connection.prepareStatement("SELECT COALESCE(SUM(quantity),0) FROM products WHERE location_id=? AND id<>? AND active=TRUE")){s.setLong(1,locationId);s.setLong(2,excludedProductId);try(ResultSet rs=s.executeQuery()){rs.next();return rs.getInt(1);}}
        catch(SQLException e){throw new DatabaseException("Could not calculate location capacity.",e);}
    }
    private List<Location> mapList(ResultSet rs)throws SQLException{List<Location> list=new ArrayList<>();while(rs.next())list.add(map(rs));return list;}
    private Location map(ResultSet rs)throws SQLException{return new Location(rs.getLong("id"),rs.getString("code"),rs.getString("aisle"),rs.getInt("side"),rs.getInt("position"),rs.getInt("x_coordinate"),rs.getInt("y_coordinate"),rs.getInt("max_capacity"),rs.getBoolean("active"));}
}
