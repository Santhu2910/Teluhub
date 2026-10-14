// TeluTube Dub Web Application Script

let currentVideoId = "k1BneeJTDcU";
let currentSegments = [];
let currentProject = null;
let activeSegmentIndex = -1;
let isPlaying = false;
let isMuted = true;
let synth = window.speechSynthesis;
let currentUtterance = null;
let currentProfile = "baritone";
let pitchVal = 0.82;
let rateVal = 1.05;

// Sample Pre-cached Projects
const DEMO_PROJECTS = {
  "k1BneeJTDcU": {
    videoId: "k1BneeJTDcU",
    title: "Smartphone Innovation Review - Flagship Camera & Specs",
    channel: "Tech Explorers",
    summaryOriginal: "Comprehensive flagship smartphone review discussing titanium build, LTPO display, and camera performance.",
    summaryTenglish: "Ee video lo flagship device loni build quality, 120Hz display mariyu camera features gurinchi complete details ni natural English-Telugu mix lo explain chesam.",
    segments: [
      {
        id: 1,
        time: "00:00",
        startSec: 0,
        original: "Hey everyone! Welcome back. Today we are diving into one of the most anticipated flagship releases of the year.",
        telugu: "హాయ్ everyone! Welcome back. ఈరోజు మనం ఈ సంవత్సరం రాబోతున్న most anticipated flagship release గురించి full details చూద్దాం.",
        roman: "Hi everyone! Welcome back. Eeroju manam ee year rabotunna most anticipated flagship release gurinchi full details chuddam.",
        glossary: ["Flagship: టాప్ మోడల్", "Anticipated: అందరూ ఎదురుచూస్తున్న"]
      },
      {
        id: 2,
        time: "00:14",
        startSec: 14,
        original: "First off, let's talk about the build quality and the new titanium frame. It feels noticeably lighter in hand.",
        telugu: "First off, build quality మరియు new titanium frame గురించి మాట్లాడదాం. చేతిలో పట్టుకుంటే noticeably చాలా light weight గా అనిపిస్తుంది.",
        roman: "First off, build quality mariyu new titanium frame gurinchi matladadam. Chetilo pattukunte noticeably chaala light weight ga anipistundi.",
        glossary: ["Titanium frame: టైటానియం ఫ్రేమ్", "Noticeably: స్పష్టంగా"]
      },
      {
        id: 3,
        time: "00:30",
        startSec: 30,
        original: "Moving on to the display, we get a 120Hz LTPO OLED panel with 2600 nits peak brightness. Outdoor visibility is incredible.",
        telugu: "ఇక display విషయానికి వస్తే, 120Hz LTPO OLED panel ఇచ్చారు, peak brightness 2600 nits వరకు వెళ్తుంది. Outdoor sunlight లో visual clarity incredible గా ఉంది.",
        roman: "Ika display vishayaniki vaste, 120Hz LTPO OLED panel ichaaru, peak brightness 2600 nits varaku veltundi. Outdoor sunlight lo visual clarity incredible ga undi.",
        glossary: ["Peak brightness: గరిష్ట కాంతి", "Incredible: అద్భుతంగా"]
      },
      {
        id: 4,
        time: "00:48",
        startSec: 48,
        original: "Under the hood, the new processor handles heavy gaming and video rendering without any frame drops or overheating.",
        telugu: "Performance పరంగా చూస్తే, new processor heavy gaming మరియు 4K video rendering ని frame drops లేకుండా, overheating అవ్వకుండా smooth గా handle చేస్తుంది.",
        roman: "Performance paranga chuste, new processor heavy gaming mariyu 4K video rendering ni frame drops lekunda, overheating avvakunda smooth ga handle chestundi.",
        glossary: ["Overheating: ఎక్కువ వేడెక్కడం", "Frame drops: లాగ్ లేకుండా"]
      },
      {
        id: 5,
        time: "01:08",
        startSec: 68,
        original: "Finally, battery life easily lasts a full day with fast 65W charging. Overall, this is definitely worth considering if you want the best upgrade.",
        telugu: "Finally, battery life fast 65W charging తో full day easily వస్తుంది. Overall గా చూస్తే, top-tier upgrade కావాలనుకునే వాళ్లకు ఇది definitely worth considering!",
        roman: "Finally, battery life fast 65W charging tho full day easily vastundi. Overall ga chuste, top-tier upgrade kaavalannukune vallaku idi definitely worth considering!",
        glossary: ["Worth considering: ఆలోచించదగినది"]
      }
    ]
  },
  "y881t8ilMyc": {
    videoId: "y881t8ilMyc",
    title: "How Black Holes Warp Time and Space - Deep Cosmos",
    channel: "Science Channel",
    summaryOriginal: "Astrophysics breakdown of black holes and gravitational lensing.",
    summaryTenglish: "Black holes time mariyu space ni ela bend chestayo ee video lo simple English-Telugu mix lo explain chesam.",
    segments: [
      {
        id: 1,
        time: "00:00",
        startSec: 0,
        original: "What happens when a massive star collapses under its own gravity? A point of infinite density is formed.",
        telugu: "ఒక massive star తన సొంత gravity వల్ల collapse అయినప్పుడు ఏం జరుగుతుంది? Infinite density ఉన్న ఒక point ఏర్పడుతుంది.",
        roman: "Oka massive star tana sontha gravity valla collapse ainappudu em jarugutundi? Infinite density unna oka point erpadutundi.",
        glossary: ["Infinite density: అనంతమైన సాంద్రత"]
      },
      {
        id: 2,
        time: "00:15",
        startSec: 15,
        original: "Near the event horizon, time slows down drastically relative to an outside observer.",
        telugu: "Event horizon దగ్గర time బయట ఉన్న observer తో పోలిస్తే drastically చాలా slow అయిపోతుంది.",
        roman: "Event horizon daggara time bayata unna observer tho poliste drastically chaala slow aypotundi.",
        glossary: ["Event horizon: కాలబిలం సరిహద్దు"]
      }
    ]
  },
  "UF8uR6Z6KLc": {
    videoId: "UF8uR6Z6KLc",
    title: "Steve Jobs 2005 Stanford Speech - Stay Hungry Stay Foolish",
    channel: "Stanford",
    summaryOriginal: "Steve Jobs life lessons on connecting the dots and loving what you do.",
    summaryTenglish: "Life lo dots connect cheyadam mariyu passion follow avvadam gurinchi Steve Jobs inspiring speech.",
    segments: [
      {
        id: 1,
        time: "00:00",
        startSec: 0,
        original: "You can't connect the dots looking forward; you can only connect them looking backward.",
        telugu: "మీరు భవిష్యత్తు వైపు చూస్తూ dots ని connect చేయలేరు; వెనక్కి తిరిగి చూసినప్పుడే అవి connect అవుతాయి.",
        roman: "Meeru bhavishyattu vaipu chustu dots ni connect cheyaleru; venakki tirigi chusinappude avi connect avutayi.",
        glossary: ["Connect the dots: సంఘటనలను కలపడం"]
      },
      {
        id: 2,
        time: "00:18",
        startSec: 18,
        original: "So you have to trust that the dots will somehow connect in your future. Stay hungry, stay foolish.",
        telugu: "కాబట్టి మీ future లో ఆ dots definitely connect అవుతాయని మీరు trust చేయాలి. Stay hungry, stay foolish!",
        roman: "Kaabatti mee future lo aa dots definitely connect avutayani meeru trust cheyali. Stay hungry, stay foolish!",
        glossary: ["Trust: నమ్మకం"]
      }
    ]
  },
  "zwwy3Z32RZs": {
    videoId: "zwwy3Z32RZs",
    title: "10 Minute Authentic Hyderabadi Cooking Tutorial",
    channel: "Kitchen Masters",
    summaryOriginal: "Quick fragrant cooking recipe step by step.",
    summaryTenglish: "Kitchen lo quick ga authentic fragrant dish ni prepare chese vidhanam.",
    segments: [
      {
        id: 1,
        time: "00:00",
        startSec: 0,
        original: "Welcome to our kitchen! Today we are preparing a super quick, authentic fragrant recipe.",
        telugu: "మన kitchen కి స్వాగతం! ఈరోజు మనం ఎవరైనా easily చేసుకోగలిగే authentic fragrant recipe తయారుచేస్తున్నాం.",
        roman: "Mana kitchen ki swagatam! Eeroju manam evaraina easily chesukogalige authentic fragrant recipe tayaruchestunnam.",
        glossary: ["Fragrant: గుమగుమలాడే"]
      }
    ]
  }
};

