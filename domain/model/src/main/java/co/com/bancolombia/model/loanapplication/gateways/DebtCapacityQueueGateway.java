package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.user.UserInfo;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

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
     * Creates a debt capacity request message
     * @param application The loan application
     * @param userInfo The user's information
     * @param loanType The loan type details
     * @param totalMonthlyDebt The total monthly debt from approved loans
     * @return JSON string representation of the message
     */
    String createDebtCapacityMessage(LoanApplication application, UserInfo userInfo, LoanType loanType, BigDecimal totalMonthlyDebt);
}