package com.playlist.converter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic integration test for the application
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "youtube.api.key=test-key",
    "spring.security.oauth2.client.registration.spotify.client-id=test-client-id",
    "spring.security.oauth2.client.registration.spotify.client-secret=test-client-secret"
})
class ApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
    }
}
