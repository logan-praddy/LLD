package ElevatorWithMultipleLIfts;

import java.util.ArrayList;
import java.util.List;

class Request {
    private final int sourceFloor;
    private final int destinationFloor;

    public Request(int sourceFloor, int destinationFloor) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }
}
enum Direction {
    UP, DOWN
}
class Elevator {
    private final int id;
    private final int capacity;
    private int currentFloor;
    private Direction currentDirection;
    private final List<Request> requests;

    public Elevator(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.currentFloor = 1;
        this.currentDirection = Direction.UP;
        this.requests = new ArrayList<>();
    }

    public synchronized void addRequest(Request request) {
        if (requests.size() < capacity) {
            requests.add(request);
            System.out.println("Elevator " + id + " added request: " + request);
            notifyAll();
        }
    }

    public synchronized Request getNextRequest() {
        while (requests.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return requests.remove(0);
    }

    public synchronized void processRequests() {
        while (true) {
            while (!requests.isEmpty()) {
                Request request = getNextRequest();
                processRequest(request);
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(Request request) {
        int startFloor = currentFloor;
        int endFloor = request.getDestinationFloor();

        if (startFloor < endFloor) {
            currentDirection = Direction.UP;
            for (int i = startFloor; i <= endFloor; i++) {
                currentFloor = i;
                System.out.println("Elevator " + id + " reached floor " + currentFloor);
                try {
                    Thread.sleep(1000); // Simulating elevator movement
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (startFloor > endFloor) {
            currentDirection = Direction.DOWN;
            for (int i = startFloor; i >= endFloor; i--) {
                currentFloor = i;
                System.out.println("Elevator " + id + " reached floor " + currentFloor);
                try {
                    Thread.sleep(1000); // Simulating elevator movement
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() {
        processRequests();
    }

    public int getCurrentFloor() {
        return currentFloor;
    }
}
class ElevatorController {
    private final List<Elevator> elevators;

    public ElevatorController(int numElevators, int capacity) {
        elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = new Elevator(i + 1, capacity);
            elevators.add(elevator);
            new Thread(elevator::run).start();
        }
    }

    public void requestElevator(int sourceFloor, int destinationFloor) {
        Elevator optimalElevator = findOptimalElevator(sourceFloor, destinationFloor);
        optimalElevator.addRequest(new Request(sourceFloor, destinationFloor));
    }

    private Elevator findOptimalElevator(int sourceFloor, int destinationFloor) {
        Elevator optimalElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int distance = Math.abs(sourceFloor - elevator.getCurrentFloor());
            if (distance < minDistance) {
                minDistance = distance;
                optimalElevator = elevator;
            }
        }

        return optimalElevator;
    }
}

public class ElevatorSystem {
    public static void main(String[] args) {
        ElevatorController controller = new ElevatorController(3, 5);

        controller.requestElevator(5, 10);
        controller.requestElevator(3, 7);
        controller.requestElevator(8, 2);
        controller.requestElevator(1, 9);
    }
}
