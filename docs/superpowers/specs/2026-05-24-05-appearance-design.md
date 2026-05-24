# Spec 05 - Appearance

## Goal
Implement dice appearance options.

## Scope
- Support classic pip display for values 1 to 6.
- Support number display for values above 6.
- Allow the user to choose display mode:
  - Automatic: pips for 1-6 and numbers for 7+
  - Numbers only
- Add simple color options:
  - Multicolor dice
  - Single color dice
- Keep image and sticker customization out of scope.

## User Experience
The app feels like a physical dice roller by default. Values above 6 use numbers so the screen stays readable and does not become visually crowded.

## Technical Design
- Create a reusable die face composable.
- Pip layouts for 1, 2, 3, 4, 5, and 6 must match a standard physical die.
- The value 4 uses four corner pips.
- Automatic mode switches to number rendering for values greater than 6.
- Color palette is fixed in version 1.

## Acceptance Criteria
- Values 1-6 render with correct classic pip patterns.
- Values above 6 render as numbers in automatic mode.
- Numbers-only mode renders every value as a number.
- User can switch between multicolor and single color.

## Verification
- Visual check values 1 through 6.
- Roll ranges above 6 and confirm numbers render legibly.

