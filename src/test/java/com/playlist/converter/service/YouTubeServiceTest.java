package com.playlist.converter.service;

import com.playlist.converter.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for YouTubeService
 */
@ExtendWith(MockitoExtension.class)
class YouTubeServiceTest {
    
    @Mock
    private WebClient webClient;
    
    private YouTubeService youTubeService;
    
    @BeforeEach
    void setUp() {
        youTubeService = new YouTubeService(webClient, "test-api-key");
    }
    
    @Test
    void testCleanVideoTitle() {
        // Test removing common YouTube decorations
        assertEquals("Song Title", youTubeService.cleanVideoTitle("Song Title (Official Video)"));
        assertEquals("Artist - Song", youTubeService.cleanVideoTitle("Artist - Song [Official Music Video]"));
        assertEquals("Beautiful Song", youTubeService.cleanVideoTitle("Beautiful Song (HD)"));
        assertEquals("Great Track", youTubeService.cleanVideoTitle("Great Track [4K]"));
        assertEquals("Awesome Music", youTubeService.cleanVideoTitle("Awesome Music (Lyrics)"));
        
        // Test with multiple decorations
        assertEquals("Clean Title", youTubeService.cleanVideoTitle("Clean Title (Official Video) [HD] (Lyrics)"));
        
        // Test with null input
        assertEquals("", youTubeService.cleanVideoTitle(null));
        
        // Test with empty input
        assertEquals("", youTubeService.cleanVideoTitle(""));
        
        // Test with no decorations
        assertEquals("Normal Title", youTubeService.cleanVideoTitle("Normal Title"));
    }
    
    @Test
    void testTrackFromYouTube() {
        Track track = Track.fromYouTube("video123", "Test Song");
        
        assertNotNull(track);
        assertEquals("video123", track.getId());
        assertEquals("Test Song", track.getTitle());
        assertEquals("youtube", track.getSource());
        assertNull(track.getArtist());
        assertNull(track.getAlbum());
    }
}
