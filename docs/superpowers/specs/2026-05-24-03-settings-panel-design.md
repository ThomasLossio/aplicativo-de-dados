# Spec 03 - Settings Panel

## Goal
Add the settings experience as a bottom panel with expandable sections.

## Scope
- Gear button opens a bottom sheet style settings panel.
- The panel contains expandable rows:
  - Número de dados
  - Valores dos dados
  - Aparência
  - Animação e rolagem
  - Som e vibração
  - Mostrar total
- Only one section needs to be expanded at a time unless Compose implementation makes multiple sections simpler without clutter.
- The panel sits over the dice screen.

## User Experience
The main screen remains clean. Detailed configuration lives behind the gear button. Each row expands inline to show controls for that category.
All visible labels are in Brazilian Portuguese.

## Technical Design
- Use Compose modal bottom sheet or a custom bottom panel, depending on which gives the best full-screen dice presentation.
- Settings rows are separate composables.
- Keep settings state centralized so later specs can add controls without rewriting the panel.

## Acceptance Criteria
- Tapping gear opens the settings panel.
- Tapping outside or closing dismisses the panel.
- Each row can expand and collapse.
- Section labels are shown in Brazilian Portuguese.
- The panel does not permanently hide the bottom controls after closing.

## Verification
- Manual test opening and closing the panel.
- Manual test expanding each settings row.
