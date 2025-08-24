package com.playlist.converter.controller;

import com.playlist.converter.service.OAuth2TokenService;
import com.playlist.converter.service.SpotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for authentication-related operations
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and OAuth2 operations")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final OAuth2TokenService oAuth2TokenService;
    private final SpotifyService spotifyService;
    
    public AuthController(OAuth2TokenService oAuth2TokenService, SpotifyService spotifyService) {
        this.oAuth2TokenService = oAuth2TokenService;
        this.spotifyService = spotifyService;
    }
    
    /**
     * Handle successful OAuth2 login
     */
    @GetMapping("/success")
    @Operation(summary = "OAuth2 login success", 
               description = "Endpoint called after successful Spotify OAuth2 authentication")
    public ResponseEntity<?> loginSuccess(Authentication authentication) {
        
        try {
            if (!(authentication instanceof OAuth2AuthenticationToken)) {
                return ResponseEntity.badRequest().body("Invalid authentication type");
            }
            
            String userId = oAuth2TokenService.getCurrentSpotifyUserId();
            String accessToken = oAuth2TokenService.getAccessToken(userId);
            
            if (accessToken == null) {
                return ResponseEntity.badRequest().body("Failed to obtain access token");
            }
            
            // Store the token
            oAuth2TokenService.storeAccessToken(userId, accessToken);
            
            // Get user profile to verify token
            try {
                var userProfile = spotifyService.getUserProfile(accessToken);
                String displayName = userProfile.has("display_name") ? 
                        userProfile.get("display_name").asText() : userId;
                
                log.info("User {} authenticated successfully", userId);
                
                return ResponseEntity.ok(Map.of(
                        "message", "Authentication successful",
                        "userId", userId,
                        "displayName", displayName,
                        "redirectUrl", "/swagger-ui.html"
                ));
                
            } catch (Exception e) {
                log.error("Error fetching user profile: {}", e.getMessage());
                return ResponseEntity.ok(Map.of(
                        "message", "Authentication successful",
                        "userId", userId,
                        "redirectUrl", "/swagger-ui.html"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error in login success handler: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Authentication error");
        }
    }
    
    /**
     * Handle OAuth2 login failure
     */
    @GetMapping("/error")
    @Operation(summary = "OAuth2 login error", 
               description = "Endpoint called when Spotify OAuth2 authentication fails")
    public ResponseEntity<?> loginError() {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Authentication failed",
                "message", "Please try logging in again",
                "loginUrl", "/oauth2/authorization/spotify"
        ));
    }
    
    /**
     * Logout endpoint
     */
    @GetMapping("/logout")
    @Operation(summary = "Logout", 
               description = "Logs out the current user and removes stored tokens")
    public ResponseEntity<?> logout(Authentication authentication) {
        
        try {
            String userId = oAuth2TokenService.getCurrentSpotifyUserId();
            if (userId != null) {
                oAuth2TokenService.removeAccessToken(userId);
                log.info("User {} logged out successfully", userId);
            }
            
            return ResponseEntity.ok(Map.of(
                    "message", "Logged out successfully",
                    "loginUrl", "/oauth2/authorization/spotify"
            ));
            
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Logout error");
        }
    }
    
    /**
     * Get login URL for Spotify OAuth2
     */
    @GetMapping("/login-url")
    @Operation(summary = "Get login URL", 
               description = "Returns the URL to initiate Spotify OAuth2 authentication")
    public ResponseEntity<?> getLoginUrl() {
        return ResponseEntity.ok(Map.of(
                "loginUrl", "/oauth2/authorization/spotify",
                "secureUrl", "https://ac7a217f327e.ngrok-free.app/oauth2/authorization/spotify",
                "message", "Use secureUrl for Spotify OAuth2 authentication (HTTPS required)"
        ));
    }
    
    /**
     * Get current user info (check if authenticated)
     */
    @GetMapping("/user")
    @Operation(summary = "Get current user", 
               description = "Returns current authenticated user information")
    public ResponseEntity<?> getCurrentUser(Authentication authentication, HttpSession session) {
        
        log.info("Authentication check - Auth object: {}, isAuthenticated: {}", 
                 authentication != null ? authentication.getClass().getSimpleName() : "null",
                 authentication != null ? authentication.isAuthenticated() : false);
        
        log.info("Session attributes - oauth2_authenticated: {}, oauth2_user: {}", 
                 session.getAttribute("oauth2_authenticated"),
                 session.getAttribute("oauth2_user"));
        
        try {
            // Check Spring Security authentication first
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName();
                log.info("Authenticated via Spring Security: {}", userId);
                
                return ResponseEntity.ok(Map.of(
                        "authenticated", true,
                        "userId", userId,
                        "displayName", userId,
                        "message", "Authenticated via Spring Security"
                ));
            }
            
            // Check session-based authentication
            Boolean sessionAuth = (Boolean) session.getAttribute("oauth2_authenticated");
            String sessionUser = (String) session.getAttribute("oauth2_user");
            
            if (Boolean.TRUE.equals(sessionAuth) && sessionUser != null) {
                log.info("Authenticated via session: {}", sessionUser);
                
                return ResponseEntity.ok(Map.of(
                        "authenticated", true,
                        "userId", sessionUser,
                        "displayName", sessionUser,
                        "message", "Authenticated via session"
                ));
            }
            
            return ResponseEntity.status(401).body(Map.of(
                    "authenticated", false,
                    "message", "Not authenticated"
            ));
            
        } catch (Exception e) {
            log.error("Error in getCurrentUser: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "authenticated", false,
                    "message", "Server error"
            ));
        }
    }
}
