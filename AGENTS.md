# Project Guide for Agents

## Project Summary
This repository is for a native Android dice roller app. The app targets Android only and uses Kotlin with Jetpack Compose. The goal is a simple, ad-free dice roller inspired by the reference app discussed with the user, while keeping our own implementation and visual identity.

## Product Decisions
- The app opens with one standard die.
- Default range is 1 to 6.
- Supported dice values are 1 to 100.
- Maximum visible dice count is 40.
- With one die, the die should occupy most of the screen.
- Bottom floating controls are ordered as roll, minus, plus, settings.
- Settings open from the gear button as a bottom panel with expandable sections.
- All dice start with the current default configuration.
- Individual dice customization happens in settings, not by tapping dice on the main screen.
- Classic pips are used for values 1 through 6.
- Values above 6 use numbers in the initial automatic display mode.
- Image and sticker customization is out of scope.
- The app must have no ads and no tracking.
- The app is for the user and nearby Brazilian users, so every visible app string must be in Brazilian Portuguese.
- Do not add multi-language support or locale switching unless the user explicitly asks later.
- Internal docs, code identifiers, and commit messages may use English, but the Android UI must not expose English labels to users.

## Specs
Implementation is split into specs in `docs/superpowers/specs/`:
- Spec 01: Android setup
- Spec 02: Main screen and rolling
- Spec 03: Settings panel
- Spec 04: Dice values
- Spec 05: Appearance
- Spec 06: Animation and interaction
- Spec 07: Sensor, sound, and vibration
- Spec 08: Polish and GitHub

Follow the specs in order unless the user explicitly changes priority.

## Development Workflow
- Prefer small commits tied to one spec or one clear behavior.
- Use test-driven development where practical: write a failing test, verify it fails, implement the smallest change, then verify it passes.
- Add automated tests for non-visual behavior such as dice count limits, random range validation, per-die range handling, total calculation, settings state, and roll triggering rules.
- Manual visual testing is expected for layout, animation feel, sensor behavior, sound, and vibration.
- Before committing implementation work, run formatting, relevant tests, and a code review or quality pass.
- If subagents are available, use them for independent review, implementation checks, or research when it reduces risk.

## Local Environment Notes
- Java 17 is installed on the user's machine.
- Android Studio is installed.
- Android SDK exists under the user's local Android directory.
- Gradle should be provided by the project through Gradle Wrapper.
- Emulator and SDK command-line tools may need PATH configuration or Android Studio setup when implementation reaches device testing.

## Style and Architecture
- Keep the first version as a single Android app module unless complexity clearly justifies modularization.
- Use Jetpack Compose for UI.
- Keep app state simple at first; introduce ViewModel and clearer state holders as behavior grows.
- Prefer clear domain functions for testable logic instead of burying behavior directly in composables.
- Keep all user-facing app text in Brazilian Portuguese (`pt-BR`). Do not use English labels in the Android UI.
