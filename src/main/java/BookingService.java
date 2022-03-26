public class BookingService {

    private final Orchestrator orchestrator;

    public BookingService(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void book(int numberOfSeats) {
        System.out.println("Booking requested: "+numberOfSeats);
        orchestrator.onTicketBooked(numberOfSeats);
    }

}
