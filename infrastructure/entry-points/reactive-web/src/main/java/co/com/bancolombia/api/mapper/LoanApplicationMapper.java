package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
import co.com.bancolombia.api.dto.LoanApplicationReviewResponse;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.LoanApplicationReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LoanApplication toModel(LoanApplicationRequest loanApplicationRequest);

    @Mapping(target = "status", source = "statusId")
    LoanApplicationResponse toResponse(LoanApplication loanApplication);

    @Mapping(target = "loanType", source = "loanType.name")
    @Mapping(target = "interestRate", source = "loanType.interestRate")
    @Mapping(target = "applicationStatus", source = "state.name")
    @Mapping(target = "email", source = "userInfo.email")
    @Mapping(target = "fullName", expression = "java(review.getUserInfo().name() + \" \" + review.getUserInfo().lastName())")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "term", source = "term")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "baseSalary", source = "userInfo.baseSalary")
    @Mapping(target = "monthlyRequestAmount", source = "monthlyRequestAmount")
    LoanApplicationReviewResponse toReviewResponse(LoanApplicationReview review);
}
