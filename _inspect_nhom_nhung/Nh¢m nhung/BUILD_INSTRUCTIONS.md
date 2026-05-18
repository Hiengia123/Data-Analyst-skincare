# Build Instructions - Java Version Issue

## Problem
Your project is failing to build because you're using **Java 25.0.1**, which is not yet fully supported by:
- Kotlin compiler version 2.1.0
- Gradle Kotlin DSL

## Solutions

### Solution 1: Install and Use Java 17 (Recommended)
1. Download **Java 17 LTS** from: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
   - Or use OpenJDK 17: https://adoptium.net/temurin/releases/?version=17
2. Install it (e.g., to `C:\Program Files\Java\jdk-17`)
3. Set JAVA_HOME environment variable:
   - Open System Properties → Advanced → Environment Variables
   - Add or edit `JAVA_HOME` to point to Java 17 installation (e.g., `C:\Program Files\Java\jdk-17`)
   - Add `%JAVA_HOME%\bin` to your PATH variable
4. Restart your IDE and terminal
5. Run `java -version` to verify it's using Java 17

### Solution 2: Use Android Studio's Embedded JDK
1. In Android Studio, go to: **File → Project Structure → SDK Location**
2. Check the **JDK location** - it should show Android Studio's embedded JDK
3. Set this as your JAVA_HOME or configure Gradle to use it
4. In `gradle.properties`, you can add:
   ```
   org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
   ```
   (Adjust path based on your Android Studio installation)

### Solution 3: Temporary Fix (For Current Session Only)
Run the build using a specific Java version:
```powershell
$env:JAVA_HOME="C:\Path\To\JDK17"
.\gradlew.bat build
```

## Current Status
- ✅ Fixed: Non-ASCII characters in project path (added `android.overridePathCheck=true`)
- ✅ Fixed: Invalid `compileSdk` syntax in build.gradle.kts
- ✅ Fixed: Invalid dependency version (x.x.x → 1.7.6)
- ❌ **Blocked by:** Java 25 incompatibility - requires Java 17 or 21

## After Fixing Java Version
Once you have Java 17 or 21 installed and configured:
```powershell
cd "E:\Nhóm nhung"
.\gradlew.bat --stop
.\gradlew.bat build
```

Your project should build successfully!

