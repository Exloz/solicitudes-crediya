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

    @Query("SELECT la.* FROM loan_application la " +
           "JOIN state s ON la.status = s.id " +
           "WHERE s.name = $1 " +
           "ORDER BY la.created_at DESC " +
           "LIMIT $2 OFFSET $3")
    Flux<LoanApplicationEntity> findByStatus(String statusName, int limit, long offset);
}