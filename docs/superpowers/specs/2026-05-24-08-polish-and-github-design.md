# Spec 08 - Polish and GitHub

## Goal
Prepare the project for practical use and public sharing.

## Scope
- Add the show-total option to the settings panel.
- Display total when enabled.
- Improve README with screenshots or usage notes if available.
- Add build and run instructions.
- Ensure the README documents that the app UI is Brazilian Portuguese only.
- Ensure the app has no ads and no tracking.
- Create or configure a public GitHub repository.
- Push the completed work.

## User Experience
The finished app should be easy to install locally, understand, and share. The total display is optional and should not clutter the main dice view when disabled.

## Technical Design
- Total is derived from current dice values.
- Keep total display unobtrusive, likely as a small overlay near the top.
- Repository setup happens after local project is stable.

## Acceptance Criteria
- Show-total setting exists.
- Total appears only when enabled.
- README contains setup, run, and testing instructions.
- README states the app is intended for Brazilian Portuguese users and does not include multi-language support.
- GitHub repository contains the final project files.

## Verification
- Build passes.
- Manual app smoke test passes.
- Confirm GitHub remote and push.
