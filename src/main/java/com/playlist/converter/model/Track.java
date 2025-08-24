package com.playlist.converter.model;

/**
 * Represents a music track from either YouTube or Spotify
 */
public class Track {
    
    private String id;
    private String title;
    private String artist;
    private String album;
    private Long durationMs;
    private String externalUrl;
    private String source; // "youtube" or "spotify"
    
    public Track() {}
    
    public Track(String id, String title, String artist, String album, Long durationMs, String externalUrl, String source) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.durationMs = durationMs;
        this.externalUrl = externalUrl;
        this.source = source;
    }
    
    /**
     * Creates a Track from YouTube video information
     */
    public static Track fromYouTube(String videoId, String title) {
        Track track = new Track();
        track.setId(videoId);
        track.setTitle(title);
        track.setSource("youtube");
        return track;
    }
    
    /**
     * Creates a Track from Spotify track information
     */
    public static Track fromSpotify(String trackId, String title, String artist, String album, Long durationMs, String externalUrl) {
        Track track = new Track();
        track.setId(trackId);
        track.setTitle(title);
        track.setArtist(artist);
        track.setAlbum(album);
        track.setDurationMs(durationMs);
        track.setExternalUrl(externalUrl);
        track.setSource("spotify");
        return track;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