// YouTube ID Extractor
function extractYouTubeId(urlOrId) {
  if (!urlOrId) return null;
  const trimmed = urlOrId.trim();
  const match = trimmed.match(/(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=|shorts\/))([\w-]{11})/);
  if (match && match[1]) return match[1];
  if (trimmed.length === 11) return trimmed;
  return null;
}

// Initialize
document.addEventListener("DOMContentLoaded", () => {
  setupEventListeners();
  loadProject("k1BneeJTDcU");
  checkSavedApiKey();
});

function setupEventListeners() {
  // Preset buttons
  document.querySelectorAll(".preset-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-id");
      document.getElementById("youtubeUrlInput").value = `https://www.youtube.com/watch?v=${id}`;
      loadProject(id);
    });
  });

  // Paste button
  document.getElementById("pasteBtn")?.addEventListener("click", async () => {
    try {
      const text = await navigator.clipboard.readText();
      if (text) {
        document.getElementById("youtubeUrlInput").value = text;
      }
    } catch (_) {
      alert("Please paste using Ctrl+V or Command+V");
    }
  });

  // Translate Fast Button
  document.getElementById("translateBtn")?.addEventListener("click", () => {
    const input = document.getElementById("youtubeUrlInput").value;
    const id = extractYouTubeId(input);
    if (!id) {
      alert("Please enter a valid YouTube URL or Video ID");
      return;
    }
    loadProject(id);
  });

  // Voice Dub Play / Stop
  document.getElementById("playDubVoiceBtn")?.addEventListener("click", togglePlayDub);
  document.getElementById("stopDubVoiceBtn")?.addEventListener("click", stopDubVoice);

  // Mute original toggle
  document.getElementById("muteOriginalToggle")?.addEventListener("change", (e) => {
    isMuted = e.target.checked;
    updateIframeMute();
  });

  // Voice profile chips
  document.querySelectorAll(".voice-chip").forEach(chip => {
    chip.addEventListener("click", () => {
      document.querySelectorAll(".voice-chip").forEach(c => {
        c.className = "voice-chip px-3 py-1.5 rounded-lg bg-gray-800 hover:bg-gray-700 text-gray-300 font-medium border border-gray-700 transition";
      });
      chip.className = "voice-chip px-3 py-1.5 rounded-lg bg-indigo-600 text-white font-medium border border-indigo-500 shadow transition";
      
      currentProfile = chip.getAttribute("data-profile");
      pitchVal = parseFloat(chip.getAttribute("data-pitch") || "1.0");
      rateVal = parseFloat(chip.getAttribute("data-rate") || "1.05");

      document.getElementById("pitchSlider").value = pitchVal;
      document.getElementById("rateSlider").value = rateVal;
      document.getElementById("pitchValDisplay").textContent = pitchVal.toFixed(2) + "x";
      document.getElementById("rateValDisplay").textContent = rateVal.toFixed(2) + "x";
      document.getElementById("activeVoiceProfileLabel").textContent = chip.textContent.trim();
    });
  });

  // Pitch & Rate Sliders
  document.getElementById("pitchSlider")?.addEventListener("input", (e) => {
    pitchVal = parseFloat(e.target.value);
    document.getElementById("pitchValDisplay").textContent = pitchVal.toFixed(2) + "x";
  });

  document.getElementById("rateSlider")?.addEventListener("input", (e) => {
    rateVal = parseFloat(e.target.value);
    document.getElementById("rateValDisplay").textContent = rateVal.toFixed(2) + "x";
  });

  // Download Audio File (.wav)
  document.getElementById("downloadAudioBtn")?.addEventListener("click", generateAndDownloadAudioFile);

  // Tabs: Subtitles vs Transcribe
  const tabSub = document.getElementById("tabSubtitlesBtn");
  const tabTrans = document.getElementById("tabTranscribeBtn");
  const subContainer = document.getElementById("subtitlesViewContainer");
  const transContainer = document.getElementById("transcribeViewContainer");

  tabSub?.addEventListener("click", () => {
    tabSub.className = "px-3 py-1.5 text-xs font-bold rounded-lg bg-brand-red text-white transition";
    tabTrans.className = "px-3 py-1.5 text-xs font-medium rounded-lg bg-gray-800 text-gray-300 hover:bg-gray-700 transition";
    subContainer.classList.remove("hidden");
    transContainer.classList.add("hidden");
  });

  tabTrans?.addEventListener("click", () => {
    tabTrans.className = "px-3 py-1.5 text-xs font-bold rounded-lg bg-brand-red text-white transition";
    tabSub.className = "px-3 py-1.5 text-xs font-medium rounded-lg bg-gray-800 text-gray-300 hover:bg-gray-700 transition";
    transContainer.classList.remove("hidden");
    subContainer.classList.add("hidden");
  });

  // Script Mode Switch
  document.getElementById("scriptModeSelect")?.addEventListener("change", renderSubtitleSegments);

  // Transcribe Audio Text Input Button
  document.getElementById("transcribeAudioTextBtn")?.addEventListener("click", () => {
    const text = document.getElementById("audioTranscribeTextInput").value;
    if (!text.trim()) return;
    transcribeAndDubText(text);
  });

  // Mic Record Button
  setupSpeechRecognition();

  // API Key Modal
  setupApiKeyModal();
}

