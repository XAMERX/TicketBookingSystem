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
}
