# Mithun - TeluTube Dub Repository

**TeluTube Dub** is an application and web suite that translates YouTube videos and audio into an authentic **English-Telugu mix ("Tenglish")**, featuring real-time synchronized voice dubbing with speaker tone cloning, and downloadable `.wav` audio files.

---

## 🌟 Features

- **YouTube Video Voice Translation**:
  - Accepts any YouTube URL (`watch?v=...`, `youtu.be/...`, `shorts/...`) or video ID.
  - Translates video speech into natural conversational **English-Telugu mix ("Tenglish")**, as spoken by popular Telugu creators.
  - Synchronized dual-script subtitles: **Telugu Script (తెలుగు లిపి)** and **Phonetic Roman Tenglish** (English letters) with vocabulary breakdown.
  
- **Speaker Tone Preserving Voice Cloning**:
  - Clones and preserves the original speaker's vocal characteristics.
  - Profiles: *Deep Tech Baritone, Speaker Clone Match, Energetic Vlogger, Calm Narrator, Expressive Female*.
  - Live adjustment of voice pitch (0.7x – 1.4x) and tempo/cadence (0.8x – 1.5x).

- **Export & Provision of Translated Audio File**:
  - Generates a full `.wav` audio file of the translated English-Telugu voiceover.
  - Share or save to device storage with one tap.

- **Audio Speech Transcription**:
  - Built-in microphone audio recorder with animated visualizer.
  - Transcribes live voice or audio clips and translates to English-Telugu mix.

- **Fast Translation**:
  - Powered by Gemini Flash (`gemini-3.5-flash`) for low-latency dubbing.
  - 1-tap instant sample video presets (Tech Reviews, Cosmos, Cooking, Motivation).
  - Local Room database caching for instant 0ms offline reloads.

- **Dual Platform (Android App + Responsive Website)**:
  - **Android Application**: Native Jetpack Compose UI with embedded YouTube player and voice engine.
  - **Web Edition**: Standalone, responsive website located in `/website/` (HTML5, Tailwind CSS, Web Speech API).

---

## 📁 Repository Structure

```
Mithun/
├── app/                              # Android Application Module (Kotlin + Jetpack Compose)
│   ├── src/main/java/com/example/    # Source Code
│   │   ├── data/                     # Models, Room Database & Gemini API Services
│   │   ├── tts/                      # VoiceDubEngine & Speaker Voice Cloning
│   │   └── ui/                       # Composables, Screens, and Theme
│   ├── src/main/assets/web/          # In-app Web Edition Bundle
│   └── build.gradle.kts              # App-level Gradle Configuration
├── website/                          # Standalone Web Application
│   ├── index.html                    # Responsive Web App with Tailwind CSS & Audio Engine
│   ├── app.js                        # Client-side Dubbing & Synthesis Logic
│   └── README.md                     # Web Deployment Guide
├── .env.example                      # Secrets Template (GEMINI_API_KEY)
├── metadata.json                     # AI Studio Platform Metadata
└── README.md                         # Repository Documentation
```

---

## 🚀 Getting Started

### 1. Android App
- Build with Gradle:
  ```bash
  gradle assembleDebug
  ```
- Run tests:
  ```bash
  gradle :app:testDebugUnitTest
  ```

### 2. Website
- Open `website/index.html` directly in any web browser.
- Or deploy to GitHub Pages / Vercel / Netlify.

---

## 🔗 Connecting to Remote GitHub Repository

To push this repository to GitHub under your account:

```bash
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/Mithun.git
git branch -M main
git push -u origin main
```
