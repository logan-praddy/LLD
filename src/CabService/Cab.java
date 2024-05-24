package CabService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double distanceTo(Point other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}

class Passenger {
    private int id;
    private Point pickupPoint;
    private Point dropoffPoint;
    private double fare;

    Passenger(int id, Point pickupPoint, Point dropoffPoint){
        this.id = id;
        this.pickupPoint = pickupPoint;
        this.dropoffPoint = dropoffPoint;
        this.fare = 0.0;
    }

    public Point getPickupPoint() {
        return pickupPoint;
    }

    public Point getDropoffPoint() {
        return dropoffPoint;
    }

    public double getFare() {
        return fare;
    }
    public void setFare(double fare){
        this.fare = fare;
    }
}

class CabService {
    private double farePerKm;
    private double farePerMinute;
    private List<Passenger> passengers;
    private LocalDateTime startTime; // Added for time calculation

    public CabService(double farePerKm, double farePerMinute) {
        this.farePerKm = farePerKm;
        this.farePerMinute = farePerMinute;
        this.passengers = new ArrayList<>();
        this.startTime = LocalDateTime.now(); // Initialize start time
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

    public void calculateFare() {
        // Calculate total distance and time for the entire trip
        double totalDistance = 0.0;
        LocalDateTime endTime = LocalDateTime.now(); // Capture end time
        Duration totalTime = Duration.between(startTime, endTime);

        for (int i = 0; i < passengers.size() - 1; i++) {
            Passenger currentPassenger = passengers.get(i);
            Passenger nextPassenger = passengers.get(i + 1);
            totalDistance += currentPassenger.getPickupPoint().distanceTo(nextPassenger.getPickupPoint());
        }

        // Calculate fare for each passenger based on individual distance traveled
        for (Passenger passenger : passengers) {
            double distanceTraveled = passenger.getPickupPoint().distanceTo(passenger.getDropoffPoint());
            double individualTime = calculateIndividualTime(totalTime, distanceTraveled, totalDistance);
            passenger.setFare(distanceTraveled * farePerKm + individualTime * farePerMinute);
        }
    }

    // Calculate individual passenger's time based on overall time and distance ratios
    private double calculateIndividualTime(Duration totalTime, double passengerDistance, double totalDistance) {
        return (totalTime.toMinutes() * passengerDistance) / totalDistance;
    }
}


public class Cab {

    public static void main(String [] args) {
        Point pickup1 = new Point(10, 20);
        Point dropoff1 = new Point(15, 25);
        Passenger passenger1 = new Passenger(1, pickup1, dropoff1);

        Point pickup2 = new Point(20, 30);
        Point dropoff2 = new Point(25, 35);
        Passenger passenger2 = new Passenger(2, pickup2, dropoff2);

        CabService cabService = new CabService(10.0, 2.0);
        cabService.addPassenger(passenger1);
        cabService.addPassenger(passenger2);
        cabService.calculateFare();

        System.out.println("Passenger 1 fare: " + passenger1.getFare());
        System.out.println("Passenger 2 fare: " + passenger2.getFare());

    }

}
