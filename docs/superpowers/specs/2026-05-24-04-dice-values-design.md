# Spec 04 - Dice Values

## Goal
Allow the user to configure dice value ranges.

## Scope
- Configure a default value range for all dice.
- Default range remains 1 to 6.
- New dice start with the current default range.
- Support values from 0 to 100.
- Allow individual dice to override the default range from the settings panel.
- Keep all validation visible and simple.

## User Experience
Most users can set one range for every die. Advanced use allows changing a specific die inside the settings panel without tapping the die on the main screen.

## Technical Design
- Use integer inputs or steppers for minimum and maximum values.
- Validate that minimum is less than or equal to maximum.
- When the default range changes, decide clearly whether it applies to existing dice:
  - Existing dice that have no individual override update with the default.
  - Existing dice with an override keep their custom range.
- Clamp current values to valid ranges after configuration changes.

## Acceptance Criteria
- The user can set default min and max values.
- The app prevents invalid ranges.
- The user can customize an individual die range from settings.
- Rolling respects each die's own range.

## Verification
- Test ranges 1-6, 1-20, 0-100, and per-die mixed ranges.
- Confirm invalid ranges cannot be saved.

