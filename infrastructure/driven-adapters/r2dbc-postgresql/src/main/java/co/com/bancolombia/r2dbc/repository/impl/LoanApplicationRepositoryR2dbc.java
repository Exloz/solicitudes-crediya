package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface LoanApplicationRepositoryR2dbc extends ReactiveCrudRepository<LoanApplicationEntity, UUID>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    Flux<LoanApplicationEntity> findByClientId(String clientId);

    @Query("SELECT la.* FROM loan_applications la " +
           "JOIN states s ON la.status = s.id " +
           "WHERE s.name IN (:statusNames) " +
           "ORDER BY la.created_at DESC " +
           "LIMIT :limit OFFSET :offset")
    Flux<LoanApplicationEntity> findByStatuses(List<String> statusNames, int limit, long offset);
}