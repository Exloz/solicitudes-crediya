package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

public interface NotificationGateway {

    Mono<String> sendStatusChangeNotification(LoanApplication application, String clientEmail);
}