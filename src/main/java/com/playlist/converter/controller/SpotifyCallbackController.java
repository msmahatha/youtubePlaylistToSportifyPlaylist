package com.playlist.converter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

/**
 * Controller to handle Spotify OAuth2 callback
 */
@Controller
public class SpotifyCallbackController {
    
    private static final Logger log = LoggerFactory.getLogger(SpotifyCallbackController.class);
    
    @GetMapping("/callback")
    public String handleSpotifyCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            HttpSession session,
            Authentication authentication) {
        
        log.info("OAuth2 callback received - code: {}, error: {}, authenticated: {}", 
                 code != null ? "present" : "null", error, 
                 authentication != null && authentication.isAuthenticated());
        
        if (error != null) {
            log.warn("OAuth2 callback error: {}", error);
            return "redirect:/?error=" + error;
        }
        
        // Check if Spring Security has already authenticated the user
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("User successfully authenticated via Spring Security OAuth2: {}", 
                     authentication.getName());
            session.setAttribute("spotify_authenticated", true);
            return "redirect:/?authenticated=true";
        }
        
        // Fallback: handle manual OAuth flow
        if (code != null) {
            session.setAttribute("spotify_auth_code", code);
            session.setAttribute("spotify_state", state);
            session.setAttribute("spotify_callback_success", true);
            log.info("Stored authorization code in session");
        }
        
        // Redirect to main page with authentication success
        return "redirect:/?authenticated=true";
    }
}
