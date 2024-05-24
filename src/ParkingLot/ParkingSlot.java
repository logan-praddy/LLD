package ParkingLot;

import ParkingLot.model.ParkingSlotType;
import ParkingLot.model.Vehicle;

import java.lang.module.ModuleDescriptor;

public class ParkingSlot {
    String name;
//    @ModuleDescriptor.Builder.Default
    boolean isAvailable = true;
    Vehicle vehicle;
    ParkingSlotType parkingSlotType;

    public ParkingSlot(String name, ParkingSlotType parkingSlotType) {
        this.name = name;
        this.parkingSlotType = parkingSlotType;
    }

    protected void addVehicle(Vehicle vehicle){
        this.vehicle = vehicle;
        this.isAvailable=false;
    }

    protected void removeVehicle(Vehicle vehicle){
        this.vehicle=null;
        this.isAvailable=true;
    }

    public ParkingSlotType getParkingSlotType() {
        return parkingSlotType;
    }
}
