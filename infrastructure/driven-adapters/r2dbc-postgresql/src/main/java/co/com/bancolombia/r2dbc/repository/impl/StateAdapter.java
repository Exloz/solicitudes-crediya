package co.com.bancolombia.r2dbc.repository.impl;

import co.com.bancolombia.model.state.State;
import co.com.bancolombia.model.state.gateways.StateRepository;
import co.com.bancolombia.r2dbc.repository.impl.StateRepositoryR2dbc;
import co.com.bancolombia.r2dbc.entity.StateEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class StateAdapter extends ReactiveAdapterOperations<State, StateEntity, Long, StateRepositoryR2dbc>
        implements StateRepository {

    public StateAdapter(StateRepositoryR2dbc repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, State.class));
    }

    @Override
    public Mono<State> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Mono<State> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity);
    }
}