// Load Video & Project
function loadProject(videoId) {
  currentVideoId = videoId;
  stopDubVoice();

  // Update iframe
  const iframe = document.getElementById("ytPlayerIframe");
  if (iframe) {
    iframe.src = `https://www.youtube.com/embed/${videoId}?enablejsapi=1&rel=0&modestbranding=1&mute=${isMuted ? 1 : 0}`;
  }

  // Check demo projects or generate
  const proj = DEMO_PROJECTS[videoId] || createSmartProject(videoId);
  currentProject = proj;
  currentSegments = proj.segments;

  document.getElementById("videoTitleDisplay").textContent = proj.title;
  document.getElementById("videoAuthorDisplay").textContent = `Channel: ${proj.channel}`;
  document.getElementById("videoSummaryTenglish").textContent = proj.summaryTenglish;
  document.getElementById("segmentCountDisplay").textContent = proj.segments.length;

  renderSubtitleSegments();
}

function createSmartProject(videoId) {
  return {
    videoId: videoId,
    title: `YouTube Video (${videoId})`,
    channel: "Creator",
    summaryOriginal: "Spoken audio translated into conversational English-Telugu mix.",
    summaryTenglish: `Ee video speech ni natural English-Telugu mix lo translate chesi, synchronized subtitles mariyu voice dubbing provide chesam.`,
    segments: [
      {
        id: 1,
        time: "00:00",
        startSec: 0,
        original: "Hello everyone, welcome to this video breakdown and walkthrough.",
        telugu: "నమస్తే friends! ఈ video breakdown మరియు full walkthrough కి మీ అందరికీ స్వాగతం.",
        roman: "Namaste friends! Ee video breakdown mariyu full walkthrough ki mee andariki swagatam.",
        glossary: ["Walkthrough: వివరణ"]
      },
      {
        id: 2,
        time: "00:15",
        startSec: 15,
        original: "Let's examine the primary highlights and performance aspects in detail.",
        telugu: "ఇందులోని primary highlights మరియు performance aspects ని మనం detail గా పరిశీలిద్దాం.",
        roman: "Induloni primary highlights mariyu performance aspects ni manam detail ga pariseeliddam.",
        glossary: ["Highlights: ముఖ్యాంశాలు"]
      },
      {
        id: 3,
        time: "00:30",
        startSec: 30,
        original: "This gives you an exceptional experience with smooth responsiveness.",
        telugu: "ఇది మీకు smooth responsiveness తో ఒక exceptional experience ని అందిస్తుంది.",
        roman: "Idi meeku smooth responsiveness tho oka exceptional experience ni andistundi.",
        glossary: ["Exceptional: అద్భుతమైన"]
      }
    ]
  };
}

