package com.chrisb97.warehouse.service;

import com.chrisb97.warehouse.config.ConnectionFactory;
import com.chrisb97.warehouse.dao.*;
import com.chrisb97.warehouse.exception.DatabaseException;
import com.chrisb97.warehouse.exception.ValidationException;
import com.chrisb97.warehouse.model.*;
import java.sql.Connection;
import java.sql.SQLException;

public class StockService {
    private final ConnectionFactory connectionFactory; private final ProductDao productDao; private final LocationDao locationDao; private final StockMovementDao movementDao;
    public StockService(ConnectionFactory cf,ProductDao pd,LocationDao ld,StockMovementDao md){connectionFactory=cf;productDao=pd;locationDao=ld;movementDao=md;}
    public void stockIn(long productId,int quantity,long locationId,String reason,String reference){execute(productId,quantity,locationId,MovementType.IN,reason,reference);}
    public void stockOut(long productId,int quantity,String reason,String reference){execute(productId,quantity,null,MovementType.OUT,reason,reference);}
    public void adjust(long productId,int newQuantity,String reason,String reference){
        if(newQuantity<0)throw new ValidationException("New quantity cannot be negative.");
        transactional(c->{Product p=locked(c,productId);int delta=Math.abs(newQuantity-p.getQuantity());validateCapacity(c,p,p.getLocationId(),newQuantity);productDao.updateStockAndLocation(c,p.getId(),newQuantity,p.getLocationId());insert(c,p,MovementType.ADJUSTMENT,delta,p.getQuantity(),newQuantity,p.getLocationId(),reason,reference);});
    }
    public void transfer(long productId,long targetLocationId,String reason,String reference){
        transactional(c->{Product p=locked(c,productId);if(p.getLocationId()==null)throw new ValidationException("Product has no source location.");if(p.getLocationId()==targetLocationId)throw new ValidationException("Source and target locations are identical.");validateCapacity(c,p,targetLocationId,p.getQuantity());productDao.updateStockAndLocation(c,p.getId(),p.getQuantity(),targetLocationId);insert(c,p,MovementType.TRANSFER,p.getQuantity(),p.getQuantity(),p.getQuantity(),targetLocationId,reason,reference);});
    }
    private void execute(long productId,int quantity,Long locationId,MovementType type,String reason,String reference){
        if(quantity<=0)throw new ValidationException("Quantity must be greater than zero.");if(reason==null||reason.isBlank())throw new ValidationException("Reason is required.");
        transactional(c->{Product p=locked(c,productId);int previous=p.getQuantity();int next;Long destination=p.getLocationId();if(type==MovementType.IN){destination=locationId;if(destination==null)throw new ValidationException("Location is required.");next=previous+quantity;validateCapacity(c,p,destination,next);}else{next=StockRules.stockOutResult(previous,quantity);}productDao.updateStockAndLocation(c,p.getId(),next,destination);insert(c,p,type,quantity,previous,next,destination,reason,reference);});
    }
    private Product locked(Connection c,long id){return productDao.findByIdForUpdate(c,id).orElseThrow(()->new ValidationException("Product not found."));}
    private void validateCapacity(Connection c,Product p,Long locationId,int resultingProductQuantity){if(locationId==null)return;Location l=locationDao.findById(c,locationId).orElseThrow(()->new ValidationException("Location not found."));if(!l.isActive())throw new ValidationException("Location is inactive.");int occupied=locationDao.currentQuantity(c,locationId,p.getId());if(occupied+resultingProductQuantity>l.getMaxCapacity())throw new ValidationException("Location capacity would be exceeded.");}
    private void insert(Connection c,Product p,MovementType type,int qty,int prev,int next,Long loc,String reason,String reference){StockMovement m=new StockMovement();m.setProductId(p.getId());m.setMovementType(type);m.setQuantity(qty);m.setPreviousQuantity(prev);m.setNewQuantity(next);m.setLocationId(loc);m.setReason(reason.trim());m.setReference(reference==null?null:reference.trim());movementDao.insert(c,m);}
    private void transactional(TransactionWork work){try(Connection c=connectionFactory.getConnection()){try{c.setAutoCommit(false);work.run(c);c.commit();}catch(Exception e){try{c.rollback();}catch(SQLException rollback){e.addSuppressed(rollback);}if(e instanceof RuntimeException runtime)throw runtime;throw new DatabaseException("Transactional stock operation failed.",e);}finally{try{c.setAutoCommit(true);}catch(SQLException ignored){}}}catch(SQLException e){throw new DatabaseException("Could not close transaction connection.",e);}}
    @FunctionalInterface private interface TransactionWork{void run(Connection connection)throws Exception;}
}
