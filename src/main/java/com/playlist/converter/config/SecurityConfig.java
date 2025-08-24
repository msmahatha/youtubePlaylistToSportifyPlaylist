package com.playlist.converter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Security configuration for OAuth2 and CORS
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    
    public SecurityConfig(OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/actuator/**",
                    "/h2-console/**",
                    "/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/callback",
                    "/success.html",
                    "/error",
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/test-auth/**"
                ).permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/user").permitAll() // Allow checking auth status
                .requestMatchers("/convertPlaylist").authenticated()
                .requestMatchers("/status/**").permitAll() // Allow checking status without auth
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureUrl("/?error=authentication_failed")
            )
            .oauth2Client(oauth2Client -> {
                // OAuth2 client configuration
            })
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())); // For H2 console
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
