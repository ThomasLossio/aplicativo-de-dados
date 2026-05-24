# Spec 06 - Animation and Interaction

## Goal
Make rolling feel responsive and tactile.

## Scope
- Add rolling animation that cycles through temporary values before settling.
- Add animation speed options: slow, normal, fast.
- Add tap-to-roll option.
- Keep the roll button available even when tap-to-roll is enabled.

## User Experience
When the user rolls, dice should visibly change through several values before stopping. The animation should feel playful but not delay gameplay too much.

## Technical Design
- Use Compose animation and coroutines.
- During animation, temporarily disable overlapping roll actions or queue only the latest roll request.
- Speed setting controls animation duration and number of intermediate updates.
- Tap-to-roll should ignore taps on controls and settings.

## Acceptance Criteria
- Rolling shows intermediate values before final results.
- Speed changes visibly affect the animation.
- Tapping the dice area rolls when enabled.
- Tapping controls does not accidentally roll.

## Verification
- Manual test all speed modes.
- Manual test repeated rapid taps and button presses.

