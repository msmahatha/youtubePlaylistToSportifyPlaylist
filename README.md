# YouTube to Spotify Playlist Converter

A Spring Boot REST API service that converts YouTube playlists to Spotify playlists using OAuth2 authentication.

## Features

- 🎵 Convert YouTube playlists to Spotify playlists
- 🔐 Secure OAuth2 authentication with Spotify
- 🎯 Smart track matching using YouTube video titles
- 📊 Detailed conversion reports with matched/skipped tracks
- 🔄 Asynchronous processing for large playlists
- 📖 OpenAPI/Swagger documentation
- 🐳 Docker support (optional)

## Tech Stack

- **Java 17**
- **Spring Boot 3.x**
- **Maven** - Dependency management
- **Spring Security OAuth2** - Spotify authentication
- **WebClient** - REST API calls
- **H2 Database** - In-memory storage
- **Lombok** - Boilerplate reduction
- **SpringDoc OpenAPI** - API documentation

## Prerequisites

1. **Java 17** or higher
2. **Maven 3.6+**
3. **Spotify Developer Account** - [Create one here](https://developer.spotify.com/)
4. **YouTube Data API Key** - [Get one here](https://developers.google.com/youtube/v3/getting-started)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd youtube-spotify-converter
```

### 2. Spotify App Configuration

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Create a new app
3. Note down the **Client ID** and **Client Secret**
4. Add redirect URI: `http://localhost:8080/login/oauth2/code/spotify`

### 3. YouTube API Configuration

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable YouTube Data API v3
4. Create credentials (API Key)
5. Note down the **API Key**

### 4. Environment Variables

Set the following environment variables:

```bash
export SPOTIFY_CLIENT_ID=your_spotify_client_id
export SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
export YOUTUBE_API_KEY=your_youtube_api_key
```

Or create a `.env` file in the project root:

```properties
SPOTIFY_CLIENT_ID=your_spotify_client_id
SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
YOUTUBE_API_KEY=your_youtube_api_key
```

### 5. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or using Java
mvn clean package
java -jar target/youtube-spotify-converter-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Docs**: http://localhost:8080/api/api-docs

## API Endpoints

### Authentication
- `GET /api/auth/login-url` - Get Spotify OAuth2 login URL
- `GET /api/auth/success` - OAuth2 success callback
- `GET /api/auth/logout` - Logout user
- `GET /api/user` - Get current user info

### Playlist Conversion
- `POST /api/convertPlaylist` - Convert YouTube playlist to Spotify
- `GET /api/status/{conversionId}` - Get conversion status
- `GET /api/conversions` - Get user's conversion history

## Usage Example

### 1. Authenticate with Spotify

```bash
# Get login URL
curl http://localhost:8080/api/auth/login-url

# Visit the returned URL to login with Spotify
```

### 2. Convert a Playlist

```bash
curl -X POST http://localhost:8080/api/convertPlaylist \
  -H "Content-Type: application/json" \
  -d '{
    "youtubePlaylistUrl": "https://www.youtube.com/playlist?list=PLXyAs_FpoMmQQwv6DOAC7Z4ckOD_Ct7zR",
    "spotifyUserId": "your_spotify_user_id",
    "playlistName": "My Converted Playlist",
    "playlistDescription": "Converted from YouTube",
    "isPublic": false
  }'
```

### 3. Check Conversion Status

```bash
curl http://localhost:8080/api/status/{conversionId}
```

## Sample Request/Response

### Conversion Request
```json
{
  "youtubePlaylistUrl": "https://www.youtube.com/playlist?list=PLXyAs_FpoMmQQwv6DOAC7Z4ckOD_Ct7zR",
  "spotifyUserId": "john_doe_spotify",
  "playlistName": "My Awesome Playlist",
  "playlistDescription": "Converted from my YouTube favorites",
  "isPublic": false
}
```

### Conversion Response
```json
{
  "conversionId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "status": "COMPLETED",
  "spotifyPlaylistId": "37i9dQZF1DXcBWIGoYBM5M",
  "spotifyPlaylistUrl": "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M",
  "totalTracks": 50,
  "matchedTracks": 42,
  "skippedTracks": 8,
  "matchedTrackNames": [
    "The Weeknd - Blinding Lights",
    "Dua Lipa - Levitating"
  ],
  "skippedTrackNames": [
    "Some rare track not on Spotify",
    "Podcast episode"
  ],
  "message": "Conversion completed successfully",
  "createdAt": "2023-08-23T10:30:00",
  "completedAt": "2023-08-23T10:32:15"
}
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8080

# Database (H2)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# OAuth2 (Spotify)
spring.security.oauth2.client.registration.spotify.scope=playlist-modify-public,playlist-modify-private,user-read-private

# API Configuration
app.conversion.max-tracks=100
app.conversion.timeout-minutes=10
```

## Docker Support

### Build Docker Image

```bash
docker build -t youtube-spotify-converter .
```

### Run with Docker

```bash
docker run -p 8080:8080 \
  -e SPOTIFY_CLIENT_ID=your_client_id \
  -e SPOTIFY_CLIENT_SECRET=your_client_secret \
  -e YOUTUBE_API_KEY=your_api_key \
  youtube-spotify-converter
```

## Development

### Running Tests

```bash
mvn test
```

### Database Console

H2 database console is available at: http://localhost:8080/api/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Client    │    │   Controller    │    │    Service      │
│                 │───▶│                 │───▶│                 │
│  (Browser/API)  │    │  (REST Layer)   │    │ (Business Logic)│
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                       ┌─────────────────┐             │
                       │   Repository    │◀────────────┘
                       │                 │
                       │ (Data Access)   │
                       └─────────────────┘
                                │
                       ┌─────────────────┐
                       │   H2 Database   │
                       │                 │
                       │  (In-Memory)    │
                       └─────────────────┘

External APIs:
┌─────────────────┐    ┌─────────────────┐
│  YouTube API    │    │  Spotify API    │
│                 │    │                 │
│ (Video Data)    │    │ (Music Data)    │
└─────────────────┘    └─────────────────┘
```

## Troubleshooting

### Common Issues

1. **"Invalid YouTube playlist URL"**
   - Ensure the URL format is correct: `https://www.youtube.com/playlist?list=PLAYLIST_ID`
   - Make sure the playlist is public

2. **"No valid Spotify access token"**
   - Re-authenticate with Spotify via `/oauth2/authorization/spotify`
   - Check if your Spotify app has correct redirect URI

3. **"Quota exceeded" errors**
   - YouTube API has daily quotas - wait 24 hours or use different API key
   - Spotify API has rate limits - the app handles retries automatically

### Logs

Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For issues and questions:
- Create an issue on GitHub
- Check the [Spotify Web API documentation](https://developer.spotify.com/documentation/web-api/)
- Check the [YouTube Data API documentation](https://developers.google.com/youtube/v3)
