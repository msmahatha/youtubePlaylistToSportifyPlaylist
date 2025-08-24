package com.playlist.converter.model;

import java.util.List;

/**
 * Represents a playlist from either YouTube or Spotify
 */
public class Playlist {
    
    private String id;
    private String name;
    private String description;
    private String ownerName;
    private String ownerId;
    private Integer trackCount;
    private List<Track> tracks;
    private String externalUrl;
    private String source; // "youtube" or "spotify"
    private Boolean isPublic;
    
    public Playlist() {}
    
    public Playlist(String id, String name, String description, String ownerName, String ownerId, 
                   Integer trackCount, List<Track> tracks, String externalUrl, String source, Boolean isPublic) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.trackCount = trackCount;
        this.tracks = tracks;
        this.externalUrl = externalUrl;
        this.source = source;
        this.isPublic = isPublic;
    }
    
    /**
     * Creates a Playlist from YouTube playlist information
     */
    public static Playlist fromYouTube(String playlistId, String title, String channelTitle, Integer itemCount) {
        Playlist playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setName(title);
        playlist.setOwnerName(channelTitle);
        playlist.setTrackCount(itemCount);
        playlist.setSource("youtube");
        return playlist;
    }
    
    /**
     * Creates a Playlist from Spotify playlist information
     */
    public static Playlist fromSpotify(String playlistId, String name, String description, String ownerName, String ownerId, Integer trackCount, String externalUrl) {
        Playlist playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setOwnerName(ownerName);
        playlist.setOwnerId(ownerId);
        playlist.setTrackCount(trackCount);
        playlist.setExternalUrl(externalUrl);
        playlist.setSource("spotify");
        return playlist;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(Integer trackCount) {
        this.trackCount = trackCount;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
