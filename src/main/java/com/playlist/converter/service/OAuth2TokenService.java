package com.playlist.converter.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing OAuth2 tokens for Spotify integration
 */
@Service
public class OAuth2TokenService {
    
    private static final Logger log = LoggerFactory.getLogger(OAuth2TokenService.class);
    
    private final OAuth2AuthorizedClientService authorizedClientService;
    
    // In-memory token storage (for demo purposes - use Redis/Database for production)
    private final Map<String, String> tokenStorage = new ConcurrentHashMap<>();
    
    public OAuth2TokenService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }
    
    /**
     * Store access token for a user
     */
    public void storeAccessToken(String userId, String accessToken) {
        log.info("Storing access token for user: {}", userId);
        tokenStorage.put(userId, accessToken);
    }
    
    /**
     * Get access token for a user
     */
    public String getAccessToken(String userId) {
        // First try to get from storage
        String token = tokenStorage.get(userId);
        if (token != null) {
            log.debug("Retrieved access token from storage for user: {}", userId);
            return token;
        }
        
        // Try to get from current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );
            
            if (client != null && client.getAccessToken() != null) {
                OAuth2AccessToken accessToken = client.getAccessToken();
                
                // Check if token is still valid
                Instant expiresAt = accessToken.getExpiresAt();
                if (expiresAt == null || expiresAt.isAfter(Instant.now())) {
                    String tokenValue = accessToken.getTokenValue();
                    // Store for future use
                    storeAccessToken(userId, tokenValue);
                    log.debug("Retrieved and stored access token from OAuth2 client for user: {}", userId);
                    return tokenValue;
                }
            }
        }
        
        log.warn("No valid access token found for user: {}", userId);
        return null;
    }
    
    /**
     * Remove access token for a user
     */
    public void removeAccessToken(String userId) {
        log.info("Removing access token for user: {}", userId);
        tokenStorage.remove(userId);
    }
    
    /**
     * Check if user has a valid access token
     */
    public boolean hasValidToken(String userId) {
        String token = getAccessToken(userId);
        return token != null && !token.isEmpty();
    }
    
    /**
     * Get current authenticated user's Spotify user ID
     */
    public String getCurrentSpotifyUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            // Try to get user ID from OAuth2 attributes
            Object userId = oauthToken.getPrincipal().getAttributes().get("id");
            if (userId != null) {
                return userId.toString();
            }
            
            // Fallback to principal name
            return oauthToken.getName();
        }
        
        return null;
    }
    
    /**
     * Get current authenticated user's display name
     */
    public String getCurrentUserDisplayName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            Object displayName = oauthToken.getPrincipal().getAttributes().get("display_name");
            if (displayName != null) {
                return displayName.toString();
            }
            
            // Fallback to principal name
            return oauthToken.getName();
        }
        
        return null;
    }
}
