package com.chrisb97.warehouse.model;

import java.util.List;

public record PickingRoute(List<PickingStop> stops, int totalDistance, boolean returnsToEntrance) { }
