package com.playlist.converter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle OAuth2 callback
 */
@Controller
public class CallbackController {
    
    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);
    
    /**
     * Handle OAuth2 callback from Spotify
     */
    @GetMapping("/callback")
    public String handleCallback(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            log.info("OAuth2 callback successful for user: {}", authentication.getName());
            return "redirect:/?authenticated=true";
        } else {
            log.warn("OAuth2 callback failed - no authentication");
            return "redirect:/?error=authentication_failed";
        }
    }
}
