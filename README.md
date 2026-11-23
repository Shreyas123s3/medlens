# MedLens

MedLens is an offline-first Android starter app for browsing a small medicine catalog and organizing symptom information. It is built with Java, XML resources, Room, Material Components, and a local rule-based assistant.

## Get the APK without Android Studio

The GitHub Actions workflow installs JDK 17, Gradle 8.9, and the Android 34 SDK in the cloud, then runs `gradle assembleDebug` on every push or manual start. No Gradle or Android tooling needs to be installed on your computer.

1. Open the repository's **Actions** tab.
2. Open the latest **Build MedLens debug APK** run and wait for it to finish successfully.
3. In **Artifacts**, download **MedLens-debug-apk**.
4. Unzip the downloaded artifact. It contains `app-debug.apk`.
5. Send `app-debug.apk` to your phone using USB, Drive, or another file transfer method, then tap it to install. If Android asks, allow your file app to install unknown apps.

The artifact is created by GitHub after the repository is pushed and the workflow completes. This offline development machine has no Android SDK and cannot currently download the SDK, so an APK cannot be produced locally here.

## Build

The project targets Android 14 (API 34), supports Android 7.0 (API 24) and newer, and uses JDK 17. For a local build, install Gradle 8.9, JDK 17, and the Android 34 SDK first, then run:

```sh
./gradlew assembleDebug
```

The resulting file is `app/build/outputs/apk/debug/app-debug.apk`. The GitHub Actions build uses the Gradle installation provided by `gradle/actions/setup-gradle`; it does not depend on the wrapper scripts or a local Gradle installation.

For command-line setup, install Temurin JDK 17, Android command-line tools, platform-tools, `platforms;android-34`, and `build-tools;34.0.0`. Accept Android SDK licenses. Set `ANDROID_HOME` to the SDK directory and set `sdk.dir` to the same absolute path in the untracked `local.properties` file.

## Medicine content review

The small seed catalog currently names paracetamol and ibuprofen. Their detailed clinical fields are deliberately `Information unavailable`; the source field refers only to the WHO Model List of Essential Medicines (2025) name listing. The data needs review by a pharmacist or doctor before real-world use. No dosage information is included.

## Safety and current scope

- A local emergency phrase detector runs before the assistant's offline follow-up flow and shows India emergency number 112.
- The assistant is clearly marked offline, asks follow-up questions, and does not diagnose or recommend a medicine.
- Medicine records and bookmarks are stored locally in Room. The app does not send symptom text to a server.
- The remote service class is only a networking configuration seam; a deployable backend and live AI endpoint are not included yet.
- Chat history is stored locally on this device. There are no local accounts or user passwords.

## Backend

Remote AI is not enabled in this build. Before adding it, implement and deploy a backend proxy that stores provider secrets server-side, applies safety rules and rate limits, returns validated structured responses, and obtains user consent before any symptom text leaves the device. Never put provider API keys in the Android app.
