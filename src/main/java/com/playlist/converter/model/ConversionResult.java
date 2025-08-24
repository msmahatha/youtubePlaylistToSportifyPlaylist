package com.playlist.converter.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a playlist conversion job
 */
@Entity
@Table(name = "conversions")
public class ConversionResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String conversionId;
    
    @Column(nullable = false)
    private String youtubePlaylistUrl;
    
    @Column(nullable = false)
    private String spotifyUserId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversionStatus status;
    
    private String spotifyPlaylistId;
    private String spotifyPlaylistUrl;
    
    @Column(length = 1000)
    private String errorMessage;
    
    private Integer totalTracks;
    private Integer matchedTracks;
    private Integer skippedTracks;
    
    @ElementCollection
    @CollectionTable(name = "matched_tracks", joinColumns = @JoinColumn(name = "conversion_id"))
    private List<String> matchedTrackNames;
    
    @ElementCollection
    @CollectionTable(name = "skipped_tracks", joinColumns = @JoinColumn(name = "conversion_id"))
    private List<String> skippedTrackNames;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime completedAt;
    
    public ConversionResult() {}
    
    public ConversionResult(Long id, String conversionId, String youtubePlaylistUrl, String spotifyUserId, 
                           ConversionStatus status, String spotifyPlaylistId, String spotifyPlaylistUrl, 
                           String errorMessage, Integer totalTracks, Integer matchedTracks, Integer skippedTracks, 
                           List<String> matchedTrackNames, List<String> skippedTrackNames, 
                           LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.conversionId = conversionId;
        this.youtubePlaylistUrl = youtubePlaylistUrl;
        this.spotifyUserId = spotifyUserId;
        this.status = status;
        this.spotifyPlaylistId = spotifyPlaylistId;
        this.spotifyPlaylistUrl = spotifyPlaylistUrl;
        this.errorMessage = errorMessage;
        this.totalTracks = totalTracks;
        this.matchedTracks = matchedTracks;
        this.skippedTracks = skippedTracks;
        this.matchedTrackNames = matchedTrackNames;
        this.skippedTrackNames = skippedTrackNames;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ConversionStatus.PENDING;
        }
    }
    
    public enum ConversionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getYoutubePlaylistUrl() {
        return youtubePlaylistUrl;
    }

    public void setYoutubePlaylistUrl(String youtubePlaylistUrl) {
        this.youtubePlaylistUrl = youtubePlaylistUrl;
    }

    public String getSpotifyUserId() {
        return spotifyUserId;
    }

    public void setSpotifyUserId(String spotifyUserId) {
        this.spotifyUserId = spotifyUserId;
    }

    public ConversionStatus getStatus() {
        return status;
    }

    public void setStatus(ConversionStatus status) {
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
