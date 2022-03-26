public class InventoryService {

    int seatsLeft;

    public InventoryService(int numberOfSeats) {
        this.seatsLeft = numberOfSeats;
    }

    public boolean onTicketBooked(int numberOfSeatsRequested) {
        if (numberOfSeatsRequested > this.seatsLeft) {
            return false;
        }
        this.seatsLeft -= numberOfSeatsRequested;
        System.out.println("Remaining number of seats: "+this.seatsLeft);
        return true;
    }

    public Object getSeatsLeft() {
        return this.seatsLeft;
    }
}
