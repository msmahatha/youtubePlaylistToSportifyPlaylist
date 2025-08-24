package com.playlist.converter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for playlist conversion
 */
public class ConversionRequest {
    
    @NotBlank(message = "YouTube playlist URL is required")
    @Pattern(regexp = "^https://www\\.youtube\\.com/playlist\\?list=.*", 
             message = "Invalid YouTube playlist URL format")
    private String youtubePlaylistUrl;
    
    @NotBlank(message = "Spotify user ID is required")
    private String spotifyUserId;
    
    private String playlistName; // Optional: custom name for the new Spotify playlist
    private String playlistDescription; // Optional: description for the new Spotify playlist
    private Boolean isPublic; // Optional: whether the playlist should be public (default: false)
    
    public ConversionRequest() {}
    
    public ConversionRequest(String youtubePlaylistUrl, String spotifyUserId, String playlistName, String playlistDescription, Boolean isPublic) {
        this.youtubePlaylistUrl = youtubePlaylistUrl;
        this.spotifyUserId = spotifyUserId;
        this.playlistName = playlistName;
        this.playlistDescription = playlistDescription;
        this.isPublic = isPublic;
    }
    
    /**
     * Extracts YouTube playlist ID from the URL
     */
    public String extractPlaylistId() {
        if (youtubePlaylistUrl == null) {
            return null;
        }
        
        String[] parts = youtubePlaylistUrl.split("list=");
        if (parts.length > 1) {
            String playlistId = parts[1];
            // Remove any additional parameters
            int ampersandIndex = playlistId.indexOf('&');
            if (ampersandIndex != -1) {
                playlistId = playlistId.substring(0, ampersandIndex);
            }
            return playlistId;
        }
        return null;
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

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistDescription() {
        return playlistDescription;
    }

    public void setPlaylistDescription(String playlistDescription) {
        this.playlistDescription = playlistDescription;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
