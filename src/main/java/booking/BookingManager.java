package booking;

public class BookingManager {

    private IPaymentGateway paymentGateway;
    private INotificationService notificationService;
    private IEventRepository eventRepository;

    public BookingManager(IPaymentGateway paymentGateway,
                          INotificationService notificationService,
                          IEventRepository eventRepository) {
        this.paymentGateway = paymentGateway;
        this.notificationService = notificationService;
        this.eventRepository = eventRepository;

    }

    public void book(String eventId, double amount) {
        if (eventId == null || eventId.isEmpty() || amount <= 0) {
            return;
        }
        if (eventRepository.isSoldOut(eventId)) {
            return;
        }

        String transactionId = paymentGateway.processPayment(amount);

        eventRepository.saveBooking(eventId);

        notificationService.sendConfirmation("Booking successful. Transaction ID: " + transactionId);
    }
}
