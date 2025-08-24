package com.playlist.converter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenAPIConfig {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("YouTube to Spotify Playlist Converter API")
                        .description("REST API service to convert YouTube playlists to Spotify playlists using OAuth2 authentication")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("GitHub Copilot")
                                .email("support@example.com")
                                .url("https://github.com/example/youtube-spotify-converter"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/api")
                                .description("Local development server"),
                        new Server()
                                .url("https://your-domain.com/api")
                                .description("Production server")
                ));
    }
}
