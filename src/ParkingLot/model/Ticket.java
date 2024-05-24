package ParkingLot.model;

import ParkingLot.ParkingSlot;


public class Ticket {
    String ticketNumber;
    long startTime;
    long endTime;
    Vehicle vehicle;
    ParkingSlot parkingSlot;

    public Ticket(){
    }


//    public static Ticket createTicket(Vehicle vehicle, ParkingSlot parkingSlot){
//        return Ticket.builder()
//                .parkingSlot(parkingSlot)
//                .startTime(System.currentTimeMillis())
//                .vehicle(vehicle)
//                .ticketNumber(vehicle.getVehicleNumber()+System.currentTimeMillis())
//                .build();
//    }

//    public Ticket createTicket(Vehicle vehicle, ParkingSlot parkingSlot){
//        Ticket ticket = new Ticket();
//        ticket.setParkingSlot(parkingSlot);
//        ticket.setStartTime(System.currentTimeMillis());
//        ticket.setVehicle(vehicle);
//        ticket.setTicketNumber(vehicle.getVehicleNumber()+System.currentTimeMillis());
//        return ticket;
//    }
    public String getTicketNumber(){
        return ticketNumber;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSlot getParkingSlot() {
        return parkingSlot;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setParkingSlot(ParkingSlot parkingSlot) {
        this.parkingSlot = parkingSlot;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
}
