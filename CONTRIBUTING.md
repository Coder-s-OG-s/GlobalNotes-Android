# Contributing to Global Notes — Android

First off, thank you for taking the time to contribute! 🎉

Global Notes Android is an open source project and we welcome contributions of all kinds — bug fixes, new features, documentation improvements, UI polish, and more. This guide will help you get started.

---

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pull Request Process](#pull-request-process)
- [Code Style](#code-style)
- [Project Structure](#project-structure)
- [Good First Issues](#good-first-issues)

---

## Code of Conduct

By participating in this project, you agree to be respectful and constructive. We want this to be a welcoming space for everyone regardless of experience level. Harassment, discrimination, or toxic behavior of any kind will not be tolerated.

---

## How Can I Contribute?

### 🐛 Reporting Bugs

Before opening a bug report, please check if it already exists in [Issues](https://github.com/YOUR_USERNAME/Global-Notes-Android/issues).

When filing a bug, include:
- Device model and Android version
- Steps to reproduce the issue
- What you expected to happen vs. what actually happened
- Screenshots or screen recordings if helpful
- Logcat output if the app crashed

Use the **Bug Report** issue template.

### 💡 Suggesting Features

Open a [Feature Request](https://github.com/YOUR_USERNAME/Global-Notes-Android/issues/new) issue and describe:
- The problem you're trying to solve
- How the feature would work from a user's perspective
- Any similar features from the web app or other apps you can reference

### 🔧 Submitting Code

Code contributions are welcome! See the workflow below.

### 📖 Improving Documentation

Even small documentation fixes (typos, broken links, unclear instructions) are valuable. Feel free to open a PR directly for docs changes.

---

## Getting Started

### Fork & Clone

```bash
# Fork the repo on GitHub, then:
git clone https://github.com/YOUR_USERNAME/Global-Notes-Android.git
cd Global-Notes-Android
git remote add upstream https://github.com/ORIGINAL_USERNAME/Global-Notes-Android.git
```

### Set Up the Project

1. Open the project in **Android Studio Hedgehog or newer**
2. Create `local.properties` in the root and add your Supabase credentials (or leave blank for offline/guest mode):
   ```properties
   SUPABASE_URL=https://your-project.supabase.co
   SUPABASE_ANON_KEY=your-anon-key
   ```
3. Let Gradle sync complete
4. Run the app on an emulator or physical device (API 26+)

### Keep Your Fork Updated

```bash
git fetch upstream
git checkout main
git merge upstream/main
```

---

## Development Workflow

### 1. Pick or create an issue

Check [Issues](https://github.com/YOUR_USERNAME/Global-Notes-Android/issues) for something to work on. If you're starting something new, open an issue first so we can discuss it before you invest time building it.

Comment on the issue to let others know you're working on it.

### 2. Create a branch

Always branch off `main`. Use a descriptive name:

```bash
git checkout -b feature/ai-assistant-panel
git checkout -b fix/editor-autosave-crash
git checkout -b docs/update-setup-instructions
git checkout -b ui/amoled-theme-polish
```

**Branch naming prefixes:**

| Prefix | Use for |
|---|---|
| `feature/` | New features |
| `fix/` | Bug fixes |
| `ui/` | UI-only changes, polish, theming |
| `docs/` | Documentation only |
| `refactor/` | Code cleanup without behavior change |
| `test/` | Adding or fixing tests |

### 3. Make your changes

- Keep your changes focused. One PR = one thing.
- Write or update tests where applicable.
- Make sure the app builds and runs before submitting.

### 4. Test your changes

At minimum, verify:
- The feature/fix works as expected on a phone screen
- It also works correctly on a tablet (or in the resizable emulator)
- Offline mode still works (airplane mode test)
- No new lint warnings introduced (`./gradlew lint`)
- No crashes in logcat during your change's flow

### 5. Open a Pull Request

Push your branch and open a PR against `main`.

---

## Commit Message Guidelines

We follow a simplified version of [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>: <short summary>

<optional longer description>

<optional: Closes #issue-number>
```

**Types:**

| Type | Use for |
|---|---|
| `feat` | A new feature |
| `fix` | A bug fix |
| `ui` | Visual/UI-only changes |
| `docs` | Documentation changes |
| `refactor` | Code refactoring |
| `test` | Adding or updating tests |
| `chore` | Build config, dependencies, tooling |

**Examples:**

```
feat: add audio recording insert in editor

fix: crash when opening note with null folder id
Closes #42

ui: apply AMOLED theme to editor toolbar background

docs: add Supabase setup instructions to README
```

Keep the summary line under 72 characters. Use present tense ("add" not "added").

---

## Pull Request Process

1. **Title** — Use the same format as commits: `feat: add sketch insert dialog`
2. **Description** — Fill out the PR template:
   - What does this PR do?
   - How was it tested?
   - Screenshots / screen recordings (for UI changes — this is very helpful)
   - Related issue number
3. **Size** — Keep PRs small and focused. Large PRs take longer to review and are harder to merge cleanly.
4. **Review** — A maintainer will review your PR. Be responsive to feedback and requests for changes.
5. **Merge** — Once approved, a maintainer will merge it. We use squash merge to keep the history clean.

### PR Checklist

Before submitting, verify:

- [ ] Code builds without errors (`./gradlew assembleDebug`)
- [ ] No new lint errors (`./gradlew lint`)
- [ ] Tested on phone form factor
- [ ] Tested on tablet form factor (or resizable emulator)
- [ ] Offline mode still works
- [ ] No hardcoded strings — all user-visible text in `strings.xml`
- [ ] No API keys or secrets committed
- [ ] Screenshots included for any UI changes

---

## Code Style

We follow standard Kotlin + Compose conventions:

### Kotlin
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `val` over `var` wherever possible
- Prefer expression bodies for simple functions
- No wildcard imports

### Jetpack Compose
- One Composable per file for screens; smaller reusable components can be grouped
- Composable names are PascalCase nouns: `NoteEditorScreen`, `FormattingToolbar`
- State hoisting — keep state in ViewModels, not inside Composables
- Use `@Preview` annotations for components where practical
- Avoid logic inside Composables — delegate to ViewModels

### Architecture
- Follow the existing MVVM + Clean Architecture pattern
- All data access goes through the Repository layer
- ViewModels expose `StateFlow<UiState>` — never expose Room entities or DTOs directly to the UI
- Use sealed classes for UI state: `Loading`, `Success`, `Error`

### Strings
All user-visible text must be in `res/values/strings.xml`. No hardcoded strings in Kotlin or Composable files.

### No secrets in code
Never commit `SUPABASE_URL`, `SUPABASE_ANON_KEY`, or any credentials. These go in `local.properties` only, which is gitignored.

---

## Project Structure

If you're new to the codebase, start here:

```
presentation/home/        → Notes list, the main screen
presentation/editor/      → The document editor — the core of the app
presentation/auth/        → Login / sign up
data/local/               → Room DB, entities, DAOs
data/remote/              → Supabase API calls
domain/usecase/           → Business logic (good place to add features)
di/                       → Hilt modules (where dependencies are wired up)
```

The `editor` package is the most complex. The rich text editor, formatting toolbar, insert dialogs, AI panel, and auto-save logic all live there.

---

## Good First Issues

New to the project? Look for issues tagged [`good first issue`](https://github.com/YOUR_USERNAME/Global-Notes-Android/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22). These are smaller, well-defined tasks that are a great way to get familiar with the codebase.

Some examples of great starter contributions:
- Adding a missing string to `strings.xml`
- Adding a `@Preview` for a composable that doesn't have one
- Fixing a UI alignment or spacing issue
- Writing a use case unit test
- Improving an error message or empty state UI

---

## Questions?

Not sure about something? Don't hesitate to:
- Comment on the relevant issue
- Open a [Discussion](https://github.com/YOUR_USERNAME/Global-Notes-Android/discussions)
- Ask in your PR — it's better to ask than to guess

We appreciate every contribution, no matter how small. Thank you for helping make Global Notes better! 🚀
