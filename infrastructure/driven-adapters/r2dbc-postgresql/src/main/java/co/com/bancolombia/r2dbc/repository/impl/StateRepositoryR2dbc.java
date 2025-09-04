package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.r2dbc.entity.StateEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StateRepositoryR2dbc extends ReactiveCrudRepository<StateEntity, Long>, ReactiveQueryByExampleExecutor<StateEntity> {

    Mono<StateEntity> findByName(String name);
}