# Status

## Completed
- Repository scaffold with documentation and MIT license.
- Android app skeleton with Jetpack Compose and navigation.
- Home, Reader, Settings, Projects, and Browser screens.
- Import flow for images, folders, archives (ZIP/CBZ), and PDFs.
- Room database with entities + repositories for projects, OCR, and translations.
- OpenRouter settings storage (DataStore) and API client skeleton.
- ML Kit OCR engine integration and translation overlay composable.
- Manual translation editing dialog in Reader mode.

## Partial
- OCR + translation pipeline works for single page translation but needs batching, caching strategy refinement, and language detection improvements.
- PDF viewing uses Android PdfRenderer (first-page preview only).
- Browser mode is a simple WebView without overlay translation.

## Remaining
- Batch OCR/translation via WorkManager.
- Export translated page images.
- Advanced overlay fitting and bubble masking.
- Project metadata UI and editing tools.
- Improve translation prompts and glossary support.

## Build Notes
- Android SDK/Android Studio not available in this environment, so build was not executed.
- Gradle wrapper JAR is not generated; run `gradle wrapper` or open the project in Android Studio to regenerate.

## GitHub
- GitHub CLI and token are not available. See `PUSH_TO_GITHUB.md` for manual commands.