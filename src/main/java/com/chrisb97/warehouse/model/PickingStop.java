package com.chrisb97.warehouse.model;

import java.util.List;

public record PickingStop(Location location, List<PickingItem> items, int pickingOrder) { }
