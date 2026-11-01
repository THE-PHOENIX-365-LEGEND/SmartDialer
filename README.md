<div align="center">

<img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120" alt="App icon"/>

# Smart Dialer

### Open-Source Dialer & Contacts Manager for Android

Smart Dialer is a modern, privacy-focused open-source dialer and contact manager. Built with modern Android development standards, it combines robust telephony features with a stunning UI featuring real-time blur and "Liquid Glass" visual effects.

</div>

---

## 🌟 Key Features

- **🔒 Privacy First:** 100% open-source, zero trackers, zero ads, and strictly necessary permissions only.
- **✨ Advanced UI & Aesthetics:** Real-time blur effects, Liquid Glass styling, and expressive Material 3 UI design.
- **📞 Offline Caller ID (CNAP):** Identifies callers locally using CNAP protocol support (carrier dependent).
- **🛡️ Secure Private Contacts:** Create hidden, encrypted private contacts protected by biometrics or a custom PIN/password.
- **🎨 Deep Customization:**
  - Adjustable real-time blur intensity
  - Dynamic & custom color themes
  - Custom typography and app icon selector
  - Reorderable navigation tabs & customizable swipe actions
- **📇 All-in-One Management:** Full-featured call log, dialer with T9 search, contact management, SIM card picker, call blocking, and call analytics.

---

## 🛠 Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose (Material 3 Expressive)
- **Dependency Injection:** Koin
- **Local Storage:** Room Database & DataStore Preferences
- **Navigation:** Jetpack Navigation & Compose Destinations
- **Image Loading:** Coil
- **Code Generation:** KSP (Kotlin Symbol Processing)
- **Flavors:** `gplay`, `phoenix`, `rustore`

---

## 🚀 Building & Running

### Prerequisites
- **Android Studio:** Ladybug (2024.2) or newer recommended
- **JDK:** Java 17
- **Min SDK:** 29 (Android 10)
- **Target SDK:** 37

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/Phoenix365/SmartDialer.git
cd SmartDialer

# Build Debug APK (e.g. Phoenix flavor)
./gradlew assemblePhoenixDebug
```

---

## 🤝 Contributing

Contributions, bug reports, and feature requests are welcome! Feel free to check the [issues page](https://github.com/Phoenix365/SmartDialer/issues) or submit a pull request.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git checkout -b feature/AmazingFeature`)
5. Open a Pull Request

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