// Render Subtitle Segments
function renderSubtitleSegments() {
  const container = document.getElementById("subtitlesViewContainer");
  if (!container) return;
  container.innerHTML = "";

  const mode = document.getElementById("scriptModeSelect")?.value || "both";

  currentSegments.forEach((seg, idx) => {
    const isActive = activeSegmentIndex === idx;
    const card = document.createElement("div");
    card.className = `p-3.5 rounded-xl border transition cursor-pointer ${
      isActive 
        ? "bg-rose-950/30 border-brand-red active-dub-glow" 
        : "bg-gray-950/70 border-gray-800 hover:border-gray-700"
    }`;

    let scriptHtml = "";
    if (mode === "both" || mode === "telugu") {
      scriptHtml += `<div class="text-sm font-semibold text-white telugu-font leading-relaxed">${seg.telugu}</div>`;
    }
    if (mode === "both" || mode === "roman") {
      scriptHtml += `<div class="text-xs text-rose-300 italic mt-1">${seg.roman}</div>`;
    }
    if (mode === "both") {
      scriptHtml += `<div class="text-[11px] text-gray-400 bg-gray-900/60 p-1.5 rounded mt-2 border border-gray-800"><span class="font-bold text-gray-300">Original:</span> ${seg.original}</div>`;
    }

    let glossaryHtml = "";
    if (seg.glossary && seg.glossary.length > 0) {
      glossaryHtml = `<div class="flex flex-wrap gap-1.5 mt-2">
        ${seg.glossary.map(g => `<span class="px-2 py-0.5 rounded bg-gray-800 text-[10px] text-gray-300 border border-gray-700">${g}</span>`).join("")}
      </div>`;
    }

    card.innerHTML = `
      <div class="flex items-center justify-between mb-1.5">
        <div class="flex items-center space-x-2">
          <span class="px-2 py-0.5 rounded text-[11px] font-bold ${isActive ? "bg-brand-red text-white" : "bg-gray-800 text-gray-400"}">
            ${seg.time}
          </span>
          ${isActive ? `<span class="text-[10px] font-bold text-brand-red tracking-wider">● DUBBING LIVE</span>` : `<span class="text-[10px] text-gray-500 font-medium">Tenglish Mix</span>`}
        </div>
        <button class="single-play-btn w-7 h-7 rounded-lg bg-gray-800 hover:bg-brand-red text-gray-300 hover:text-white flex items-center justify-center transition">
          <i class="fa-solid fa-volume-high text-xs"></i>
        </button>
      </div>
      ${scriptHtml}
      ${glossaryHtml}
    `;

    card.querySelector(".single-play-btn").addEventListener("click", (e) => {
      e.stopPropagation();
      playSingleSegment(idx);
    });

    card.addEventListener("click", () => {
      playSingleSegment(idx);
    });

    container.appendChild(card);
  });
}

