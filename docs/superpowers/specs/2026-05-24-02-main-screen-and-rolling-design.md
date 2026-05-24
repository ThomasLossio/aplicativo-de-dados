# Spec 02 - Main Screen and Rolling

## Goal
Implement the first usable dice roller experience.

## Scope
- App opens with one standard die.
- Default die range is 1 to 6.
- One die fills most of the screen.
- Add bottom floating controls in this order: roll, minus, plus, settings.
- Plus adds one die at a time.
- Minus removes one die at a time.
- Maximum number of dice is 40.
- Rolling generates random values for all visible dice.
- Multiple dice are displayed in a responsive grid.

## User Experience
On first launch the user sees one large die using classic pip dots. They can roll immediately, add dice with `+`, remove dice with `-`, and open settings from the gear button.

## Technical Design
- Model each die as an object with an id, range, current value, and visual configuration inherited from defaults.
- Keep initial state in memory.
- Use Kotlin random generation constrained by each die range.
- The grid should adapt to screen size and dice count.

## Acceptance Criteria
- The app starts with exactly one die.
- The die range defaults to 1-6.
- The roll button updates visible dice values.
- `+` cannot exceed 40 dice.
- `-` cannot go below 1 die.
- With one die, it visually occupies most of the screen.

## Verification
- Manual test with 1, 2, 6, 20, and 40 dice.
- Confirm dice values stay within their configured range.

