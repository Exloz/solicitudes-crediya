package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationUseCasePort {

    Mono<LoanApplication> registerLoanApplication(LoanApplication loanApplication, String jwtToken);

    Flux<LoanApplicationReviewDto> getLoanApplicationsForReview(String jwtToken, int page, int size);
}
