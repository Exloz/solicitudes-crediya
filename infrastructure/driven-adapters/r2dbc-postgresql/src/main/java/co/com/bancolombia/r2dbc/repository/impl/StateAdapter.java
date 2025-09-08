package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.state.gateways.StateRepository;
import co.com.bancolombia.r2dbc.repository.impl.StateRepositoryR2dbc;
import co.com.bancolombia.r2dbc.entity.StateEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class StateAdapter extends ReactiveAdapterOperations<State, StateEntity, Long, StateRepositoryR2dbc>
        implements StateRepository {

    public StateAdapter(StateRepositoryR2dbc repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, State.class));
    }

    @Override
    public Mono<State> findById(Long id) {
        return super.findById(id)
                .doOnNext(state -> log.info("Mapped state: {}", state))
                .doOnError( error -> log.error("Error finding state by ID: {}", error.getMessage(), error));
    }

    @Override
    public Mono<State> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity);
    }
}