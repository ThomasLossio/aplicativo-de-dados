# Android Setup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a buildable native Android project for the dice roller app using Kotlin, Jetpack Compose, Gradle Wrapper, and Brazilian Portuguese visible UI text.

**Architecture:** Start with a single `app` module. Keep UI code small and Compose-based, with the entry screen in `MainActivity`; later specs can extract state and domain logic as behavior grows. Keep all visible Android UI strings in `app/src/main/res/values/strings.xml` in Brazilian Portuguese.

**Tech Stack:** Kotlin, Jetpack Compose, Android Gradle Plugin 9.2.0, Gradle 9.4.1 Wrapper, compile SDK 36.1, min SDK 24, JUnit for baseline unit test.

---

## Tooling Notes

- Official Android Gradle Plugin 9.2.0 release notes state compatibility with Gradle 9.4.1, API level 36.1, SDK Build Tools 36.0.0, and JDK 17: https://developer.android.com/build/releases/gradle-plugin
- Official Compose BOM docs recommend using the BOM so Compose library versions stay compatible: https://developer.android.com/develop/ui/compose/bom
- This machine already has Java 17, Android Studio, Android SDK, `platform-tools`, `emulator`, `platforms/android-36`, and `platforms/android-36.1`.
- `gradle` is not installed globally, so the project must commit Gradle Wrapper files and use `.\gradlew.bat`.

## File Structure

- Create `settings.gradle.kts`: Gradle plugin management and root project module inclusion.
- Create `build.gradle.kts`: root plugin aliases without applying them globally.
- Create `gradle/libs.versions.toml`: central versions for Android, Kotlin, Compose, and test dependencies.
- Create `gradle/wrapper/gradle-wrapper.properties`: Wrapper distribution configuration for Gradle 9.4.1.
- Create `gradlew` and `gradlew.bat`: Gradle Wrapper launch scripts.
- Create `gradle/wrapper/gradle-wrapper.jar`: Gradle Wrapper binary. Generate or download through Gradle's official distribution rather than hand-editing.
- Create `app/build.gradle.kts`: Android application module configuration.
- Create `app/src/main/AndroidManifest.xml`: application manifest and launcher activity.
- Create `app/src/main/java/br/com/thomas/dados/MainActivity.kt`: Compose entry point.
- Create `app/src/main/res/values/strings.xml`: Brazilian Portuguese app name and visible text.
- Create `app/src/main/res/values/colors.xml`: basic color resources for launcher/theme compatibility.
- Create `app/src/main/res/values/themes.xml`: app theme.
- Create `app/src/test/java/br/com/thomas/dados/ProjectSanityTest.kt`: baseline JVM test.
- Modify `README.md`: setup, requirements, and first run instructions in Portuguese.
- Modify `.gitignore`: add Android build and local machine files if missing.

---

### Task 1: Create the Gradle Project Skeleton

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle/libs.versions.toml`
- Modify: `.gitignore`

- [ ] **Step 1: Create `settings.gradle.kts`**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AplicativoDeDados"
include(":app")
```

- [ ] **Step 2: Create root `build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
```

- [ ] **Step 3: Create `gradle/libs.versions.toml`**

