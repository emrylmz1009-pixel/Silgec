# SilGeç (SwipeClean) 🧹📸

> Clean your gallery with intuitive swipe gestures. 100% offline, privacy-first, open-source Android gallery manager.

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Privacy Friendly](https://img.shields.io/badge/Privacy-100%25%20Offline-success.svg)](#privacy)

---

## ✨ Features

- **Tinder-Style Swipe Interface:**
  - Swipe **Right** to KEEP a photo.
  - Swipe **Left** to TRASH / DELETE a photo.
- **Monthly / Category Filtering:** Group your gallery photos by month, screenshots, or all media to review in organized batches.
- **Undo / Review Before Deletion:** Nothing is deleted immediately without your confirmation. Review pending deletions before finalizing.
- **Zero Internet / 100% Private:** Requires NO internet permission. Your photos never leave your device.
- **Fast & Modern:** Built natively with **Jetpack Compose**, **Material 3**, and Kotlin Coroutines.

---

## 🔒 Privacy

- **No Internet Access:** SilGeç does not request ndroid.permission.INTERNET. It cannot connect to any external server or transmit any data.
- **No Analytics / No Trackers:** Zero third-party SDKs, zero telemetry.
- **Local Scoped Storage:** Respects Android modern storage permissions and only reads media with your explicit system permission.

---

## 🛠️ Tech Stack

- **UI:** Jetpack Compose + Material 3
- **Language:** Kotlin
- **Image Loading:** Coil Compose
- **Architecture:** Android Jetpack (ViewModel, StateFlow)
- **Target SDK:** 36 (Android 15/16 ready)
- **Min SDK:** 24 (Android 7.0+)

---

## 📦 Building from Source

`ash
git clone https://github.com/emrylmz1009-pixel/SwipeClean.git
cd SwipeClean
./gradlew assembleRelease
`

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
