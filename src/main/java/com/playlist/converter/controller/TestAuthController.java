package com.playlist.converter.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Test controller to simulate OAuth2 authentication for development
 */
@RestController
@RequestMapping("/test-auth")
public class TestAuthController {
    
    /**
     * Simulate successful OAuth2 login for testing
     */
    @GetMapping("/login")
    public Map<String, Object> testLogin(HttpSession session) {
        // Simulate successful authentication
        session.setAttribute("oauth2_authenticated", true);
        session.setAttribute("oauth2_user", "test-user-123");
        
        return Map.of(
            "message", "Test authentication successful",
            "userId", "test-user-123",
            "authenticated", true,
            "redirectUrl", "/"
        );
    }
    
    /**
     * Clear test authentication
     */
    @GetMapping("/logout")
    public Map<String, Object> testLogout(HttpSession session) {
        session.removeAttribute("oauth2_authenticated");
        session.removeAttribute("oauth2_user");
        
        return Map.of(
            "message", "Test logout successful",
            "authenticated", false
        );
    }
}
