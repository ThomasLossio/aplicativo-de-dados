# Spec 01 - Android Setup

## Goal
Create the native Android project foundation for a simple dice roller app with no ads. The app will target Android only and use Kotlin with Jetpack Compose.

## Scope
- Create a new Android project structure.
- Use Kotlin, Jetpack Compose, and Gradle Wrapper.
- Add a minimal app theme and initial entry screen.
- Set the app's visible UI language expectation to Brazilian Portuguese only.
- Add project README with local setup notes.
- Keep the repository clean with Android-focused `.gitignore` entries.

## User Experience
This spec does not implement the dice experience yet. Its purpose is to make the project buildable and ready for incremental feature specs.

## Technical Design
- Single Android application module is enough for version 1.
- Use Compose for UI.
- Use a simple state-holder structure first; add ViewModel once app state grows in later specs.
- User-facing strings should be written in Brazilian Portuguese. The app does not need multi-language support or locale switching in version 1.
- Keep dependencies minimal until a later spec needs sensors, audio, or persistence.

## Acceptance Criteria
- The project opens in Android Studio.
- The app builds through Gradle Wrapper.
- The app launches on an emulator or physical Android device.
- Any visible placeholder or initial screen text is in Brazilian Portuguese.
- README explains required tools and how to run the app.

## Verification
- Run the Gradle build task.
- Run the app on an Android emulator when available.
