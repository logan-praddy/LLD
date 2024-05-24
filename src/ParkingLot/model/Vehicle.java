package ParkingLot.model;

public class Vehicle {
    String vehicleNumber;
    VehicleCategory vehicleCategory;

    public String getVehicleNumber(){
        return vehicleNumber;
    }

    public VehicleCategory getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public void setVehicleCategory(VehicleCategory vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }
}
