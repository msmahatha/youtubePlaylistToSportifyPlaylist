package com.playlist.converter.controller;

import com.playlist.converter.dto.ConversionRequest;
import com.playlist.converter.dto.ConversionResponse;
import com.playlist.converter.model.ConversionResult;
import com.playlist.converter.service.ConversionService;
import com.playlist.converter.service.OAuth2TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for playlist conversion operations
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Playlist Converter", description = "YouTube to Spotify Playlist Conversion API")
public class PlaylistConverterController {
    
    private static final Logger log = LoggerFactory.getLogger(PlaylistConverterController.class);
    
    private final ConversionService conversionService;
    private final OAuth2TokenService oAuth2TokenService;
    
    public PlaylistConverterController(ConversionService conversionService, OAuth2TokenService oAuth2TokenService) {
        this.conversionService = conversionService;
        this.oAuth2TokenService = oAuth2TokenService;
    }
    
    /**
     * Convert a YouTube playlist to Spotify
     */
    @PostMapping("/convertPlaylist")
    @Operation(summary = "Convert YouTube playlist to Spotify", 
               description = "Converts a YouTube playlist to a new Spotify playlist")
    public ResponseEntity<ConversionResponse> convertPlaylist(
            @Valid @RequestBody ConversionRequest request,
            Authentication authentication) {
        
        try {
            log.info("Received conversion request for YouTube playlist: {}", request.getYoutubePlaylistUrl());
            
            // Validate authentication and token
            String currentUserId = oAuth2TokenService.getCurrentSpotifyUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ConversionResponse.withMessage("User not authenticated with Spotify"));
            }
            
            // Check if user has valid token
            if (!oAuth2TokenService.hasValidToken(request.getSpotifyUserId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ConversionResponse.withMessage("No valid Spotify access token found. Please login again."));
            }
            
            // Ensure the authenticated user matches the request
            if (!currentUserId.equals(request.getSpotifyUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ConversionResponse.withMessage("Cannot convert playlist for a different user"));
            }
            
            // Initiate conversion
            String conversionId = conversionService.initiateConversion(request);
            
            log.info("Conversion initiated with ID: {}", conversionId);
            
            return ResponseEntity.ok(ConversionResponse.initiated(conversionId));
            
        } catch (Exception e) {
            log.error("Error initiating conversion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ConversionResponse.error("Failed to initiate conversion: " + e.getMessage()));
        }
    }
    
    /**
     * Get the status of a conversion
     */
    @GetMapping("/status/{conversionId}")
    @Operation(summary = "Get conversion status", 
               description = "Retrieves the current status and progress of a playlist conversion")
    public ResponseEntity<ConversionResponse> getConversionStatus(
            @Parameter(description = "Conversion ID") @PathVariable String conversionId) {
        
        try {
            ConversionResult result = conversionService.getConversionStatus(conversionId);
            
            ConversionResponse response = ConversionResponse.fromEntity(result);
            
            // Add appropriate message based on status
            switch (result.getStatus()) {
                case PENDING:
                    response.setMessage("Conversion is pending and will start shortly");
                    break;
                case IN_PROGRESS:
                    response.setMessage("Conversion is currently in progress");
                    break;
                case COMPLETED:
                    response.setMessage("Conversion completed successfully");
                    break;
                case FAILED:
                    response.setMessage("Conversion failed: " + result.getErrorMessage());
                    break;
            }
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Error getting conversion status for ID {}: {}", conversionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ConversionResponse.withMessage("Conversion not found: " + conversionId));
        } catch (Exception e) {
            log.error("Unexpected error getting conversion status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ConversionResponse.error("Internal server error"));
        }
    }
    
    /**
     * Get all conversions for the current user
     */
    @GetMapping("/conversions")
    @Operation(summary = "Get user conversions", 
               description = "Retrieves all playlist conversions for the current user")
    public ResponseEntity<?> getUserConversions(Authentication authentication) {
        
        try {
            String currentUserId = oAuth2TokenService.getCurrentSpotifyUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated with Spotify");
            }
            
            List<ConversionResult> conversions = conversionService.getUserConversions(currentUserId);
            
            List<ConversionResponse> responses = conversions.stream()
                    .map(ConversionResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
            
        } catch (Exception e) {
            log.error("Error getting user conversions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }
    
    /**
     * Get current user information
     */
    @GetMapping("/user")
    @Operation(summary = "Get current user", 
               description = "Retrieves information about the currently authenticated user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        
        try {
            String userId = oAuth2TokenService.getCurrentSpotifyUserId();
            String displayName = oAuth2TokenService.getCurrentUserDisplayName();
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }
            
            return ResponseEntity.ok(java.util.Map.of(
                    "userId", userId,
                    "displayName", displayName != null ? displayName : userId,
                    "hasValidToken", oAuth2TokenService.hasValidToken(userId)
            ));
            
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }
}
