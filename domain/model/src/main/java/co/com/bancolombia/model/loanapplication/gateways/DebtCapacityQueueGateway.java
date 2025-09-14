package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.user.UserInfo;
import reactor.core.publisher.Mono;

/**
 * Gateway for sending debt capacity calculation requests to queues
 */
public interface DebtCapacityQueueGateway {

    /**
     * Sends a debt capacity request using JWT token to get user info
     * @param application The loan application
     * @param jwtToken JWT token for user authentication
     * @return Mono with the message ID
     */
    Mono<String> sendDebtCapacityRequest(LoanApplication application, String jwtToken);

    /**
     * Sends a debt capacity request with pre-fetched user and loan type info
     * @param application The loan application
     * @param userInfo The user's information
     * @param loanType The loan type details
     * @return Mono with the message ID
     */
    Mono<String> sendDebtCapacityRequest(LoanApplication application, UserInfo userInfo, LoanType loanType);

    /**
     * Creates a debt capacity request message
     * @param application The loan application
     * @param userInfo The user's information
     * @param loanType The loan type details
     * @return JSON string representation of the message
     */
    String createDebtCapacityMessage(LoanApplication application, UserInfo userInfo, LoanType loanType);
}