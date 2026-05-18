# 📱 Build & Install Guide - Cosmetic App

## Status: Building Now ⚙️

**Date:** December 27, 2025  
**Project:** Cosmetic E-commerce App (project261)

---

## ✅ Step-by-Step Build & Install Instructions

### **Step 1: Clean Build** ✅ IN PROGRESS

**Command:**
```bash
cd "E:\Nhóm nhung"
./gradlew clean
```

**What this does:**
- Removes all previous build files
- Clears cached dependencies
- Ensures a fresh build
- Takes: ~10-30 seconds

**Expected output:**
```
BUILD SUCCESSFUL in 15s
1 actionable task: 1 executed
```

---

### **Step 2: Rebuild Debug APK** ✅ IN PROGRESS

**Command:**
```bash
./gradlew assembleDebug
```

**What this does:**
- Compiles all Kotlin code
- Processes resources
- Packages everything into an APK
- Takes: ~1-3 minutes (first time may be longer)

**Expected output:**
```
BUILD SUCCESSFUL in 1m 30s
XX actionable tasks: XX executed
```

**APK Location:**
The built APK will be at:
```
E:\Nhóm nhung\app\build\outputs\apk\debug\app-debug.apk
```

---

### **Step 3: Install on Device** 🔜 NEXT

You have **3 options** to install:

---

## 📲 **OPTION 1: Install via ADB (Recommended)**

### Prerequisites:
- USB Debugging enabled on your phone
- Phone connected via USB cable
- ADB drivers installed

### Steps:

**1. Enable USB Debugging on Phone:**
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times to enable Developer Options
   - Go to Settings → Developer Options
   - Enable "USB Debugging"

**2. Connect Phone via USB**
   - Plug phone into computer
   - Allow USB debugging when prompted on phone

**3. Verify Connection:**
```bash
adb devices
```
Expected output:
```
List of devices attached
XXXXXXXX    device
```

**4. Install APK:**
```bash
cd "E:\Nhóm nhung"
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

The `-r` flag reinstalls the app if it already exists.

**Expected output:**
```
Performing Streamed Install
Success
```

---

## 📲 **OPTION 2: Install via Android Studio**

### Steps:

1. **Open Project in Android Studio**
   - File → Open → Select `E:\Nhóm nhung`

2. **Select Device**
   - Click device dropdown at top
   - Select your connected phone or emulator

3. **Run App**
   - Click the green "Run" button (▶️)
   - Or press Shift + F10

4. **Wait for Installation**
   - Android Studio will build and install automatically
   - App will launch on your device

---

## 📲 **OPTION 3: Manual APK Transfer**

### Steps:

1. **Locate the APK:**
   ```
   E:\Nhóm nhung\app\build\outputs\apk\debug\app-debug.apk
   ```

2. **Transfer to Phone:**
   - **Via USB:** Copy APK to phone's Download folder
   - **Via Email:** Email APK to yourself and download on phone
   - **Via Cloud:** Upload to Google Drive/Dropbox and download on phone

3. **Install on Phone:**
   - Open File Manager on phone
   - Navigate to Download folder
   - Tap on `app-debug.apk`
   - Allow "Install from Unknown Sources" if prompted
   - Tap "Install"

---

## 🎯 After Installation

### **1. Launch the App**
- Find "Project261" app icon
- Tap to open

### **2. Test Loading**
- Wait 2-3 seconds
- Categories should appear (Dior, Chanel, M.A.C, Rare Beauty)
- Products should load below

### **3. Verify Features**
- [ ] Categories load quickly (1-2 seconds)
- [ ] Category images display
- [ ] Products load (2-3 seconds)
- [ ] Product images display
- [ ] Prices show in VND (đồng)
- [ ] Click category → Shows filtered products
- [ ] Click product → Shows details
- [ ] Add to cart → Works

---

## 🔧 Quick Commands Reference

### Clean Build:
```bash
cd "E:\Nhóm nhung"
./gradlew clean
```

### Build Debug APK:
```bash
./gradlew assembleDebug
```

### Build Release APK (for production):
```bash
./gradlew assembleRelease
```

### Install via ADB:
```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Uninstall from device:
```bash
adb uninstall com.uilover.project261
```

