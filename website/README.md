# TeluTube Dub Web Edition

A standalone, modern, responsive web application to translate YouTube videos and voice audio into an authentic **English-Telugu mix ("Tenglish")** with speaker-preserved voice dubbing and downloadable `.wav` audio files.

## Features
- **YouTube Video Dubbing**: Paste any YouTube video link or select instant demo presets.
- **English-Telugu Mix ("Tenglish")**: Authentic conversational Telugu mixed with English technical terms.
- **Bilingual Subtitle Script**: View in **Telugu Script** (తెలుగు లిపి) and **Phonetic Roman** (English alphabet).
- **Speaker Tone Preserving Voice Cloning**:
  - Deep Tech Baritone (MKBHD / Prasad Tech in Telugu)
  - Speaker Clone Match
  - Energetic Vlogger
  - Calm Narrator
  - Expressive Female
  - Dynamic Pitch and Speed modulation.
- **Download Translated Audio File**: Direct `.wav` audio file synthesis and download in the browser.
- **Audio Microphone Transcription**: Live speech recognition and conversion into English-Telugu mix.
- **Gemini AI Integration**: Uses `gemini-3.5-flash` for ultra-fast translations.

## How to Run
1. Simply open `index.html` in any modern web browser (Chrome, Edge, Safari, Firefox).
2. Or serve locally with any static web server:
   ```bash
   npx serve .
   # or
   python3 -m http.server 8000
   ```
3. Or deploy to GitHub Pages, Vercel, Netlify, or Firebase Hosting with zero configuration needed.
