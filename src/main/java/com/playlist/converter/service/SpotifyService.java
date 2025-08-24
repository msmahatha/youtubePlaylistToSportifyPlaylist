package com.playlist.converter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.playlist.converter.model.Playlist;
import com.playlist.converter.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with Spotify Web API
 */
@Service
public class SpotifyService {
    
    private static final Logger log = LoggerFactory.getLogger(SpotifyService.class);
    
    private final WebClient webClient;
    
    public SpotifyService(@Qualifier("spotifyWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
    
    /**
     * Search for a track on Spotify
     */
    public Track searchTrack(String query, String accessToken) {
        try {
            log.debug("Searching Spotify for track: {}", query);
            
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("type", "track")
                            .queryParam("limit", 1)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response == null || !response.has("tracks") || 
                response.get("tracks").get("items").size() == 0) {
                log.debug("No Spotify track found for query: {}", query);
                return null;
            }
            
            JsonNode track = response.get("tracks").get("items").get(0);
            
            return Track.fromSpotify(
                    track.get("id").asText(),
                    track.get("name").asText(),
                    track.get("artists").get(0).get("name").asText(),
                    track.get("album").get("name").asText(),
                    track.get("duration_ms").asLong(),
                    track.get("external_urls").get("spotify").asText()
            );
            
        } catch (WebClientResponseException e) {
            log.error("Error searching Spotify track: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Create a new playlist on Spotify
     */
    public Playlist createPlaylist(String userId, String name, String description, boolean isPublic, String accessToken) {
        try {
            log.info("Creating Spotify playlist: {} for user: {}", name, userId);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("description", description);
            requestBody.put("public", isPublic);
            
            JsonNode response = webClient.post()
                    .uri("/users/{user_id}/playlists", userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response == null) {
                throw new RuntimeException("Failed to create Spotify playlist");
            }
            
            return Playlist.fromSpotify(
                    response.get("id").asText(),
                    response.get("name").asText(),
                    response.get("description").asText(),
                    response.get("owner").get("display_name").asText(),
                    response.get("owner").get("id").asText(),
                    0, // Initial track count is 0
                    response.get("external_urls").get("spotify").asText()
            );
            
        } catch (WebClientResponseException e) {
            log.error("Error creating Spotify playlist: {}", e.getMessage());
            throw new RuntimeException("Failed to create Spotify playlist: " + e.getMessage());
        }
    }
    
    /**
     * Add tracks to a Spotify playlist
     */
    public boolean addTracksToPlaylist(String playlistId, List<String> trackUris, String accessToken) {
        try {
            log.info("Adding {} tracks to Spotify playlist: {}", trackUris.size(), playlistId);
            
            // Spotify API allows max 100 tracks per request
            int batchSize = 100;
            for (int i = 0; i < trackUris.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, trackUris.size());
                List<String> batch = trackUris.subList(i, endIndex);
                
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("uris", batch);
                
                webClient.post()
                        .uri("/playlists/{playlist_id}/tracks", playlistId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .body(BodyInserters.fromValue(requestBody))
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();
                
                log.debug("Added batch of {} tracks to playlist", batch.size());
            }
            
            log.info("Successfully added all tracks to playlist: {}", playlistId);
            return true;
            
        } catch (WebClientResponseException e) {
            log.error("Error adding tracks to Spotify playlist: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get user profile information
     */
    public JsonNode getUserProfile(String accessToken) {
        try {
            return webClient.get()
                    .uri("/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error fetching Spotify user profile: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch Spotify user profile: " + e.getMessage());
        }
    }
    
    /**
     * Generate Spotify track URI from track ID
     */
    public String getTrackUri(String trackId) {
        return "spotify:track:" + trackId;
    }
    
    /**
     * Clean and prepare search query for better Spotify matching
     */
    public String prepareSearchQuery(String youtubeTitle) {
        if (youtubeTitle == null) return "";
        
        // Remove common YouTube decorations and clean up
        String cleaned = youtubeTitle
                .replaceAll("\\s*\\(Official\\s+Video\\)", "")
                .replaceAll("\\s*\\(Official\\s+Music\\s+Video\\)", "")
                .replaceAll("\\s*\\(Official\\s+Audio\\)", "")
                .replaceAll("\\s*\\[Official\\s+Video\\]", "")
                .replaceAll("\\s*\\[Official\\s+Music\\s+Video\\]", "")
                .replaceAll("\\s*\\[Official\\s+Audio\\]", "")
                .replaceAll("\\s*\\(HD\\)", "")
                .replaceAll("\\s*\\[HD\\]", "")
                .replaceAll("\\s*\\(4K\\)", "")
                .replaceAll("\\s*\\[4K\\]", "")
                .replaceAll("\\s*\\(Lyrics\\)", "")
                .replaceAll("\\s*\\[Lyrics\\]", "")
                .replaceAll("\\s*-\\s*Topic", "") // Remove YouTube auto-generated topic channels
                .trim();
        
        // Extract artist and song if format is "Artist - Song"
        if (cleaned.contains(" - ")) {
            String[] parts = cleaned.split(" - ", 2);
            if (parts.length == 2) {
                return parts[1].trim() + " " + parts[0].trim(); // "Song Artist" format works better for Spotify search
            }
        }
        
        return cleaned;
    }
}
