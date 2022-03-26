public class InventoryService {

    int numberOfSeats;

    public InventoryService(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public boolean decrementCapacity(int numberOfSeatsRequested) {
        if (numberOfSeatsRequested > this.numberOfSeats) {
            return false;
        }
        this.numberOfSeats -= numberOfSeatsRequested;
        System.out.println("Remaining number of seats: "+this.numberOfSeats);
        return true;
    }
}
