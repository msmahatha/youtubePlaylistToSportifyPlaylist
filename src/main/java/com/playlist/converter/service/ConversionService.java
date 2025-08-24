package com.playlist.converter.service;

import com.playlist.converter.dto.ConversionRequest;
import com.playlist.converter.model.ConversionResult;
import com.playlist.converter.model.Playlist;
import com.playlist.converter.model.Track;
import com.playlist.converter.repository.ConversionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing playlist conversion operations
 */
@Service
public class ConversionService {
    
    private static final Logger log = LoggerFactory.getLogger(ConversionService.class);
    
    private final YouTubeService youTubeService;
    private final SpotifyService spotifyService;
    private final ConversionRepository conversionRepository;
    private final OAuth2TokenService oAuth2TokenService;
    
    public ConversionService(YouTubeService youTubeService, SpotifyService spotifyService, 
                           ConversionRepository conversionRepository, OAuth2TokenService oAuth2TokenService) {
        this.youTubeService = youTubeService;
        this.spotifyService = spotifyService;
        this.conversionRepository = conversionRepository;
        this.oAuth2TokenService = oAuth2TokenService;
    }
    
    /**
     * Initiates a playlist conversion process
     */
    public String initiateConversion(ConversionRequest request) {
        String conversionId = UUID.randomUUID().toString();
        
        // Create conversion record
        ConversionResult conversion = new ConversionResult();
        conversion.setConversionId(conversionId);
        conversion.setYoutubePlaylistUrl(request.getYoutubePlaylistUrl());
        conversion.setSpotifyUserId(request.getSpotifyUserId());
        conversion.setStatus(ConversionResult.ConversionStatus.PENDING);
        
        conversionRepository.save(conversion);
        
        // Start async conversion
        performConversionAsync(conversionId, request);
        
        return conversionId;
    }
    
    /**
     * Performs the actual conversion asynchronously
     */
    @Async
    public void performConversionAsync(String conversionId, ConversionRequest request) {
        ConversionResult conversion = conversionRepository.findByConversionId(conversionId)
                .orElseThrow(() -> new RuntimeException("Conversion not found: " + conversionId));
        
        try {
            log.info("Starting conversion for ID: {}", conversionId);
            
            // Update status to IN_PROGRESS
            conversion.setStatus(ConversionResult.ConversionStatus.IN_PROGRESS);
            conversionRepository.save(conversion);
            
            // Step 1: Extract YouTube playlist ID
            String playlistId = request.extractPlaylistId();
            if (playlistId == null) {
                throw new RuntimeException("Invalid YouTube playlist URL");
            }
            
            // Step 2: Fetch YouTube playlist info
            Playlist youtubePlaylist = youTubeService.getPlaylistInfo(playlistId);
            log.info("YouTube playlist info: {} tracks", youtubePlaylist.getTrackCount());
            
            // Step 3: Fetch YouTube tracks
            List<Track> youtubeTracks = youTubeService.getPlaylistTracks(playlistId);
            conversion.setTotalTracks(youtubeTracks.size());
            conversionRepository.save(conversion);
            
            // Step 4: Get Spotify access token
            String accessToken = oAuth2TokenService.getAccessToken(request.getSpotifyUserId());
            if (accessToken == null) {
                throw new RuntimeException("No valid Spotify access token found for user: " + request.getSpotifyUserId());
            }
            
            // Step 5: Search for tracks on Spotify
            List<String> matchedTrackUris = new ArrayList<>();
            List<String> matchedTrackNames = new ArrayList<>();
            List<String> skippedTrackNames = new ArrayList<>();
            
            for (Track youtubeTrack : youtubeTracks) {
                String cleanTitle = youTubeService.cleanVideoTitle(youtubeTrack.getTitle());
                String searchQuery = spotifyService.prepareSearchQuery(cleanTitle);
                
                Track spotifyTrack = spotifyService.searchTrack(searchQuery, accessToken);
                
                if (spotifyTrack != null) {
                    matchedTrackUris.add(spotifyService.getTrackUri(spotifyTrack.getId()));
                    matchedTrackNames.add(spotifyTrack.getArtist() + " - " + spotifyTrack.getTitle());
                    log.debug("Matched: {} -> {}", youtubeTrack.getTitle(), spotifyTrack.getTitle());
                } else {
                    skippedTrackNames.add(youtubeTrack.getTitle());
                    log.debug("Skipped: {}", youtubeTrack.getTitle());
                }
            }
            
            // Step 6: Create Spotify playlist
            String playlistName = request.getPlaylistName() != null ? 
                    request.getPlaylistName() : 
                    "Converted from YouTube: " + youtubePlaylist.getName();
            
            String playlistDescription = request.getPlaylistDescription() != null ?
                    request.getPlaylistDescription() :
                    "Playlist converted from YouTube using Playlist Converter";
            
            boolean isPublic = request.getIsPublic() != null ? request.getIsPublic() : false;
            
            Playlist spotifyPlaylist = spotifyService.createPlaylist(
                    request.getSpotifyUserId(),
                    playlistName,
                    playlistDescription,
                    isPublic,
                    accessToken
            );
            
            // Step 7: Add tracks to Spotify playlist
            if (!matchedTrackUris.isEmpty()) {
                boolean success = spotifyService.addTracksToPlaylist(
                        spotifyPlaylist.getId(),
                        matchedTrackUris,
                        accessToken
                );
                
                if (!success) {
                    throw new RuntimeException("Failed to add tracks to Spotify playlist");
                }
            }
            
            // Step 8: Update conversion result
            conversion.setStatus(ConversionResult.ConversionStatus.COMPLETED);
            conversion.setSpotifyPlaylistId(spotifyPlaylist.getId());
            conversion.setSpotifyPlaylistUrl(spotifyPlaylist.getExternalUrl());
            conversion.setMatchedTracks(matchedTrackNames.size());
            conversion.setSkippedTracks(skippedTrackNames.size());
            conversion.setMatchedTrackNames(matchedTrackNames);
            conversion.setSkippedTrackNames(skippedTrackNames);
            conversion.setCompletedAt(LocalDateTime.now());
            
            conversionRepository.save(conversion);
            
            log.info("Conversion completed successfully: {} (matched: {}, skipped: {})", 
                    conversionId, matchedTrackNames.size(), skippedTrackNames.size());
            
        } catch (Exception e) {
            log.error("Conversion failed for ID {}: {}", conversionId, e.getMessage(), e);
            
            // Update conversion with error
            conversion.setStatus(ConversionResult.ConversionStatus.FAILED);
            conversion.setErrorMessage(e.getMessage());
            conversion.setCompletedAt(LocalDateTime.now());
            
            conversionRepository.save(conversion);
        }
    }
    
    /**
     * Gets the status of a conversion
     */
    public ConversionResult getConversionStatus(String conversionId) {
        return conversionRepository.findByConversionId(conversionId)
                .orElseThrow(() -> new RuntimeException("Conversion not found: " + conversionId));
    }
    
    /**
     * Gets all conversions for a user
     */
    public List<ConversionResult> getUserConversions(String spotifyUserId) {
        return conversionRepository.findBySpotifyUserIdOrderByCreatedAtDesc(spotifyUserId);
    }
}