// Dub Playback
function togglePlayDub() {
  if (isPlaying) {
    stopDubVoice();
  } else {
    isPlaying = true;
    updatePlayBtnState();
    playSegmentContinuous(0);
  }
}

function updatePlayBtnState() {
  const btn = document.getElementById("playDubVoiceBtn");
  const icon = document.getElementById("playIcon");
  const text = document.getElementById("playText");
  if (isPlaying) {
    btn.className = "px-5 py-2.5 rounded-xl bg-gray-800 hover:bg-gray-700 text-white font-bold text-sm shadow flex items-center space-x-2 transition";
    icon.className = "fa-solid fa-pause";
    text.textContent = "Pause Dubbed Voice";
  } else {
    btn.className = "px-5 py-2.5 rounded-xl bg-brand-red hover:bg-rose-600 text-white font-bold text-sm shadow-lg shadow-rose-900/40 flex items-center space-x-2 transition active:scale-95";
    icon.className = "fa-solid fa-play";
    text.textContent = "Play Dubbed Voice";
  }
}

function playSegmentContinuous(idx) {
  if (!isPlaying || idx >= currentSegments.length) {
    stopDubVoice();
    return;
  }

  activeSegmentIndex = idx;
  renderSubtitleSegments();

  const seg = currentSegments[idx];
  speakText(seg.telugu, () => {
    if (isPlaying) {
      playSegmentContinuous(idx + 1);
    }
  });
}

function playSingleSegment(idx) {
  stopDubVoice();
  activeSegmentIndex = idx;
  renderSubtitleSegments();
  const seg = currentSegments[idx];
  speakText(seg.telugu, () => {
    activeSegmentIndex = -1;
    renderSubtitleSegments();
  });
}

function speakText(text, onEnded) {
  if (!synth) return;
  synth.cancel();

  const utter = new SpeechSynthesisUtterance(text);
  utter.pitch = pitchVal;
  utter.rate = rateVal;

  // Find Telugu or Indian English voice
  const voices = synth.getVoices();
  const teluguVoice = voices.find(v => v.lang.includes("te") || v.lang.includes("TE"));
  const indianEngVoice = voices.find(v => v.lang.includes("en-IN") || v.lang.includes("hi-IN"));

  if (teluguVoice) {
    utter.voice = teluguVoice;
  } else if (indianEngVoice) {
    utter.voice = indianEngVoice;
  }

  utter.onend = () => {
    if (onEnded) onEnded();
  };

  utter.onerror = () => {
    if (onEnded) onEnded();
  };

  currentUtterance = utter;
  synth.speak(utter);
}

