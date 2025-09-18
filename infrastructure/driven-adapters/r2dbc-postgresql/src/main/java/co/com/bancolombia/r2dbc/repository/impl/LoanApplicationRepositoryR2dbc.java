package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface LoanApplicationRepositoryR2dbc extends ReactiveCrudRepository<LoanApplicationEntity, UUID>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    Flux<LoanApplicationEntity> findByClientId(String clientId);

    @Query("SELECT la.* FROM loan_application la " +
           "JOIN state s ON la.status = s.id " +
           "WHERE s.id IN (:idList) " +
           "ORDER BY la.created_at DESC " +
           "LIMIT $2 OFFSET $3")
    Flux<LoanApplicationEntity> findByStatus(List<Integer> idList, int limit, long offset);

    @Query("UPDATE loan_application SET status = $2, updated_at = CURRENT_TIMESTAMP WHERE id = $1 RETURNING *")
    Mono<LoanApplicationEntity> updateStatus(UUID id, Long statusId);

    Flux<LoanApplicationEntity> fingByStatusAndClientId(Integer status, String clientId);
}