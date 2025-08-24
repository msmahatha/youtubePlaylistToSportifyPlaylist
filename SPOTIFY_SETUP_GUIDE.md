# 🎵 Spotify OAuth2 Setup Guide

## ✅ Current Configuration Status
Your Spotify credentials have been successfully configured in the application!

### 📋 Your Spotify App Details
- **Client ID**: `21404d78bc55495588b8933cc3ae4468` ✓
- **Client Secret**: `0a9873ec93ba49489715f70df8f452db` ✓
- **Current Redirect URI**: `http://127.0.0.1:3000/callback`

## ⚠️ Required Update in Spotify Dashboard

You need to update the redirect URI in your Spotify app to match our Spring Boot application:

### 🔧 Steps to Update Redirect URI

1. **Go to Spotify Developer Dashboard**
   - Visit: https://developer.spotify.com/dashboard
   - Login with your Spotify account

2. **Select Your App**
   - Find your app with Client ID: `21404d78bc55495588b8933cc3ae4468`
   - Click on the app name

3. **Update Redirect URIs**
   - Click on "Edit Settings"
   - In the "Redirect URIs" section, **replace**:
     ```
     http://127.0.0.1:3000/callback
     ```
   - **With**:
     ```
     http://127.0.0.1:8080/api/login/oauth2/code/spotify
     ```

4. **Save Changes**
   - Click "Save" at the bottom of the settings

## 🚀 Testing OAuth2 Flow

Once you've updated the Spotify redirect URI, test the authentication flow:

### 1. Get Login URL
```bash
curl -v http://localhost:8080/api/auth/login-url
```

### 2. Follow the Redirect
The application will redirect you to Spotify's authorization page:
```
https://accounts.spotify.com/authorize?response_type=code&client_id=21404d78bc55495588b8933cc3ae4468&scope=playlist-modify-public%20playlist-modify-private%20user-read-private&state=...&redirect_uri=http://127.0.0.1:8080/api/login/oauth2/code/spotify
```

### 3. Complete Authentication
- Open the above URL in your browser
- Login to Spotify and authorize the app
- You'll be redirected back to our application

### 4. Test Playlist Conversion
Once authenticated, you can test the playlist conversion:
```bash
curl -X POST http://localhost:8080/api/convertPlaylist \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=YOUR_SESSION_ID" \
  -d '{
    "youtubePlaylistUrl": "https://www.youtube.com/playlist?list=PLv3TTBr1W_9tppikBxAE_G6qjWdBljBHJ",
    "spotifyUserId": "your-spotify-user-id",
    "playlistName": "My Converted Playlist",
    "playlistDescription": "Converted from YouTube",
    "isPublic": false
  }'
```

## 🎯 What's Already Working

✅ **Application Configuration**: Spotify credentials are properly configured
✅ **OAuth2 Flow**: Authorization URLs are being generated correctly
✅ **YouTube API**: Configured and ready to fetch playlist data
✅ **Database**: H2 database is set up for storing conversion results
✅ **API Documentation**: Swagger UI available at http://localhost:8080/api/swagger-ui.html

## 🔐 Security Notes

- **Client Secret**: Keep your client secret (`0a9873ec93ba49489715f70df8f452db`) secure
- **Environment Variables**: Consider using `.env` file for production deployment
- **HTTPS**: For production, use HTTPS URLs for redirect URIs

## 📝 Next Steps

1. **Update Spotify Redirect URI** (as described above)
2. **Test OAuth2 authentication** in browser
3. **Try playlist conversion** with a real YouTube playlist
4. **Deploy to production** with proper environment variables

---

**Last Updated**: August 23, 2025
**Status**: ✅ Credentials Configured - Redirect URI Update Required
