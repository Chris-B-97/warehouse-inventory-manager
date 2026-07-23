package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.config.ConnectionFactory;
import com.chrisb97.warehouse.exception.DatabaseException;
import com.chrisb97.warehouse.model.MovementType;
import com.chrisb97.warehouse.model.StockMovement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcStockMovementDao implements StockMovementDao {
    private static final String SELECT="""
        SELECT m.id,m.product_id,p.sku product_sku,p.name product_name,m.movement_type,m.quantity,
               m.previous_quantity,m.new_quantity,m.location_id,l.code location_code,m.reason,m.reference,m.created_at
        FROM stock_movements m JOIN products p ON p.id=m.product_id LEFT JOIN locations l ON l.id=m.location_id
        """;
    private final ConnectionFactory connectionFactory;
    public JdbcStockMovementDao(ConnectionFactory connectionFactory){this.connectionFactory=connectionFactory;}
    @Override public void insert(Connection connection,StockMovement m){
        String sql="INSERT INTO stock_movements(product_id,movement_type,quantity,previous_quantity,new_quantity,location_id,reason,reference) VALUES(?,?,?,?,?,?,?,?)";
        try(PreparedStatement s=connection.prepareStatement(sql)){s.setLong(1,m.getProductId());s.setString(2,m.getMovementType().name());s.setInt(3,m.getQuantity());s.setInt(4,m.getPreviousQuantity());s.setInt(5,m.getNewQuantity());if(m.getLocationId()==null)s.setNull(6,Types.BIGINT);else s.setLong(6,m.getLocationId());s.setString(7,m.getReason());s.setString(8,m.getReference());s.executeUpdate();}
        catch(SQLException e){throw new DatabaseException("Could not record stock movement.",e);}
    }
    @Override public List<StockMovement> findRecent(int limit){
        try(Connection c=connectionFactory.getConnection();PreparedStatement s=c.prepareStatement(SELECT+" ORDER BY m.created_at DESC LIMIT ?")){s.setInt(1,limit);try(ResultSet rs=s.executeQuery()){return mapList(rs);}}
        catch(SQLException e){throw new DatabaseException("Could not load movements.",e);}
    }
    @Override public List<StockMovement> search(Long productId,MovementType type,LocalDate from,LocalDate to){
        StringBuilder sql=new StringBuilder(SELECT).append(" WHERE 1=1 ");List<Object> params=new ArrayList<>();
        if(productId!=null){sql.append("AND m.product_id=? ");params.add(productId);}if(type!=null){sql.append("AND m.movement_type=? ");params.add(type.name());}
        if(from!=null){sql.append("AND m.created_at>=? ");params.add(Timestamp.valueOf(from.atStartOfDay()));}if(to!=null){sql.append("AND m.created_at<? ");params.add(Timestamp.valueOf(to.plusDays(1).atStartOfDay()));}
        sql.append("ORDER BY m.created_at DESC");
        try(Connection c=connectionFactory.getConnection();PreparedStatement s=c.prepareStatement(sql.toString())){for(int i=0;i<params.size();i++)s.setObject(i+1,params.get(i));try(ResultSet rs=s.executeQuery()){return mapList(rs);}}
        catch(SQLException e){throw new DatabaseException("Could not filter movements.",e);}
    }
    private List<StockMovement> mapList(ResultSet rs)throws SQLException{List<StockMovement> list=new ArrayList<>();while(rs.next()){StockMovement m=new StockMovement();m.setId(rs.getLong("id"));m.setProductId(rs.getLong("product_id"));m.setProductSku(rs.getString("product_sku"));m.setProductName(rs.getString("product_name"));m.setMovementType(MovementType.valueOf(rs.getString("movement_type")));m.setQuantity(rs.getInt("quantity"));m.setPreviousQuantity(rs.getInt("previous_quantity"));m.setNewQuantity(rs.getInt("new_quantity"));m.setLocationId(rs.getObject("location_id",Long.class));m.setLocationCode(rs.getString("location_code"));m.setReason(rs.getString("reason"));m.setReference(rs.getString("reference"));m.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());list.add(m);}return list;}
}