### Check connected devices:
```bash
adb devices
```

### View app logs:
```bash
adb logcat | findstr "project261"
```

---

## 🐛 Troubleshooting

### Build Fails:

**Error: "SDK location not found"**
```bash
# Create local.properties file
echo sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk > local.properties
```

**Error: "Gradle sync failed"**
- Open Android Studio
- File → Sync Project with Gradle Files
- Wait for sync to complete

**Error: "Java version incompatible"**
- Ensure Java 11 is installed
- Set JAVA_HOME environment variable

---

### Installation Fails:

**Error: "INSTALL_FAILED_UPDATE_INCOMPATIBLE"**
```bash
# Uninstall old version first
adb uninstall com.uilover.project261
# Then install again
adb install app\build\outputs\apk\debug\app-debug.apk
```

**Error: "device unauthorized"**
- Check phone for USB debugging authorization dialog
- Tap "Allow"
- Run `adb devices` again

**Error: "no devices/emulators found"**
- Check USB cable connection
- Enable USB Debugging on phone
- Install phone's USB drivers on PC

---

### App Doesn't Load Data:

**Categories/Products not loading:**
1. Check internet connection on phone
2. Verify Firebase Database URL in code
3. Check Firebase Console → Database Rules
4. Check AndroidManifest has INTERNET permission

**Check logs:**
```bash
adb logcat -s Firebase
```

---

## 📱 Build Variants

### Debug Build (Current):
- **Purpose:** Development and testing
- **Features:** 
  - Debugging enabled
  - Larger APK size
  - Not optimized
  - Can inspect code
- **Command:** `./gradlew assembleDebug`
- **Output:** `app-debug.apk`

### Release Build:
- **Purpose:** Production deployment
- **Features:**
  - Optimized and minified
  - Smaller APK size
  - Requires signing key
  - ProGuard enabled
- **Command:** `./gradlew assembleRelease`
- **Output:** `app-release-unsigned.apk`

---

## 📊 Build Performance

### Expected Build Times:

| Task | First Build | Subsequent Builds |
|------|-------------|-------------------|
| Clean | 10-20 sec | 10-20 sec |
| Build Debug | 2-4 min | 30-60 sec |
| Build Release | 3-5 min | 1-2 min |
| Install via ADB | 5-10 sec | 5-10 sec |

### Tips for Faster Builds:
- Use Gradle daemon (enabled by default)
- Enable build cache in `gradle.properties`
- Use incremental builds (don't clean unless necessary)
- Increase Gradle memory in `gradle.properties`:
  ```
  org.gradle.jvmargs=-Xmx2048m
  ```

---

## ✅ Checklist

### Before Building:
- [ ] Fixed Firebase Database URL in MainRepository.kt
- [ ] Added INTERNET permissions to AndroidManifest.xml
- [ ] Updated google-services.json
- [ ] Verified Firebase Database has data

### During Building:
- [ ] Run `./gradlew clean`
- [ ] Run `./gradlew assembleDebug`
- [ ] Wait for "BUILD SUCCESSFUL"
- [ ] Locate APK in `app/build/outputs/apk/debug/`

### Installing:
- [ ] Phone connected via USB (if using ADB)
- [ ] USB Debugging enabled
- [ ] Run `adb devices` to verify connection
- [ ] Run `adb install -r app-debug.apk`
- [ ] Or transfer APK and install manually

### Testing:
- [ ] App launches successfully
- [ ] Categories load within 2-3 seconds
- [ ] Products load within 2-3 seconds
- [ ] Images display correctly
- [ ] Navigation works
- [ ] Cart functionality works

---

## 🚀 Quick Start (Copy & Paste)

```bash
# Navigate to project
cd "E:\Nhóm nhung"

# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r app\build\outputs\apk\debug\app-debug.apk

# Done! App should be installed on your phone
```

---

## 📞 Need Help?

If you encounter issues:

1. Check the Troubleshooting section above
2. Verify all fixes were applied (Firebase URL, permissions)
3. Check Firebase Console for database data
4. View app logs: `adb logcat`

---

**Status:** Build commands executed! ✅  
**Next:** Wait for build to complete, then install on device

Good luck! 🎉

