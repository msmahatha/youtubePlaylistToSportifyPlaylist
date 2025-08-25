#!/bin/bash

# YouTube to Spotify Playlist Converter - Startup Script

echo "🎵 YouTube to Spotify Playlist Converter"
echo "========================================"

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ Error: .env file not found!"
    echo "📝 Please copy .env.example to .env and fill in your credentials:"
    echo "   cp .env.example .env"
    echo "   # Then edit .env with your API keys"
    exit 1
fi

# Load environment variables
echo "🔧 Loading environment variables..."
export $(cat .env | grep -v '^#' | xargs)

# Check required variables
if [ -z "$SPOTIFY_CLIENT_ID" ] || [ -z "$SPOTIFY_CLIENT_SECRET" ] || [ -z "$YOUTUBE_API_KEY" ]; then
    echo "❌ Error: Missing required environment variables!"
    echo "📝 Please check your .env file contains:"
    echo "   - SPOTIFY_CLIENT_ID"
    echo "   - SPOTIFY_CLIENT_SECRET" 
    echo "   - YOUTUBE_API_KEY"
    exit 1
fi

echo "✅ Environment variables loaded successfully"
echo "🚀 Starting the application..."
echo ""

# Start the application
./mvnw spring-boot:run
