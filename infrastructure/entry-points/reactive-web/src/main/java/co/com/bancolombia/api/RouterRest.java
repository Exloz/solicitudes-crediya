package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
import co.com.bancolombia.api.dto.LoanApplicationReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    private static final String LOAN_APPLICATION_PATH = "/api/v1/solicitud";
    private static final String CREATE_LOAN_APPLICATION_OPERATION_ID = "createLoanApplication";
    private static final String REGISTER_LOAN_APPLICATION_SUMMARY = "Register loan application";
    private static final String REGISTER_LOAN_APPLICATION_DESCRIPTION = "Registers a new loan application in the system";
    private static final String REQUEST_BODY_DESCRIPTION = "Loan application data to register";
    private static final String RESPONSE_CREATED_DESCRIPTION = "Loan application created successfully";
    private static final String RESPONSE_BAD_REQUEST_DESCRIPTION = "Invalid input data";
    private static final String RESPONSE_NOT_FOUND_DESCRIPTION = "Loan type not found";
    private static final String RESPONSE_UNPROCESSABLE_ENTITY_DESCRIPTION = "Loan amount outside allowed limits";
    private static final String RESPONSE_INTERNAL_SERVER_ERROR_DESCRIPTION = "Internal server error";
    private static final String RESPONSE_UNAUTHORIZED_DESCRIPTION = "Unauthorized - Invalid or missing JWT token";
    private static final String RESPONSE_FORBIDDEN_DESCRIPTION = "Forbidden - Insufficient privileges or user ID mismatch";

    // Review constants
    private static final String GET_LOAN_APPLICATIONS_OPERATION_ID = "getLoanApplicationsForReview";
    private static final String GET_LOAN_APPLICATIONS_SUMMARY = "Get loan applications for review";
    private static final String GET_LOAN_APPLICATIONS_DESCRIPTION = "Retrieves a paginated list of loan applications that require review by an advisor";
    private static final String PAGE_PARAM_DESCRIPTION = "Page number (0-based)";
    private static final String SIZE_PARAM_DESCRIPTION = "Number of items per page (max 100)";
    private static final String RESPONSE_OK_DESCRIPTION = "Loan applications retrieved successfully";

    @Bean
    @RouterOperations({
            @RouterOperation( path = LOAN_APPLICATION_PATH,
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "registerLoanApplication",
                    operation = @Operation( operationId = CREATE_LOAN_APPLICATION_OPERATION_ID,
                            summary = REGISTER_LOAN_APPLICATION_SUMMARY,
                            description = REGISTER_LOAN_APPLICATION_DESCRIPTION + ". Requires USER role and valid JWT token.",
                            parameters = {
                                @Parameter(name = "Authorization", description = "JWT token with Bearer prefix", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            },
                            requestBody = @RequestBody(
                                description = REQUEST_BODY_DESCRIPTION,
                                required = true,
                                content = @Content(schema = @Schema(implementation = LoanApplicationRequest.class))
                            ),
                            responses = {
                                @ApiResponse(responseCode = "201", description = RESPONSE_CREATED_DESCRIPTION,
                                    content = @Content(schema = @Schema(implementation = LoanApplicationResponse.class))),
                                @ApiResponse(responseCode = "400", description = RESPONSE_BAD_REQUEST_DESCRIPTION),
                                @ApiResponse(responseCode = "401", description = RESPONSE_UNAUTHORIZED_DESCRIPTION),
                                @ApiResponse(responseCode = "403", description = RESPONSE_FORBIDDEN_DESCRIPTION),
                                @ApiResponse(responseCode = "404", description = RESPONSE_NOT_FOUND_DESCRIPTION),
                                @ApiResponse(responseCode = "422", description = RESPONSE_UNPROCESSABLE_ENTITY_DESCRIPTION),
                                @ApiResponse(responseCode = "500", description = RESPONSE_INTERNAL_SERVER_ERROR_DESCRIPTION)
                            })
            ),
            @RouterOperation( path = LOAN_APPLICATION_PATH,
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "getLoanApplicationsForReview",
                    operation = @Operation( operationId = GET_LOAN_APPLICATIONS_OPERATION_ID,
                            summary = GET_LOAN_APPLICATIONS_SUMMARY,
                            description = GET_LOAN_APPLICATIONS_DESCRIPTION + ". Requires Advisor role and valid JWT token.",
                            parameters = {
                                @Parameter(name = "Authorization", description = "JWT token with Bearer prefix", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."),
                                @Parameter(name = "page", description = PAGE_PARAM_DESCRIPTION, required = false, example = "0"),
                                @Parameter(name = "size", description = SIZE_PARAM_DESCRIPTION, required = false, example = "10")
                            },
                            responses = {
                                @ApiResponse(responseCode = "200", description = RESPONSE_OK_DESCRIPTION,
                                    content = @Content(schema = @Schema(implementation = LoanApplicationReviewResponse.class))),
                                @ApiResponse(responseCode = "401", description = RESPONSE_UNAUTHORIZED_DESCRIPTION),
                                @ApiResponse(responseCode = "403", description = RESPONSE_FORBIDDEN_DESCRIPTION),
                                @ApiResponse(responseCode = "500", description = RESPONSE_INTERNAL_SERVER_ERROR_DESCRIPTION)
                            })
            )})
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(LOAN_APPLICATION_PATH), handler::registerLoanApplication)
                .andRoute(GET(LOAN_APPLICATION_PATH), handler::getLoanApplicationsForReview);
    }
}
