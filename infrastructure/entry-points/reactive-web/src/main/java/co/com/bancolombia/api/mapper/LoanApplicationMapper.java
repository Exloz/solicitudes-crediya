package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    @Mapping(target = "id", ignore = true)
    LoanApplication toModel(LoanApplicationRequest loanApplicationRequest);
    LoanApplicationResponse toResponse(LoanApplication loanApplication);
}
