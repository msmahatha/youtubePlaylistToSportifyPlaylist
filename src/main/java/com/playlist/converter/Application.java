package com.playlist.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for YouTube to Spotify Playlist Converter
 * 
 * This Spring Boot application provides REST API endpoints to convert
 * YouTube playlists to Spotify playlists using OAuth2 authentication.
 * 
 * @author GitHub Copilot
 * @version 1.0
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
