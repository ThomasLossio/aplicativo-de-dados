# Spec 07 - Sensor, Sound, and Vibration

## Goal
Add device feedback and shake-to-roll.

## Scope
- Add shake-to-roll setting.
- Add simple dice roll sound setting.
- Add vibration setting.
- Trigger sound and vibration when a roll starts or completes.
- Avoid excessive vibration during long animations unless explicitly enabled later.

## User Experience
The app can be rolled by physically shaking the phone. Sound and vibration make the roll feel more physical, but all feedback can be disabled.

## Technical Design
- Use Android sensor APIs for accelerometer-based shake detection.
- Debounce shake events so one shake triggers one roll.
- Use Android vibration APIs with compatibility handling.
- Use a bundled short audio asset or generated simple sound if licensing is clear.
- Respect device silent behavior where appropriate.

## Acceptance Criteria
- Shake-to-roll works when enabled.
- Shake-to-roll does nothing when disabled.
- Sound can be enabled and disabled.
- Vibration can be enabled and disabled.
- No repeated roll storm occurs from one shake.

## Verification
- Test on physical Android device for shake and vibration.
- Test emulator for settings behavior where sensors are limited.

