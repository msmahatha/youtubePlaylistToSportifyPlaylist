package com.playlist.converter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for WebClient beans used for external API calls
 */
@Configuration
public class WebClientConfig {
    
    @Value("${youtube.api.base-url}")
    private String youtubeApiBaseUrl;
    
    @Value("${spotify.api.base-url}")
    private String spotifyApiBaseUrl;
    
    /**
     * WebClient for YouTube Data API
     */
    @Bean("youtubeWebClient")
    public WebClient youtubeWebClient() {
        return WebClient.builder()
                .baseUrl(youtubeApiBaseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
                .build();
    }
    
    /**
     * WebClient for Spotify Web API
     */
    @Bean("spotifyWebClient")
    public WebClient spotifyWebClient() {
        return WebClient.builder()
                .baseUrl(spotifyApiBaseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .build();
    }
    
    /**
     * General purpose WebClient
     */
    @Bean("generalWebClient")
    public WebClient generalWebClient() {
        return WebClient.builder()
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)) // 5MB
                .build();
    }
}
