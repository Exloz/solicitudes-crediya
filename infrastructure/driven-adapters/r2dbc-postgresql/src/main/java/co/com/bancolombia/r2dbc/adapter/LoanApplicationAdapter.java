package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.LoanApplicationRepositoryR2dbc;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class LoanApplicationAdapter extends ReactiveAdapterOperations<LoanApplication, LoanApplicationEntity, UUID, LoanApplicationRepositoryR2dbc>
        implements LoanApplicationRepository {

    private final TransactionalOperator transactionalOperator;

    public LoanApplicationAdapter(LoanApplicationRepositoryR2dbc repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        return super.save(loanApplication)
                .as(transactionalOperator::transactional);
    }


    @Override
    public Mono<LoanApplication> findById(UUID id) {
        return super.findById(id);
    }

    @Override
    public Flux<LoanApplication> findByClientId(String clientId) {
        return repository.findByClientId(clientId)
                .map(this::toEntity);
    }
}