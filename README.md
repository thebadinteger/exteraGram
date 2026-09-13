# exteraGram  
(Mostly) deobfuscated, compilable source code of the exteraGram client  

## [@stopextera](https://t.me/stopextera)  
Don't use exteraGram, use [exteraless](https://github.com/exteraless/exteraless)  

## Build  
Quick build:  
```bash
git clone https://github.com/thebadinteger/exteraGram.git
cd exteraGram
./gradlew :TMessagesProj_AppStandalone:assembleAfatDebug
```  
### Requirements  
- **JDK:** Java Development Kit 17
- **Android SDK:**
  - Compile SDK: `35` (or `36`)
  - Target SDK: `36`
  - Min SDK: `24`
  - Build Tools: `35.0.0`  
### Native build config (C++ vs Prebuilt)  
In `gradle.properties`, locate the native build toggle:  
```properties
# Set to 'true' to compile all C/C++ libraries from source using Android NDK and CMake
# Set to 'false' to use precompiled high-performance .so libraries from src/main/jniLibs.
buildNativeFromSource=false
```  
`buildNativeFromSource=true`:  
**Requirements:**
- Android NDK version `27.2.12479018` (or compatible)
- CMake `3.22.1+`
- At least 16 GB RAM and 40 GB free disk space

### Building

**Windows (PowerShell / CMD):**
```powershell
# Universal fat APK:
.\gradlew.bat :TMessagesProj_AppStandalone:assembleAfatDebug
# 64-bit ARM APK:
.\gradlew.bat :TMessagesProj_AppStandalone:assembleArm64Debug
# 32-bit ARM APK:
.\gradlew.bat :TMessagesProj_AppStandalone:assembleArmv7Debug
# Build all variants simultaneously:
.\gradlew.bat :TMessagesProj_AppStandalone:assembleDebug
```

**Linux / macOS:**
```bash
chmod +x gradlew
# Universal fat APK:
./gradlew :TMessagesProj_AppStandalone:assembleAfatDebug
# 64-bit ARM APK:
./gradlew :TMessagesProj_AppStandalone:assembleArm64Debug
# 32-bit ARM APK:
./gradlew :TMessagesProj_AppStandalone:assembleArmv7Debug
# Build all variants simultaneously:
./gradlew :TMessagesProj_AppStandalone:assembleDebug
```

**Output APK files will be generated under:**
`TMessagesProj_AppStandalone/build/outputs/apk/`
- `afat/debug/app-afatDebug.apk` (Universal)
- `arm64/debug/app-arm64Debug.apk` (ARM 64-bit)
- `armv7/debug/app-armv7Debug.apk` (ARM 32-bit)

## Building with Android Studio
In the **Build Variants** panel on the left, select your target variant  
Run or build the project via **Build -> Build Bundle(s) / APK(s) -> Build APK(s)**

## Signing Release APKs

To produce release-signed APKs, configure your `signingConfigs` in `TMessagesProj/build.gradle` or supply keystore parameters via command line flags:

```bash
./gradlew :TMessagesProj_AppStandalone:assembleAfatStandalone \
  -Pandroid.injected.signing.store.file=/path/to/keystore.jks \
  -Pandroid.injected.signing.store.password=YOUR_PASSWORD \
  -Pandroid.injected.signing.key.alias=YOUR_ALIAS \
  -Pandroid.injected.signing.key.password=YOUR_PASSWORD
```

### Official Compilation guide: [DrKLO/Telegram/README.md](https://github.com/DrKLO/Telegram/blob/master/README.md)  

## License
The original Telegram code is published under the [GNU GPLv2](LICENSE) license
