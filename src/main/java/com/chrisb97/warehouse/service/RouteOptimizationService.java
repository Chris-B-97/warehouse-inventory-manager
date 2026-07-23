package com.chrisb97.warehouse.service;

import com.chrisb97.warehouse.model.*;
import java.util.*;

public class RouteOptimizationService {
    public int manhattanDistance(int x1,int y1,int x2,int y2){return Math.abs(x1-x2)+Math.abs(y1-y2);}
    public PickingRoute optimize(List<PickingStop> unorderedStops,boolean returnToEntrance){
        List<PickingStop> remaining=new ArrayList<>(unorderedStops);List<PickingStop> ordered=new ArrayList<>();int x=0,y=0,total=0,order=1;
        while(!remaining.isEmpty()){int cx=x,cy=y;PickingStop next=remaining.stream().min(Comparator.<PickingStop>comparingInt(s->manhattanDistance(cx,cy,s.location().getXCoordinate(),s.location().getYCoordinate())).thenComparing(s->s.location().getCode())).orElseThrow();total+=manhattanDistance(x,y,next.location().getXCoordinate(),next.location().getYCoordinate());ordered.add(new PickingStop(next.location(),next.items(),order++));x=next.location().getXCoordinate();y=next.location().getYCoordinate();remaining.remove(next);}
        if(returnToEntrance)total+=manhattanDistance(x,y,0,0);return new PickingRoute(List.copyOf(ordered),total,returnToEntrance);
    }
    public List<PickingStop> groupByLocation(List<PickingRequest> requests,Map<Long,Location> locations){
        Map<Long,List<PickingItem>> grouped=new LinkedHashMap<>();for(PickingRequest r:requests){if(r.requestedQuantity()<=0)throw new IllegalArgumentException("Requested quantity must be positive.");Long id=r.product().getLocationId();if(id==null)throw new IllegalArgumentException("Product has no location: "+r.product().getSku());grouped.computeIfAbsent(id,k->new ArrayList<>()).add(new PickingItem(r.product(),r.requestedQuantity()));}
        List<PickingStop> stops=new ArrayList<>();grouped.forEach((id,items)->stops.add(new PickingStop(Objects.requireNonNull(locations.get(id),"Location missing for id "+id),List.copyOf(items),0)));return stops;
    }
}
