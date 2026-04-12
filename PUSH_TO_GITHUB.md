# Push to GitHub

This workspace does not have the GitHub CLI configured. Use the steps below to create and push the repository manually.

## Option A: GitHub CLI (recommended)
```
export REPO_NAME="manga-bangla-live"
gh auth login
gh repo create "$REPO_NAME" --private --source=. --remote=origin --push
```

## Option B: Manual Git Remote
```
export REPO_NAME="manga-bangla-live"
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/$REPO_NAME.git
git push -u origin main
```

> Replace `<YOUR_GITHUB_USERNAME>` with your GitHub handle.