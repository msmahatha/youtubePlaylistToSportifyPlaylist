#!/bin/bash

# Script to restart ngrok and update Spring Boot configuration
# Usage: ./restart-ngrok.sh

echo "🔄 Restarting ngrok and updating configuration..."

# Kill existing ngrok processes
echo "Stopping existing ngrok processes..."
pkill -f ngrok
sleep 2

# Start ngrok in background
echo "Starting new ngrok tunnel..."
nohup ngrok http 3000 > ngrok.log 2>&1 &
sleep 5

# Extract the new ngrok URL
echo "Extracting ngrok URL..."
NGROK_URL=$(curl -s http://localhost:4040/api/tunnels | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    for tunnel in data['tunnels']:
        if tunnel['proto'] == 'https':
            print(tunnel['public_url'])
            break
except:
    print('ERROR: Could not extract ngrok URL')
")

if [[ $NGROK_URL == *"ngrok-free.app"* ]]; then
    echo "✅ New ngrok URL: $NGROK_URL"
    
    # Update application.properties
    echo "Updating application.properties..."
    sed -i '' "s|spring.security.oauth2.client.registration.spotify.redirect-uri=.*|spring.security.oauth2.client.registration.spotify.redirect-uri=$NGROK_URL/callback|" src/main/resources/application.properties
    
    # Restart Spring Boot
    echo "Restarting Spring Boot..."
    pkill -f "spring-boot:run"
    sleep 3
    nohup ./mvnw spring-boot:run > app.log 2>&1 &
    
    echo "✅ Done! Your application is now available at: $NGROK_URL"
    echo "🔗 Update your Spotify app redirect URI to: $NGROK_URL/callback"
else
    echo "❌ Failed to get ngrok URL. Check ngrok.log for details."
fi
