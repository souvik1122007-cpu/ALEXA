
# NOVA — Personal AI Assistant

NOVA is a personal Android AI assistant powered by an online AI backend.

## Features
- AI chat interface
- Chat history stored locally
- Backend URL and access token settings
- Secure settings storage
- Automatic Android APK build using GitHub Actions

## Android App
- Language: Kotlin
- UI: Jetpack Compose
- Local database: Room
- Minimum Android version: Android 8.0

## Backend
The backend uses FastAPI and an OpenAI-compatible chat API.

## Configuration
Configure these environment variables on your backend host:

- NOVA_ACCESS_TOKEN
- AI_API_KEY
- AI_BASE_URL
- AI_MODEL

Never commit real API keys or access tokens to GitHub.

## Build APK
Open the repository's Actions tab and run the
"Build NOVA Android APK" workflow.

Download the NOVA-debug-APK artifact after a successful build.

## Project Status
Development version. Build and test before everyday use.
