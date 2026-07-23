package com.chrisb97.warehouse.service;
import com.chrisb97.warehouse.dao.StockMovementDao;import com.chrisb97.warehouse.model.*;import java.time.LocalDate;import java.util.List;
public class MovementService{private final StockMovementDao dao;public MovementService(StockMovementDao dao){this.dao=dao;}public List<StockMovement> search(Long productId,MovementType type,LocalDate from,LocalDate to){return dao.search(productId,type,from,to);}}
