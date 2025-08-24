# 🎵 YouTube to Spotify Playlist Converter - Complete Usage Guide

## 🚀 How It Works

The YouTube to Spotify Playlist Converter takes a YouTube playlist URL and recreates it on Spotify by:

1. **Reading YouTube Playlist** - Extracts all video titles and metadata from a YouTube playlist
2. **Searching Spotify** - Finds matching songs on Spotify using intelligent search algorithms
3. **Creating Spotify Playlist** - Creates a new playlist in your Spotify account with the matched songs
4. **Track Conversion Status** - Provides detailed reports on which songs were successfully converted

---

## 📋 Step-by-Step Usage Instructions

### Step 1: Start the Application 🏃‍♂️

```bash
# Navigate to project directory
cd playlist

# Start the application
./mvnw spring-boot:run

# Application will start on http://localhost:3000
```

### Step 2: Authenticate with Spotify 🔐

**Option A: Using Browser**
1. Open: http://localhost:3000/api/auth/login
2. You'll be redirected to Spotify login
3. Grant permissions to the app
4. You'll be redirected back with authentication success

**Option B: Using API**
```bash
# Get login URL
curl http://localhost:3000/auth/login-url

# Response will show the Spotify authorization URL
```

### Step 3: Convert YouTube Playlist to Spotify 🎯

**API Endpoint:** `POST /api/convertPlaylist`

```bash
curl -X POST http://localhost:3000/api/convertPlaylist \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID" \
  -d '{
    "youtubePlaylistUrl": "https://www.youtube.com/playlist?list=PLexampleplaylistID",
    "spotifyUserId": "your-spotify-user-id",
    "playlistName": "My Converted Playlist",
    "isPublic": false
  }'
```

**Example Request Body:**
```json
{
  "youtubePlaylistUrl": "https://www.youtube.com/playlist?list=PLQHVzmFMAhPYJOkkNNBKCvJBxlDSUQoHT",
  "spotifyUserId": "your_spotify_username",
  "playlistName": "Awesome Rock Playlist",
  "isPublic": true
}
```

**Response:**
```json
{
  "conversionId": "conv_12345",
  "status": "IN_PROGRESS",
  "message": "Playlist conversion started successfully",
  "estimatedTime": "2-5 minutes",
  "tracksFound": 25
}
```

### Step 4: Check Conversion Status ⏳

```bash
curl http://localhost:3000/api/status/conv_12345
```

**Response:**
```json
{
  "conversionId": "conv_12345",
  "status": "COMPLETED",
  "progress": 100,
  "tracksProcessed": 25,
  "tracksSuccessful": 23,
  "tracksFailed": 2,
  "spotifyPlaylistId": "37i9dQZF1DXcF6B6QPhFDv",
  "failedTracks": [
    {
      "title": "Obscure Song Title",
      "reason": "Not found on Spotify"
    }
  ]
}
```

### Step 5: View All Your Conversions 📚

```bash
curl http://localhost:3000/api/conversions
```

---

## 🎯 Real-World Example

Let's convert a popular YouTube playlist:

### 1. Start the Application
```bash
./mvnw spring-boot:run
```

### 2. Authenticate
Visit: http://localhost:3000/api/auth/login

### 3. Convert Playlist
```bash
curl -X POST http://localhost:3000/api/convertPlaylist \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=ABC123" \
  -d '{
    "youtubePlaylistUrl": "https://www.youtube.com/playlist?list=PLQHVzmFMAhPYJOkkNNBKCvJBxlDSUoHT",
    "spotifyUserId": "johndoe",
    "playlistName": "My YouTube Hits",
    "isPublic": false
  }'
```

### 4. Monitor Progress
```bash
# Check status every 30 seconds
curl http://localhost:3000/api/status/conv_12345
```

### 5. Enjoy Your Playlist! 🎉
The converted playlist will appear in your Spotify account!

---

## 🔧 API Endpoints Reference

### Authentication Endpoints
- `GET /api/auth/login` - Initiate Spotify login
- `GET /auth/login-url` - Get Spotify authorization URL
- `GET /callback` - OAuth callback (automatic)

### Conversion Endpoints
- `POST /api/convertPlaylist` - Start playlist conversion
- `GET /api/status/{id}` - Check conversion status
- `GET /api/conversions` - List all conversions
- `GET /api/user` - Get authenticated user info

### System Endpoints
- `GET /actuator/health` - Application health check
- `GET /swagger-ui.html` - Interactive API documentation

---

## ⚙️ Configuration Options

### YouTube Playlist URL Formats Supported:
- `https://www.youtube.com/playlist?list=PLAYLIST_ID`
- `https://youtube.com/playlist?list=PLAYLIST_ID`
- `https://m.youtube.com/playlist?list=PLAYLIST_ID`

### Spotify Playlist Options:
- **Public**: Visible to everyone on Spotify
- **Private**: Only visible to you
- **Collaborative**: Others can add songs (if public)

### Conversion Limits:
- **Max tracks per playlist**: 100 songs
- **Timeout**: 10 minutes per conversion
- **Concurrent conversions**: 5 per user

---

## 🎵 How the Matching Algorithm Works

### 1. Extract YouTube Video Data
- Video title
- Channel name
- Description
- Duration

### 2. Clean and Normalize
- Remove common YouTube suffixes ("Official Video", "HD", etc.)
- Extract artist and song name
- Handle various title formats

### 3. Search Spotify
- Primary search: "artist song"
- Fallback search: song title only
- Duration matching for accuracy
- Popularity-based ranking

### 4. Create Playlist
- Create playlist with your specified name
- Add all successfully matched tracks
- Generate detailed conversion report

---

## 📊 Conversion Status Types

- **`PENDING`** - Conversion request received
- **`IN_PROGRESS`** - Currently processing tracks
- **`COMPLETED`** - All tracks processed successfully
- **`PARTIAL_SUCCESS`** - Some tracks couldn't be found
- **`FAILED`** - Conversion failed due to error

---

## 🛠️ Advanced Usage

### Batch Conversions
```bash
# Convert multiple playlists
for playlist in "PLlist1" "PLlist2" "PLlist3"; do
  curl -X POST http://localhost:3000/api/convertPlaylist \
    -H "Content-Type: application/json" \
    -d "{\"youtubePlaylistUrl\":\"https://youtube.com/playlist?list=$playlist\",\"playlistName\":\"Playlist $playlist\"}"
done
```

### Monitor with Notifications
```bash
# Script to monitor conversion and notify when complete
conversion_id="conv_12345"
while true; do
  status=$(curl -s http://localhost:3000/api/status/$conversion_id | jq -r '.status')
  if [[ "$status" == "COMPLETED" || "$status" == "FAILED" ]]; then
    echo "Conversion $status!"
    break
  fi
  sleep 30
done
```

---

## 🎉 You're Ready to Convert!

Your YouTube to Spotify Playlist Converter is now ready to transform your favorite YouTube playlists into Spotify gold! 

**Quick Start:**
1. `./mvnw spring-boot:run`
2. Visit http://localhost:3000/api/auth/login
3. Use the API or Swagger UI to convert playlists
4. Enjoy your music on Spotify! 🎵

---

*Happy playlist converting! 🎶*
