package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface LoanApplicationRepositoryR2dbc extends ReactiveCrudRepository<LoanApplicationEntity, UUID>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    Flux<LoanApplicationEntity> findByClientId(String clientId);
}