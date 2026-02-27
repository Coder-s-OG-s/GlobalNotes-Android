# 📝 Global Notes — Android

> Native Android client for Global Notes Workspace — a rich document editor with offline-first sync, Supabase auth, and folders/labels organization.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

The Android companion to the [Global Notes Workspace](https://github.com/Coder-s-OG-s/Global_Notes-workspace) web app — a full-featured, MS Word / Apple Notes style document editor that works offline and syncs to the cloud.

🌐 **Web App**: [ojt-2025-persistent-notes-workspace-nine.vercel.app](https://ojt-2025-persistent-notes-workspace-nine.vercel.app/)

---

## ✨ Features

- **Rich Document Editor** — Bold, italic, underline, strikethrough, text size, text color, highlights, bullet lists, alignment, and more
- **Offline-First** — All notes saved locally with Room. Works without internet, syncs when connected
- **Supabase Cloud Sync** — Sign in to sync notes across your devices. Guest mode available with local-only storage
- **Folders & Labels** — Organize notes into folders with custom colors and labels with descriptions
- **Document Backgrounds** — Plain, Lined, Grid, Dotted, Blueprint, and more backgrounds for the editor canvas
- **Rich Inserts** — Embed photos, audio recordings, sketches (finger drawing), shapes, and tables inside notes
- **AI Assistant** — Built-in AI panel to generate content directly in your note
- **QR Sharing** — Share notes via WhatsApp, email, or QR code
- **Themes** — AMOLED Dark, Nature Green, Corporate Gray, and Minimal White
- **Import / Export** — JSON-based backup and restore
- **Adaptive Layout** — 3-panel layout on tablets (like the web), single-panel with drawer on phones

---

## 📸 Screenshots

> Coming soon — contributions welcome!

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Local DB | Room (SQLite) |
| Cloud | Supabase (Auth + PostgreSQL) |
| Dependency Injection | Hilt |
| Navigation | Navigation Compose |
| Network | Ktor Client |
| Rich Text Editor | richeditor-compose |
| Image Loading | Coil |
| QR Code | ZXing Android Embedded |
| Serialization | kotlinx.serialization |
| Preferences | DataStore |
| Background Sync | WorkManager |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 26+ (minSdk 26)
- A [Supabase](https://supabase.com) project (optional — app works in guest/offline mode without it)

### 1. Clone the repo

```bash
git clone https://github.com/YOUR_USERNAME/Global-Notes-Android.git
cd Global-Notes-Android
```

### 2. Add your Supabase credentials

Create a `local.properties` file in the root of the project (it's gitignored):

```properties
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
```

> If you skip this step, the app will run in **Guest / Offline mode** — all notes are stored locally only.

### 3. Set up the Supabase database

In your Supabase SQL editor, run:

```sql
-- Notes table
create table notes (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  title text not null default '',
  content text not null default '',
  folder_id uuid references folders(id) on delete set null,
  tags text not null default '',
  card_color text not null default 'ClassicBlue',
  background_style text not null default 'Plain',
  is_deleted boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- Folders table
create table folders (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  name text not null,
  color text not null default '#4A90D9',
  created_at timestamptz not null default now()
);

-- Labels table
create table labels (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  name text not null,
  description text,
  color text not null default '#000000',
  created_at timestamptz not null default now()
);

-- Row Level Security
alter table notes enable row level security;
alter table folders enable row level security;
alter table labels enable row level security;

create policy "Users can only access their own notes"
  on notes for all using (auth.uid() = user_id);

create policy "Users can only access their own folders"
  on folders for all using (auth.uid() = user_id);

create policy "Users can only access their own labels"
  on labels for all using (auth.uid() = user_id);
```

### 4. Build and run

Open the project in Android Studio and click **Run**, or:

```bash
./gradlew assembleDebug
```

---

## 📁 Project Structure

```
app/src/main/java/com/globalnotes/app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── remote/         # Supabase API, DTOs
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Note, Folder, Label domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic use cases
├── presentation/
│   ├── auth/           # Login / Sign up screen
│   ├── home/           # Notes list screen
│   ├── editor/         # Document editor screen
│   ├── folders/        # Folders management screen
│   ├── search/         # Search screen
│   ├── settings/       # Settings screen
│   └── components/     # Shared Composables
├── di/                 # Hilt dependency injection modules
├── util/               # Extensions, constants, helpers
└── MainActivity.kt
```

---

## 🤝 Contributing

Contributions are what make open source great! See [CONTRIBUTING.md](CONTRIBUTING.md) for full guidelines on how to get started.

Quick steps:
1. Fork the repo
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push and open a Pull Request

---

## 🔗 Related

- [Global Notes Workspace (Web)](https://github.com/Coder-s-OG-s/Global_Notes-workspace) — the original web app this is based on
- [Live Web Demo](https://ojt-2025-persistent-notes-workspace-nine.vercel.app/)

---

## 📜 License

Distributed under the MIT License. See [LICENSE](LICENSE) for more information.

---

## 💬 Community

Have questions, ideas, or found a bug? [Open an issue](https://github.com/YOUR_USERNAME/Global-Notes-Android/issues) — we'd love to hear from you.
