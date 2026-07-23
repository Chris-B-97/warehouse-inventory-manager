package com.chrisb97.warehouse.service;
import com.chrisb97.warehouse.model.*;import org.junit.jupiter.api.Test;import java.util.*;import static org.junit.jupiter.api.Assertions.*;
class RouteOptimizationServiceTest{private final RouteOptimizationService service=new RouteOptimizationService();private Location loc(long id,String code,int x,int y){return new Location(id,code,"A",1,1,x,y,100,true);}private Product product(long id,String sku,long location){Product p=new Product();p.setId(id);p.setSku(sku);p.setName(sku);p.setLocationId(location);p.setQuantity(20);return p;}
@Test void calculatesManhattanDistance(){assertEquals(9,service.manhattanDistance(1,2,5,7));}
@Test void choosesNearestNeighbourAndReturns(){Location a=loc(1,"A",2,0),b=loc(2,"B",8,0),c=loc(3,"C",4,0);PickingRoute route=service.optimize(List.of(new PickingStop(b,List.of(),0),new PickingStop(a,List.of(),0),new PickingStop(c,List.of(),0)),true);assertEquals(List.of("A","C","B"),route.stops().stream().map(s->s.location().getCode()).toList());assertEquals(16,route.totalDistance());}
@Test void groupsProductsAtSameLocation(){Location a=loc(1,"A-1-01",2,2);Product p1=product(1,"P1",1),p2=product(2,"P2",1);List<PickingStop> stops=service.groupByLocation(List.of(new PickingRequest(p1,2),new PickingRequest(p2,3)),Map.of(1L,a));assertEquals(1,stops.size());assertEquals(2,stops.getFirst().items().size());}
}
