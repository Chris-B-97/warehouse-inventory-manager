package com.chrisb97.warehouse.service;

import com.chrisb97.warehouse.dao.ProductDao;
import com.chrisb97.warehouse.model.Product;
import com.chrisb97.warehouse.validation.ProductValidator;
import java.util.List;

public class ProductService {
    private final ProductDao dao;
    public ProductService(ProductDao dao){this.dao=dao;}
    public List<Product> search(String search,String category,boolean lowStockOnly){return dao.findAll(search,category,lowStockOnly);}
    public List<Product> activeProducts(){return dao.findAllActive();}
    public Product save(Product product){ProductValidator.validate(product);product.setSku(product.getSku().trim().toUpperCase());product.setName(product.getName().trim());return product.getId()==null?dao.save(product):updateAndReturn(product);}
    private Product updateAndReturn(Product product){dao.update(product);return product;}
    public void deactivate(long id){dao.deactivate(id);}
}
