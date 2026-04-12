# Architecture

## Overview
Manga Bangla Live follows a modular package structure inside a single Android application module for MVP velocity. The architecture separates UI, domain, data, OCR, translation, overlay rendering, and storage concerns.

```
com.example.mangabanglalive
├── app                # App container / DI-lite wiring
├── ui                 # Compose UI, themes, navigation
├── reader             # Reader screen + view models
├── editor             # Caption edit UI (MVP foundations)
├── settings           # Settings UI + view models
├── data               # Room entities, DAOs, repositories
├── domain             # Domain models + repository interfaces
├── ocr                # OCR interfaces + ML Kit implementation
├── translation        # OpenRouter client + translation engine
├── overlay            # Overlay rendering composables/utilities
├── storage            # DataStore for settings
└── util               # Shared utilities
```

## Data Flow
1. **Import**: User selects images/folder/archive/PDF via system picker.
2. **Persistence**: Projects and pages are stored in Room.
3. **OCR**: `OcrEngine` extracts text segments + bounding boxes.
4. **Translation**: `TranslationEngine` submits segments to OpenRouter.
5. **Overlay**: Translated segments are rendered on top of the page.
6. **Editing**: Users can manually edit segment text (foundation data structures in place).

## Key Interfaces
- `OcrEngine`: Extracts text and bounding boxes from Bitmaps.
- `TranslationEngine`: Translates text segments based on settings.
- `ProjectRepository`: Manages project/page persistence.
- `SettingsRepository`: Stores OpenRouter configuration.

## Storage
- **Room**: Projects, pages, OCR segments, translations.
- **DataStore**: OpenRouter settings, user preferences.

## Background Work
WorkManager is included for future batch OCR/translation workflows (not yet enabled in UI).

## Security
Secrets are never stored in code. The OpenRouter API key is held in local DataStore and should be treated as sensitive.