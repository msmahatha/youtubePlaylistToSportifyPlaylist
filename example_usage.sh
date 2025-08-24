#!/bin/bash

# 🎵 YouTube to Spotify Playlist Converter - Example Script
# This script demonstrates how to use the API programmatically

echo "🎵 YouTube to Spotify Playlist Converter Demo"
echo "=============================================="

# Configuration
API_BASE="http://localhost:3000"
YOUTUBE_PLAYLIST="https://www.youtube.com/playlist?list=PLQHVzmFMAhPYJOkkNNBKCvJBxlDSUoHT"
SPOTIFY_USER_ID="your_spotify_username"
PLAYLIST_NAME="My Converted Playlist"

echo ""
echo "📋 Step 1: Check Application Health"
health=$(curl -s "$API_BASE/actuator/health")
echo "Health Status: $health"

echo ""
echo "🔐 Step 2: Get Authentication URL"
auth_response=$(curl -s "$API_BASE/auth/login-url")
echo "Auth Response: $auth_response"

echo ""
echo "📝 Step 3: Prepare Conversion Request"
conversion_payload=$(cat << EOF
{
  "youtubePlaylistUrl": "$YOUTUBE_PLAYLIST",
  "spotifyUserId": "$SPOTIFY_USER_ID",
  "playlistName": "$PLAYLIST_NAME",
  "playlistDescription": "Converted from YouTube playlist",
  "isPublic": false
}
EOF
)

echo "Conversion Payload:"
echo "$conversion_payload" | jq .

echo ""
echo "🎯 Step 4: How to Convert (after authentication)"
echo "Run this command after logging in via browser:"
echo ""
echo "curl -X POST $API_BASE/api/convertPlaylist \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -H 'Cookie: JSESSIONID=YOUR_SESSION_ID' \\"
echo "  -d '$conversion_payload'"

echo ""
echo "📊 Step 5: Monitor Conversion Status"
echo "curl $API_BASE/api/status/CONVERSION_ID"

echo ""
echo "📚 Step 6: View All Conversions"
echo "curl $API_BASE/api/conversions"

echo ""
echo "✅ Complete! Your YouTube playlist will be recreated on Spotify!"
echo ""
echo "🌐 For interactive testing, visit: $API_BASE/swagger-ui.html"
echo "🔗 To authenticate, visit: $API_BASE/api/auth/login"
