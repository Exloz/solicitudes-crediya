package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplicationReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface LoanApplicationUseCasePort {

    Mono<LoanApplication> registerLoanApplication(LoanApplication loanApplication, String jwtToken);

    Flux<LoanApplicationReview> getLoanApplications(String jwtToken, int page, int size, List<Integer> typeList);

    Mono<LoanApplication> updateLoanApplicationStatus(UUID id, Long statusId, String jwtToken);
}
