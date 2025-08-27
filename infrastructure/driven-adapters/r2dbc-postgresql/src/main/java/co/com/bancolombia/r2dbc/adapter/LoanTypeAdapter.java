package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.loantype.LoanType;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import co.com.bancolombia.r2dbc.LoanTypeRepositoryR2dbc;
import co.com.bancolombia.r2dbc.entity.LoanTypeEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeAdapter extends ReactiveAdapterOperations<LoanType, LoanTypeEntity, Long, LoanTypeRepositoryR2dbc>
        implements LoanTypeRepository {

    public LoanTypeAdapter(LoanTypeRepositoryR2dbc repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
    }

    @Override
    public Mono<LoanType> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<LoanType> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity);
    }
}