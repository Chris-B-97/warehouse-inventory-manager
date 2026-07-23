package com.chrisb97.warehouse.dao;

import com.chrisb97.warehouse.model.DashboardStats;

public interface DashboardDao {
    DashboardStats loadStats();
}
