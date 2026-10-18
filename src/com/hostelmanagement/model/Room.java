package com.hostelmanagement.model;

public class Room {

    private final int roomId;
    private final String roomNumber;
    private final String blockWing;
    private final int capacity;
    private final int currentOccupancy;
    private final double monthlyFee;
    private final String roomStatus;

    public Room(
            int roomId,
            String roomNumber,
            String blockWing,
            int capacity,
            int currentOccupancy,
            double monthlyFee,
            String roomStatus
    ) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.blockWing = blockWing;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
        this.monthlyFee = monthlyFee;
        this.roomStatus = roomStatus;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getBlockWing() {
        return blockWing;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public int getAvailableBeds() {
        return Math.max(0, capacity - currentOccupancy);
    }

    @Override
    public String toString() {
        return roomNumber + " (" + blockWing + ", Avail: " + getAvailableBeds() + "/" + capacity + ")";
    }
}
