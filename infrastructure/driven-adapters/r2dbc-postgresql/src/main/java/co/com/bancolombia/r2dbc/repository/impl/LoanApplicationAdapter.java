package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
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

    @Override
    public Flux<LoanApplication> findByStatus(List<Integer> typeList, int limit, long offset) {
        return repository.findByStatus(typeList, limit, offset)
                .doOnNext(entity -> log.info("Mapped entity: {}", entity))
                .map(this::toEntity);
    }

    @Override
    public Mono<LoanApplication> updateStatus(UUID id, Long statusId) {
        return repository.updateStatus(id, statusId)
                .map(this::toEntity)
                .as(transactionalOperator::transactional)
                .doOnNext(updated -> log.info("Updated loan application {} to status {}", id, statusId));
    }

    @Override
    public Flux<LoanApplication> findByStatusAndClientId(Integer status, String clientId) {
        return repository.fingByStatusAndClientId( status, clientId)
                .map(this::toEntity);
    }
}