```toml
[versions]
agp = "9.2.0"
kotlin = "2.3.20"
compileSdk = "36"
minSdk = "24"
targetSdk = "36"
composeBom = "2026.05.00"
androidxActivity = "1.12.0"
androidxCore = "1.18.0"
androidxLifecycle = "2.10.0"
junit = "4.13.2"

[libraries]
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "androidxActivity" }
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "androidxCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "androidxLifecycle" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
junit = { group = "junit", name = "junit", version.ref = "junit" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

- [ ] **Step 4: Update `.gitignore` if needed**

Ensure these lines are present:

```gitignore
.gradle/
.idea/
build/
local.properties
*.iml
.superpowers/
```

- [ ] **Step 5: Commit the skeleton**

Run:

```powershell
git add settings.gradle.kts build.gradle.kts gradle/libs.versions.toml .gitignore
git commit -m "build: add Android Gradle skeleton"
```

Expected: a commit containing only Gradle skeleton files and `.gitignore` changes.

---

### Task 2: Add Gradle Wrapper

**Files:**
- Create: `gradlew`
- Create: `gradlew.bat`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `gradle/wrapper/gradle-wrapper.jar`

- [ ] **Step 1: Create `gradle/wrapper/gradle-wrapper.properties`**

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-9.4.1-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

- [ ] **Step 2: Add wrapper scripts and wrapper jar**

Use one of these implementation methods:

```powershell
# Preferred when a Gradle executable is available through Android Studio or a temporary download:
gradle wrapper --gradle-version 9.4.1 --distribution-type bin
```

If `gradle` is still unavailable, download the official Gradle 9.4.1 distribution, extract it to a temporary folder outside the repo, run its `bin\gradle.bat wrapper --gradle-version 9.4.1 --distribution-type bin`, then delete the temporary extracted distribution.

- [ ] **Step 3: Verify wrapper starts**

Run:

```powershell
.\gradlew.bat --version
```

Expected: output includes `Gradle 9.4.1`.

- [ ] **Step 4: Commit wrapper files**

Run:

```powershell
git add gradlew gradlew.bat gradle/wrapper
git commit -m "build: add Gradle wrapper"
```

Expected: a commit containing wrapper scripts, wrapper properties, and wrapper jar.

---

### Task 3: Add the Android App Module

**Files:**
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/values/strings.xml`
- Create: `app/src/main/res/values/colors.xml`
- Create: `app/src/main/res/values/themes.xml`

- [ ] **Step 1: Create `app/build.gradle.kts`**

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "br.com.thomas.dados"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "br.com.thomas.dados"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
}
```

- [ ] **Step 2: Create `app/src/main/AndroidManifest.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AplicativoDeDados">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 3: Create `app/src/main/res/values/strings.xml`**

```xml
<resources>
    <string name="app_name">Dados</string>
    <string name="initial_screen_title">Dados</string>
    <string name="initial_screen_subtitle">Preparando o rolador de dados.</string>
</resources>
```

- [ ] **Step 4: Create `app/src/main/res/values/colors.xml`**

```xml
<resources>
    <color name="seed">#39A86B</color>
</resources>
```

- [ ] **Step 5: Create `app/src/main/res/values/themes.xml`**

```xml
<resources>
    <style name="Theme.AplicativoDeDados" parent="android:style/Theme.Material.Light.NoActionBar">
        <item name="android:fontFamily">sans</item>
        <item name="android:windowLightStatusBar">true</item>
        <item name="android:navigationBarColor">#39A86B</item>
        <item name="android:statusBarColor">#39A86B</item>
    </style>
</resources>
```

- [ ] **Step 6: Create empty ProGuard rules file**

Create `app/proguard-rules.pro` with:

```proguard
# Project-specific ProGuard rules will be added when release hardening is needed.
```

---

### Task 4: Add Compose Entry Screen

**Files:**
- Create: `app/src/main/java/br/com/thomas/dados/MainActivity.kt`
- Create: `app/src/test/java/br/com/thomas/dados/ProjectSanityTest.kt`

- [ ] **Step 1: Write the failing JVM sanity test**

Create `app/src/test/java/br/com/thomas/dados/ProjectSanityTest.kt`:

```kotlin
package br.com.thomas.dados

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectSanityTest {
    @Test
    fun appStartsWithPortugueseName() {
        assertEquals("Dados", AppText.appName)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests br.com.thomas.dados.ProjectSanityTest.appStartsWithPortugueseName
```

Expected: FAIL because `AppText` does not exist.

- [ ] **Step 3: Create `MainActivity.kt` with minimal app text holder and Compose UI**

```kotlin
package br.com.thomas.dados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AppText {
    const val appName = "Dados"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DadosApp()
        }
    }
}

@Composable
fun DadosApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            InitialScreen()
        }
    }
}

@Composable
private fun InitialScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF39A86B))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.initial_screen_title),
            color = Color.White,
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(id = R.string.initial_screen_subtitle),
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 18.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InitialScreenPreview() {
    DadosApp()
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests br.com.thomas.dados.ProjectSanityTest.appStartsWithPortugueseName
```

