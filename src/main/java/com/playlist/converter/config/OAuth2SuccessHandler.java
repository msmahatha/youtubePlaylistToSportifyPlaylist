package com.playlist.converter.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom OAuth2 authentication success handler
 */
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    
    private static final Logger log = LoggerFactory.getLogger(OAuth2SuccessHandler.class);
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        log.info("OAuth2 authentication success for user: {}", authentication.getName());
        
        if (authentication instanceof OAuth2AuthenticationToken token) {
            // Store authentication details in session
            request.getSession().setAttribute("oauth2_authenticated", true);
            request.getSession().setAttribute("oauth2_user", authentication.getName());
            request.getSession().setAttribute("oauth2_token", token);
            
            // Get user details from OAuth2 token
            var attributes = token.getPrincipal().getAttributes();
            String displayName = attributes.containsKey("display_name") ? 
                    attributes.get("display_name").toString() : authentication.getName();
            
            request.getSession().setAttribute("oauth2_display_name", displayName);
            
            log.info("Stored OAuth2 authentication in session for user: {} ({})", 
                     authentication.getName(), displayName);
        }
        
        // Redirect to the converter page with authentication success parameter
        response.sendRedirect("/?auth=success");
    }
}
