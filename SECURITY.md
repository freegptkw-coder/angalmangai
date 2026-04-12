# Security Policy

## Reporting a Vulnerability
Please report security issues privately. Do **not** open public issues for sensitive vulnerabilities.

## Secrets Handling
- Never commit API keys, tokens, or credentials.
- The OpenRouter API key is stored locally in DataStore and should not be logged.

## Data Privacy
OCR and translation results are cached locally for performance. Users should be informed that translations are sent to the configured provider (OpenRouter).