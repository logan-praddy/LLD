
import java.util.*;
import java.util.concurrent.*;

// Interface for the Third-Party Payment Gateway
interface ThirdPartyPaymentGateway {
    String sendCheck(Map<String, String> checkDetails, double amount);
    String sendWire(Map<String, String> wireDetails, double amount);
}

// Class representing a payment transaction
class PaymentTransaction {
    private String sellerId;
    private double amount;
    private String paymentMethod;
    private String transactionId;
    private Date timestamp;

    public PaymentTransaction(String sellerId, double amount, String paymentMethod, String transactionId) {
        this.sellerId = sellerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.timestamp = new Date();
    }

    // Getters and setters
}

// Payment service class
public class SellerPaymentService {
    private ThirdPartyPaymentGateway paymentGateway;
    private Map<String, List<PaymentTransaction>> paymentAuditLog;
    private ExecutorService executor;

    public SellerPaymentService(ThirdPartyPaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
        this.paymentAuditLog = new HashMap<>();
        this.executor = Executors.newFixedThreadPool(10); // Adjust thread pool size as needed
    }

    // Method to process payment to seller
    public void processPayment(String sellerId, double amount, String paymentMethod) {
        // Prepare payment details
        Map<String, String> paymentDetails = new HashMap<>();
        // Add seller-specific payment details if needed

        // Submit payment task to executor
        executor.submit(() -> {
            try {
                // Send payment via the appropriate method based on seller's preference
                String transactionId;
                if (paymentMethod.equals("check")) {
                    transactionId = paymentGateway.sendCheck(paymentDetails, amount);
                } else if (paymentMethod.equals("wire")) {
                    transactionId = paymentGateway.sendWire(paymentDetails, amount);
                } else {
                    throw new IllegalArgumentException("Invalid payment method");
                }

                // Log payment transaction
                PaymentTransaction transaction = new PaymentTransaction(sellerId, amount, paymentMethod, transactionId);
                logPaymentTransaction(sellerId, transaction);
            } catch (Exception e) {
                // Handle payment failure
                System.err.println("Payment failed: " + e.getMessage());
                // Log payment failure if needed
            }
        });
    }

    // Method to log payment transaction
    private void logPaymentTransaction(String sellerId, PaymentTransaction transaction) {
        synchronized (paymentAuditLog) {
            paymentAuditLog.putIfAbsent(sellerId, new ArrayList<>());
            paymentAuditLog.get(sellerId).add(transaction);
        }
    }

    // Method to retrieve payment status for a seller
    public List<PaymentTransaction> getPaymentStatus(String sellerId) {
        synchronized (paymentAuditLog) {
            return paymentAuditLog.getOrDefault(sellerId, Collections.emptyList());
        }
    }

    // Shutdown method to stop the executor
    public void shutdown() {
        executor.shutdown();
    }

    // Main method for testing
    public static void main(String[] args) {
        // Instantiate the ThirdPartyPaymentGateway implementation
        ThirdPartyPaymentGateway paymentGateway = new DummyPaymentGateway();

        // Create SellerPaymentService instance
        SellerPaymentService paymentService = new SellerPaymentService(paymentGateway);

        // Test payment processing
        paymentService.processPayment("seller1", 100.0, "check");
        paymentService.processPayment("seller2", 150.0, "wire");

        // Retrieve payment status
        List<PaymentTransaction> seller1Transactions = paymentService.getPaymentStatus("seller1");
        System.out.println("Payment status for seller1: " + seller1Transactions);

        // Shutdown the payment service
        paymentService.shutdown();
    }
}


//// Dummy implementation of the Third Party Payment Gateway
class DummyPaymentGateway implements ThirdPartyPaymentGateway {
    @Override
    public String sendCheck(Map<String, String> checkDetails, double amount) {
        // Dummy implementation for sending a check
        System.out.println("Sending check payment to seller: " + checkDetails + ", Amount: " + amount);
        return "TransactionID_Check123";
    }

    @Override
    public String sendWire(Map<String, String> wireDetails, double amount) {
        // Dummy implementation for sending a wire transfer
        System.out.println("Sending wire transfer payment to seller: " + wireDetails + ", Amount: " + amount);
        return "TransactionID_Wire456";
    }

}

//    /*We are a very large e-commerce company. Presently, we have 3 microservices providing the CRUD functions on sellers, products and orders
//
//        ———-SellerService————
//        sellerID (PK)
//        sellerName
//        paymentDetails[check/wire]
//
//        ————ProductService———- all products that seller
//        productID (PK)
//        sellerID
//        sellerPrice -sp
//        buyerPrice
//
//        ————OrderService———— customer purchases/booked products availabl in this service
//        orderID (PK)
//        buyerID
//        []productsIDs
//        orderTimestamp
//
//        Design a seller-side payment system. Buyer-side system already exists. We only need to design the system to pay out sellers on our platform.
//
//        10 orders - 2 sellers (5 orders each)
//
//        Info:
//        Payment gateway takes ~1 minute to process a payment
//        We are charged a fixed fee per transfer by the payment gateway
//
//        Requirements (in order of importance):
//        There should be an audit log of all payments made out to sellers
//        Sellers should be paid out, in their preferred form of payment, as soon as possible
//        Sellers will be calling up on the status of their payments!! Have a way to tell them the status of their payment, or steps required to fix any problems
//        Keep the payment gateway fees to a minimum
//        No payments should be dropped.
//        No duplicate payments should be made out.
//
//        The third party payment gateway’s interface looks like this,
//
//interface service ThirdPartyPaymentGateway {
//    function sendCheck(checkDetails, amount) returns transactionId or error;
//    function sendWire(wireDetails, amount) returns transactionId or error;
//}
//*/
//
//
//