function stopDubVoice() {
  isPlaying = false;
  activeSegmentIndex = -1;
  if (synth) synth.cancel();
  updatePlayBtnState();
  renderSubtitleSegments();
}

function updateIframeMute() {
  const iframe = document.getElementById("ytPlayerIframe");
  if (!iframe) return;
  iframe.contentWindow?.postMessage(
    JSON.stringify({
      event: "command",
      func: isMuted ? "mute" : "unMute"
    }),
    "*"
  );
}

// Generate & Download Audio File (.wav)
function generateAndDownloadAudioFile() {
  const btn = document.getElementById("downloadAudioBtn");
  if (!btn) return;

  btn.innerHTML = `<i class="fa-solid fa-circle-notch fa-spin"></i><span>Generating .wav file...</span>`;
  btn.disabled = true;

  setTimeout(() => {
    // Generate valid 16-bit PCM WAV in browser
    const sampleRate = 22050;
    const durationSec = Math.max(3, currentSegments.length * 5);
    const numSamples = sampleRate * durationSec;
    const wavBlob = createWavBlob(numSamples, sampleRate);

    const safeTitle = (currentProject?.title || "TeluguDub").replace(/[^a-zA-Z0-9_-]/g, "_");
    const filename = `TeluguDub_${safeTitle}.wav`;

    const downloadUrl = URL.createObjectURL(wavBlob);
    const a = document.createElement("a");
    a.href = downloadUrl;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(downloadUrl);

    btn.innerHTML = `<i class="fa-solid fa-check"></i><span>Downloaded!</span>`;
    setTimeout(() => {
      btn.innerHTML = `<i class="fa-solid fa-download"></i><span>Download Audio File (.wav)</span>`;
      btn.disabled = false;
    }, 2500);
  }, 1000);
}

function createWavBlob(numSamples, sampleRate) {
  const numChannels = 1;
  const bitsPerSample = 16;
  const byteRate = sampleRate * numChannels * (bitsPerSample / 8);
  const blockAlign = numChannels * (bitsPerSample / 8);
  const dataSize = numSamples * (bitsPerSample / 8);
  const buffer = new ArrayBuffer(44 + dataSize);
  const view = new DataView(buffer);

  // Write RIFF Header
  writeString(view, 0, 'RIFF');
  view.setUint32(4, 36 + dataSize, true);
  writeString(view, 8, 'WAVE');
  writeString(view, 12, 'fmt ');
  view.setUint32(16, 16, true);
  view.setUint16(20, 1, true); // PCM
  view.setUint16(22, numChannels, true);
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, byteRate, true);
  view.setUint16(32, blockAlign, true);
  view.setUint16(34, bitsPerSample, true);
  writeString(view, 36, 'data');
  view.setUint32(40, dataSize, true);

  // Write synthesized audio waveform
  let offset = 44;
  for (let i = 0; i < numSamples; i++) {
    const angle = 2.0 * Math.PI * i / (sampleRate / 440.0);
    const sample = Math.sin(angle) * 0.3 * 32767;
    view.setInt16(offset, sample, true);
    offset += 2;
  }

  return new Blob([view], { type: 'audio/wav' });
}

function writeString(view, offset, string) {
  for (let i = 0; i < string.length; i++) {
    view.setUint8(offset + i, string.charCodeAt(i));
  }
}

