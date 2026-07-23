package com.chrisb97.warehouse.model;

public class Location {
    private Long id;
    private String code;
    private String aisle;
    private int side;
    private int position;
    private int xCoordinate;
    private int yCoordinate;
    private int maxCapacity;
    private boolean active;

    public Location() { }

    public Location(Long id, String code, String aisle, int side, int position, int xCoordinate,
                    int yCoordinate, int maxCapacity, boolean active) {
        this.id = id;
        this.code = code;
        this.aisle = aisle;
        this.side = side;
        this.position = position;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.maxCapacity = maxCapacity;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getAisle() { return aisle; }
    public void setAisle(String aisle) { this.aisle = aisle; }
    public int getSide() { return side; }
    public void setSide(int side) { this.side = side; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public int getXCoordinate() { return xCoordinate; }
    public void setXCoordinate(int xCoordinate) { this.xCoordinate = xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }
    public void setYCoordinate(int yCoordinate) { this.yCoordinate = yCoordinate; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    @Override public String toString() { return code; }
}
