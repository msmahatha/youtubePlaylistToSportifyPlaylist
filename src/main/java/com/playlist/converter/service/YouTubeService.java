package com.playlist.converter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.playlist.converter.model.Playlist;
import com.playlist.converter.model.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for interacting with YouTube Data API v3
 */
@Service
public class YouTubeService {
    
    private static final Logger log = LoggerFactory.getLogger(YouTubeService.class);
    
    private final WebClient webClient;
    private final String apiKey;
    
    public YouTubeService(@Qualifier("youtubeWebClient") WebClient webClient,
                         @Value("${youtube.api.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }
    
    /**
     * Fetches playlist information from YouTube
     */
    public Playlist getPlaylistInfo(String playlistId) {
        try {
            log.info("Fetching YouTube playlist info for ID: {}", playlistId);
            
            JsonNode response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/playlists")
                            .queryParam("part", "snippet,contentDetails")
                            .queryParam("id", playlistId)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response == null || !response.has("items") || response.get("items").size() == 0) {
                throw new RuntimeException("Playlist not found or is private");
            }
            
            JsonNode playlistItem = response.get("items").get(0);
            JsonNode snippet = playlistItem.get("snippet");
            JsonNode contentDetails = playlistItem.get("contentDetails");
            
            return Playlist.fromYouTube(
                    playlistId,
                    snippet.get("title").asText(),
                    snippet.get("channelTitle").asText(),
                    contentDetails.get("itemCount").asInt()
            );
            
        } catch (WebClientResponseException e) {
            log.error("Error fetching YouTube playlist info: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch YouTube playlist: " + e.getMessage());
        }
    }
    
    /**
     * Fetches all videos from a YouTube playlist
     */
    public List<Track> getPlaylistTracks(String playlistId) {
        List<Track> tracks = new ArrayList<>();
        String pageToken = null;
        
        try {
            log.info("Fetching tracks from YouTube playlist: {}", playlistId);
            
            do {
                final String currentPageToken = pageToken;
                JsonNode response = webClient.get()
                        .uri(uriBuilder -> {
                            var builder = uriBuilder
                                    .path("/playlistItems")
                                    .queryParam("part", "snippet")
                                    .queryParam("playlistId", playlistId)
                                    .queryParam("maxResults", 50)
                                    .queryParam("key", apiKey);
                            
                            if (currentPageToken != null) {
                                builder.queryParam("pageToken", currentPageToken);
                            }
                            
                            return builder.build();
                        })
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();
                
                if (response == null || !response.has("items")) {
                    break;
                }
                
                for (JsonNode item : response.get("items")) {
                    JsonNode snippet = item.get("snippet");
                    
                    // Skip deleted or private videos
                    if (snippet.has("title") && !snippet.get("title").asText().equals("Deleted video") &&
                        !snippet.get("title").asText().equals("Private video")) {
                        
                        String videoId = snippet.get("resourceId").get("videoId").asText();
                        String title = snippet.get("title").asText();
                        
                        tracks.add(Track.fromYouTube(videoId, title));
                    }
                }
                
                pageToken = response.has("nextPageToken") ? response.get("nextPageToken").asText() : null;
                
            } while (pageToken != null);
            
            log.info("Successfully fetched {} tracks from YouTube playlist", tracks.size());
            return tracks;
            
        } catch (WebClientResponseException e) {
            log.error("Error fetching YouTube playlist tracks: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch YouTube playlist tracks: " + e.getMessage());
        }
    }
    
    /**
     * Extracts clean song title from YouTube video title
     * Removes common YouTube video suffixes like (Official Video), [HD], etc.
     */
    public String cleanVideoTitle(String title) {
        if (title == null) return "";
        
        // Remove common YouTube video decorations
        String cleaned = title
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
                .trim();
        
        return cleaned;
    }
}
