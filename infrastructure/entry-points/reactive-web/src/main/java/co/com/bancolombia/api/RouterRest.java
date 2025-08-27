package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.LoanApplicationRequest;
import co.com.bancolombia.api.dto.LoanApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation( path = "/api/v1/solicitud",
                    produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "registerLoanApplication",
                    operation = @Operation( operationId = "createLoanApplication",
                            summary = "Register loan application",
                            description = "Registers a new loan application in the system",
                            requestBody = @RequestBody(
                                description = "Loan application data to register",
                                required = true,
                                content = @Content(schema = @Schema(implementation = LoanApplicationRequest.class))
                            ),
                            responses = {
                                @ApiResponse(responseCode = "201", description = "Loan application created successfully",
                                    content = @Content(schema = @Schema(implementation = LoanApplicationResponse.class))),
                                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                                @ApiResponse(responseCode = "404", description = "Loan type not found"),
                                @ApiResponse(responseCode = "422", description = "Loan amount outside allowed limits"),
                                @ApiResponse(responseCode = "500", description = "Internal server error")
                            })
            )})
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitud"), handler::registerLoanApplication);
    }
}
