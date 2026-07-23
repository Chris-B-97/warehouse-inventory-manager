package com.chrisb97.warehouse.service;

import com.chrisb97.warehouse.dao.LocationDao;
import com.chrisb97.warehouse.dao.ProductDao;
import com.chrisb97.warehouse.exception.ValidationException;
import com.chrisb97.warehouse.model.Location;
import java.util.List;

public class LocationService {
    private final LocationDao dao; private final ProductDao productDao;
    public LocationService(LocationDao dao,ProductDao productDao){this.dao=dao;this.productDao=productDao;}
    public List<Location> search(String search){return dao.findAll(search);}
    public List<Location> activeLocations(){return dao.findAllActive();}
    public Location save(Location l){validate(l);l.setCode(l.getCode().trim().toUpperCase());if(l.getId()==null)return dao.save(l);dao.update(l);return l;}
    public void setActive(long id,boolean active){if(!active&&productDao.countProductsAtLocation(id)>0)throw new ValidationException("Move or deactivate products before disabling this location.");dao.setActive(id,active);}
    private void validate(Location l){if(l.getCode()==null||l.getCode().isBlank())throw new ValidationException("Location code is required.");if(l.getAisle()==null||!l.getAisle().matches("[A-E]"))throw new ValidationException("Aisle must be A, B, C, D or E.");if(l.getSide()<1||l.getSide()>2)throw new ValidationException("Side must be 1 or 2.");if(l.getPosition()<1)throw new ValidationException("Position must be positive.");if(l.getMaxCapacity()<1)throw new ValidationException("Capacity must be positive.");}
}