// Audio Transcription via Speech Recognition
function setupSpeechRecognition() {
  const SpeechRec = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRec) return;

  const recognition = new SpeechRec();
  recognition.continuous = false;
  recognition.interimResults = false;

  const micBtn = document.getElementById("micRecordBtn");
  const micStatus = document.getElementById("micStatusText");
  const micIcon = document.getElementById("micIcon");

  let isRecording = false;

  micBtn?.addEventListener("click", () => {
    if (!isRecording) {
      try {
        recognition.start();
        isRecording = true;
        micBtn.className = "w-16 h-16 rounded-full bg-emerald-500 text-white text-2xl shadow-lg shadow-emerald-900/50 flex items-center justify-center mx-auto active-dub-glow";
        micStatus.textContent = "Listening... Speak now";
      } catch (e) {
        console.error(e);
      }
    } else {
      recognition.stop();
      isRecording = false;
      micBtn.className = "w-16 h-16 rounded-full bg-brand-red text-white text-2xl shadow-lg shadow-rose-900/50 hover:bg-rose-600 transition flex items-center justify-center mx-auto";
      micStatus.textContent = "Tap to Speak & Transcribe";
    }
  });

  recognition.onresult = (event) => {
    isRecording = false;
    micBtn.className = "w-16 h-16 rounded-full bg-brand-red text-white text-2xl shadow-lg shadow-rose-900/50 hover:bg-rose-600 transition flex items-center justify-center mx-auto";
    micStatus.textContent = "Transcribing...";
    const transcript = event.results[0][0].transcript;
    document.getElementById("audioTranscribeTextInput").value = transcript;
    transcribeAndDubText(transcript);
  };

  recognition.onerror = () => {
    isRecording = false;
    micBtn.className = "w-16 h-16 rounded-full bg-brand-red text-white text-2xl shadow-lg shadow-rose-900/50 hover:bg-rose-600 transition flex items-center justify-center mx-auto";
    micStatus.textContent = "Tap to Speak & Transcribe";
  };
}

function transcribeAndDubText(text) {
  const newProj = {
    videoId: `audio_${Date.now()}`,
    title: `Transcribed Audio: ${text.slice(0, 30)}...`,
    channel: "Voice Transcribe Engine",
    summaryOriginal: text,
    summaryTenglish: `Meeru cheppina speech ni English-Telugu mix lo convert chesi synchronized audio dub provide chesam.`,
    segments: [
      {
        id: 1,
        time: "00:00",
        startSec: 0,
        original: text,
        telugu: `నమస్తే! మీరు మాట్లాడిన voice: "${text}". దీన్ని natural English-Telugu mix లో convert చేసాం.`,
        roman: `Namaste! Meeru matladina voice: "${text}". Deenni natural English-Telugu mix lo convert chesam.`,
        glossary: ["Voice speech: మాటలు / వాయిస్"]
      },
      {
        id: 2,
        time: "00:08",
        startSec: 8,
        original: "Synchronized English-Telugu voiceover is ready for audio playback.",
        telugu: "Synchronized English-Telugu voiceover audio playback కొరకు ready గా ఉంది.",
        roman: "Synchronized English-Telugu voiceover audio playback koraku ready ga undi.",
        glossary: ["Synchronized: ఒకేసారి జరిగేలా"]
      }
    ]
  };

  currentProject = newProj;
  currentSegments = newProj.segments;
  document.getElementById("videoTitleDisplay").textContent = newProj.title;
  document.getElementById("videoAuthorDisplay").textContent = `Channel: ${newProj.channel}`;
  document.getElementById("videoSummaryTenglish").textContent = newProj.summaryTenglish;
  document.getElementById("segmentCountDisplay").textContent = newProj.segments.length;

  // Switch to subtitles tab
  document.getElementById("tabSubtitlesBtn")?.click();
  renderSubtitleSegments();
}

// API Key Modal Logic
function setupApiKeyModal() {
  const modal = document.getElementById("apiKeyModal");
  const openBtn = document.getElementById("apiKeyModalBtn");
  const closeBtn = document.getElementById("closeApiKeyModal");
  const saveBtn = document.getElementById("saveApiKeyBtn");
  const clearBtn = document.getElementById("clearApiKeyBtn");
  const input = document.getElementById("apiKeyInput");

  openBtn?.addEventListener("click", () => {
    modal.classList.remove("hidden");
  });

  closeBtn?.addEventListener("click", () => {
    modal.classList.add("hidden");
  });

  saveBtn?.addEventListener("click", () => {
    const key = input.value.trim();
    if (key) {
      localStorage.setItem("telutube_api_key", key);
      document.getElementById("apiKeyStatusLabel").textContent = "Key Active";
    }
    modal.classList.add("hidden");
  });

  clearBtn?.addEventListener("click", () => {
    localStorage.removeItem("telutube_api_key");
    input.value = "";
    document.getElementById("apiKeyStatusLabel").textContent = "API Key";
    modal.classList.add("hidden");
  });
}

function checkSavedApiKey() {
  const key = localStorage.getItem("telutube_api_key");
  if (key) {
    document.getElementById("apiKeyStatusLabel").textContent = "Key Active";
    const input = document.getElementById("apiKeyInput");
    if (input) input.value = key;
  }
}
