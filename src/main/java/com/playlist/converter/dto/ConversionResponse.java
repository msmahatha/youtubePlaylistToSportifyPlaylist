package com.playlist.converter.dto;

import com.playlist.converter.model.ConversionResult;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for playlist conversion
 */
public class ConversionResponse {
    
    private String conversionId;
    private ConversionResult.ConversionStatus status;
    private String spotifyPlaylistId;
    private String spotifyPlaylistUrl;
    private String message;
    private Integer totalTracks;
    private Integer matchedTracks;
    private Integer skippedTracks;
    private List<String> matchedTrackNames;
    private List<String> skippedTrackNames;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    
    public ConversionResponse() {}
    
    public ConversionResponse(String conversionId, ConversionResult.ConversionStatus status, String spotifyPlaylistId, 
                            String spotifyPlaylistUrl, String message, Integer totalTracks, Integer matchedTracks, 
                            Integer skippedTracks, List<String> matchedTrackNames, List<String> skippedTrackNames, 
                            String errorMessage, LocalDateTime createdAt, LocalDateTime completedAt) {
        this.conversionId = conversionId;
        this.status = status;
        this.spotifyPlaylistId = spotifyPlaylistId;
        this.spotifyPlaylistUrl = spotifyPlaylistUrl;
        this.message = message;
        this.totalTracks = totalTracks;
        this.matchedTracks = matchedTracks;
        this.skippedTracks = skippedTracks;
        this.matchedTrackNames = matchedTrackNames;
        this.skippedTrackNames = skippedTrackNames;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }
    
    /**
     * Creates a ConversionResponse from ConversionResult entity
     */
    public static ConversionResponse fromEntity(ConversionResult result) {
        ConversionResponse response = new ConversionResponse();
        response.setConversionId(result.getConversionId());
        response.setStatus(result.getStatus());
        response.setSpotifyPlaylistId(result.getSpotifyPlaylistId());
        response.setSpotifyPlaylistUrl(result.getSpotifyPlaylistUrl());
        response.setTotalTracks(result.getTotalTracks());
        response.setMatchedTracks(result.getMatchedTracks());
        response.setSkippedTracks(result.getSkippedTracks());
        response.setMatchedTrackNames(result.getMatchedTrackNames());
        response.setSkippedTrackNames(result.getSkippedTrackNames());
        response.setErrorMessage(result.getErrorMessage());
        response.setCreatedAt(result.getCreatedAt());
        response.setCompletedAt(result.getCompletedAt());
        return response;
    }
    
    /**
     * Creates a response for successful conversion initiation
     */
    public static ConversionResponse initiated(String conversionId) {
        ConversionResponse response = new ConversionResponse();
        response.setConversionId(conversionId);
        response.setStatus(ConversionResult.ConversionStatus.PENDING);
        response.setMessage("Conversion initiated successfully. Use the conversionId to track progress.");
        return response;
    }
    
    /**
     * Creates a response with just a message
     */
    public static ConversionResponse withMessage(String message) {
        ConversionResponse response = new ConversionResponse();
        response.setMessage(message);
        return response;
    }
    
    /**
     * Creates an error response
     */
    public static ConversionResponse error(String errorMessage) {
        ConversionResponse response = new ConversionResponse();
        response.setStatus(ConversionResult.ConversionStatus.FAILED);
        response.setErrorMessage(errorMessage);
        return response;
    }

    // Getters and Setters
    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public ConversionResult.ConversionStatus getStatus() {
        return status;
    }

    public void setStatus(ConversionResult.ConversionStatus status) {
        this.status = status;
    }

    public String getSpotifyPlaylistId() {
        return spotifyPlaylistId;
    }

    public void setSpotifyPlaylistId(String spotifyPlaylistId) {
        this.spotifyPlaylistId = spotifyPlaylistId;
    }

    public String getSpotifyPlaylistUrl() {
        return spotifyPlaylistUrl;
    }

    public void setSpotifyPlaylistUrl(String spotifyPlaylistUrl) {
        this.spotifyPlaylistUrl = spotifyPlaylistUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(Integer totalTracks) {
        this.totalTracks = totalTracks;
    }

    public Integer getMatchedTracks() {
        return matchedTracks;
    }

    public void setMatchedTracks(Integer matchedTracks) {
        this.matchedTracks = matchedTracks;
    }

    public Integer getSkippedTracks() {
        return skippedTracks;
    }

    public void setSkippedTracks(Integer skippedTracks) {
        this.skippedTracks = skippedTracks;
    }

    public List<String> getMatchedTrackNames() {
        return matchedTrackNames;
    }

    public void setMatchedTrackNames(List<String> matchedTrackNames) {
        this.matchedTrackNames = matchedTrackNames;
    }

    public List<String> getSkippedTrackNames() {
        return skippedTrackNames;
    }

    public void setSkippedTrackNames(List<String> skippedTrackNames) {
        this.skippedTrackNames = skippedTrackNames;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
