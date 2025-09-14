package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

/**
 * Gateway for sending notification messages to queues
 */
public interface NotificationQueueGateway {

    /**
     * Sends a status change notification for a loan application
     * @param application The loan application
     * @param clientEmail The client's email address
     * @return Mono with the message ID
     */
    Mono<String> sendStatusNotification(LoanApplication application, String clientEmail);

    /**
     * Creates a notification message for the given loan application
     * @param application The loan application
     * @param clientEmail The client's email address
     * @return JSON string representation of the message
     */
    String createNotificationMessage(LoanApplication application, String clientEmail);
}