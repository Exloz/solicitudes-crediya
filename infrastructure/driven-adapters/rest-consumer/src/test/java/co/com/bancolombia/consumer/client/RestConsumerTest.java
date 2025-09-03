package co.com.bancolombia.consumer.client;


import co.com.bancolombia.consumer.service.JwtService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import java.io.IOException;


class RestConsumerTest {

    private static RestConsumer restConsumer;

    private static MockWebServer mockBackEnd;


    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        // For testing purposes, we'll create a simple JwtService mock
        var jwtService = new JwtService(null); // null publicKey for testing
        restConsumer = new RestConsumer(webClient, jwtService);
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Validate RestConsumer is properly initialized")
    void validateRestConsumerInitialization() {
        // Test that RestConsumer is created successfully with required dependencies
        assert restConsumer != null;
    }
}