Expected: PASS.

- [ ] **Step 5: Run app build**

Run:

```powershell
.\gradlew.bat :app:assembleDebug
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Commit app module**

Run:

```powershell
git add app
git commit -m "feat: add initial Android app shell"
```

Expected: a commit containing the app module, entry activity, resources, and baseline test.

---

### Task 5: Add README Setup Instructions

**Files:**
- Create or modify: `README.md`

- [ ] **Step 1: Create `README.md` in Portuguese**

```markdown
# Dados

Aplicativo Android nativo para rolar dados, feito em Kotlin com Jetpack Compose.

## Objetivo

Criar um rolador de dados simples, sem anúncios e sem rastreamento, para uso pessoal e de pessoas próximas. A interface do aplicativo é somente em português do Brasil.

## Requisitos

- Android Studio instalado.
- JDK 17.
- Android SDK com plataforma Android 36 ou 36.1.
- Emulador Android ou aparelho físico com depuração USB.

Nesta máquina, o SDK Android foi encontrado em:

```text
C:\Users\thoma\AppData\Local\Android\Sdk
```

Se `adb` e `emulator` não estiverem no `PATH`, use os executáveis diretamente dentro do SDK ou configure o Android Studio para abrir e rodar o app.

## Como compilar

```powershell
.\gradlew.bat :app:assembleDebug
```

## Como rodar testes automatizados

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

## Como abrir no Android Studio

1. Abra o Android Studio.
2. Escolha `Open`.
3. Selecione esta pasta do projeto.
4. Aguarde a sincronização do Gradle.
5. Escolha um emulador ou aparelho físico.
6. Clique em `Run`.

## Specs do projeto

As decisões e etapas de implementação ficam em `docs/superpowers/specs/`.
```

- [ ] **Step 2: Run verification commands**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

Expected: both commands finish with BUILD SUCCESSFUL.

- [ ] **Step 3: Commit README**

Run:

```powershell
git add README.md
git commit -m "docs: add Android setup instructions"
```

Expected: a commit containing only `README.md`.

---

### Task 6: Final Quality Pass for Spec 01

**Files:**
- Read: `docs/superpowers/specs/2026-05-24-01-android-setup-design.md`
- Read: `AGENTS.md`
- Inspect: all files changed since the commit before this plan execution.

- [ ] **Step 1: Run full verification**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
git status --short
```

Expected:
- Unit tests finish with BUILD SUCCESSFUL.
- Debug APK build finishes with BUILD SUCCESSFUL.
- `git status --short` has no unstaged implementation changes after commits.

- [ ] **Step 2: Review Spec 01 acceptance criteria**

Confirm each item:

```text
Project opens in Android Studio: structure contains settings.gradle.kts and app module.
Builds through Gradle Wrapper: .\gradlew.bat :app:assembleDebug succeeded.
Launches on emulator/device: attempt if an emulator or physical device is available; otherwise document that Android Studio/device testing is still manual.
README explains required tools and how to run the app: README.md contains requirements, build, tests, and Android Studio instructions.
Any visible initial screen text is in Brazilian Portuguese: strings.xml and MainActivity text are pt-BR.
```

- [ ] **Step 3: Request or perform code review**

Review focus:

```text
Check Gradle versions, SDK configuration, package naming, pt-BR visible strings, README accuracy, and whether files are scoped to Spec 01 only.
```

- [ ] **Step 4: Fix review findings before moving to Spec 02**

If a review finding requires changes, make the smallest fix, run:

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

Then commit:

```powershell
git add .
git commit -m "chore: address Android setup review"
```

Expected: no Important or Critical review findings remain.

---

## Self-Review

- Spec coverage: this plan creates a Kotlin + Compose Android project, Gradle Wrapper, minimal theme, initial pt-BR screen, README, build verification, and Android-focused ignore behavior.
- Red-flag scan: no incomplete or unspecified implementation steps remain.
- Type consistency: `AppText`, `DadosApp`, `InitialScreen`, package `br.com.thomas.dados`, and resource names are consistent across test, activity, manifest, and strings.
