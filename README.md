# Manga Bangla Live

Manga Bangla Live is an Android-first manga reader and live translation app focused on Bangla translation overlays. This repository provides the initial production-ready scaffold with a clean architecture, Compose UI, import flows, and an OpenRouter integration skeleton.

> **Security note**: Never commit API keys. The app stores the OpenRouter key locally using Android DataStore. For GitHub Actions, use repository secrets instead of `.env` files or hardcoded tokens.

## MVP Status
- ✅ Android project scaffold with Jetpack Compose.
- ✅ Home, Reader, Settings, Projects screens.
- ✅ Import flow for images, folders, archives (via DocumentFile), and PDFs.
- ✅ Room database schema for projects, pages, OCR, and translations.
- ✅ OpenRouter API client + settings storage.
- ✅ OCR + translation interfaces with an ML Kit-based OCR engine implementation.
- ✅ Overlay renderer composable for translated text blocks.
- ⚠️ Translation and OCR pipelines are functional but still need tuning and UI polish.

See [STATUS.md](STATUS.md) for detailed progress and gaps.

## Architecture
The codebase follows a clean-ish MVVM structure with explicit domain/data layers and interfaces for OCR, translation, and overlay rendering. See [ARCHITECTURE.md](ARCHITECTURE.md).

## Stack
- Kotlin
- Jetpack Compose
- Room
- DataStore
- Retrofit + OkHttp
- Coil
- WorkManager
- ML Kit Text Recognition
- Android PDF Viewer (JitPack)

## Setup
1. Install Android Studio (Giraffe+ recommended).
2. Open the project in Android Studio.
3. Let Gradle sync.
4. Run the `app` configuration on an emulator or device.

### OpenRouter Configuration
Open **Settings** in the app and enter:
- API Key
- Model (example: `openrouter/auto`)
- Provider (optional)
- Base URL (`https://openrouter.ai/api/v1` by default)

Tap **Test Connection** to verify.

## Build
```bash
./gradlew :app:assembleDebug
```

## CI
GitHub Actions now validates three stages:
```bash
./gradlew :app:compileDebugKotlin -x processDebugResources --stacktrace
./gradlew :app:testDebugUnitTest --stacktrace
./gradlew :app:assembleDebug --stacktrace
```

If a workflow needs credentials later, add them in `Settings > Secrets and variables > Actions`.
Recommended secret names:
- `OPENROUTER_API_KEY`
- `REPO_ACCESS_TOKEN`

### AndroidIDE (mobile) setup
If you are building inside AndroidIDE, set the SDK path in `local.properties`:

```bash
echo "sdk.dir=/data/user/0/com.tom.rv2ide/files/home/android-sdk" > local.properties
```

Then run:
```bash
./gradlew :app:assembleDebug
```

## Notes
- PDF viewing uses the platform `PdfRenderer` (first-page preview in MVP).
- OCR currently uses ML Kit Text Recognition (Latin). Multi-language support is planned.
- Live translate works best after Accessibility and Overlay permissions are granted.

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md).

## Security
See [SECURITY.md](SECURITY.md).
