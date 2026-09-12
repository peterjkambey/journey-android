# Journey — Android client (Kotlin + Jetpack Compose)

Aplikasi Android untuk **Journey** (journey.anyflow.site) — praktik harian:
devotional, prayer prompt, reflection, journal, prayer requests, circles, dan feed.
Tampilan mengikuti mockup v0 user: bottom nav 5 tab (Home, Programs, Journal,
Feed, You), primary wine `#732E4B`, teks `#3A3A3A`, kartu putih radius besar,
heading serif sistem (mockup memakai Fraunces).

- Package / applicationId: `com.anyflow.journey`
- Versi: `0.1.0` (versionCode 1), minSdk 26, targetSdk 34, compileSdk 35
- Repo: https://github.com/peterjkambey/journey-android

## Cara build (Windows, git-bash, TANPA Android Studio)

Toolchain yang dipakai (sudah terpasang di PC ini):

| Komponen | Lokasi |
|---|---|
| JDK 17 | `C:\Android\jdk17` |
| Android SDK | `C:\Android\sdk` (platforms;android-35, build-tools;34.0.0) |
| Gradle 8.9 | `C:\Android\gradle-8.9` (dipanggil langsung, TANPA wrapper) |

`local.properties` wajib ada di root repo:

```
sdk.dir=C\:\\Android\\sdk
```

Build debug:

```bash
export JAVA_HOME="C:\Android\jdk17"
cd /e/project_2026/ANYFLOW/journey-android
/c/Android/gradle-8.9/bin/gradle assembleDebug --console=plain
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Verifikasi APK:

```bash
"C:/Android/sdk/build-tools/34.0.0/aapt.exe" dump badging app/build/outputs/apk/debug/app-debug.apk | grep -E "^package:|application-label:|sdkVersion:|targetSdkVersion:"
```

Install ke HP (wireless debugging / USB):

```bash
adb devices
adb -s <serial> install -r app/build/outputs/apk/debug/app-debug.apk
adb -s <serial> shell am start -n com.anyflow.journey/.MainActivity
```

Buka tab tertentu langsung (bantu screenshot):

```bash
adb shell am start -n com.anyflow.journey/.MainActivity --es tab FEED
```

## Base URL API

Default: `https://journey.anyflow.site/api/v1/` (diset di `app/build.gradle.kts`
sebagai `BuildConfig.API_BASE_URL`). Bisa dioverride saat build:

```bash
/c/Android/gradle-8.9/bin/gradle assembleDebug -PapiBaseUrl=http://10.0.2.2:8123/api/v1/
```

(`10.0.2.2` = host PC dari dalam emulator Android.)

Semua endpoint butuh header `Authorization: Bearer <token>` + `Accept: application/json`,
kecuali `POST auth/login`.

> **Catatan status server (12-09-2026):** endpoint API v1 sudah ada di repo
> `journey-app` (`routes/api.php`, commit `318ea0b`) dan sudah diuji jalan di
> `php artisan serve` lokal, **tetapi belum ter-deploy ke production** —
> `https://journey.anyflow.site/api/v1/*` masih 404. Deploy `journey-app` dulu
> supaya app ini bisa login dari HP.

## Akun demo

```
email:    samuel.wong@student.anyflow.site
password: journey##keren
```

Layar login sudah terisi akun demo itu (mockup tidak punya layar login; endpoint
butuh token Sanctum, jadi layar login ditambahkan).

## Layar

| Tab | Isi |
|---|---|
| Home | sapaan "Good morning" + streak, TODAY'S PRACTICE (langkah Prayer/Reflection mengembang inline, "Mark prayer done"/"Mark reflection done", bottom sheet "Share this moment?" → Add photo/video / Skip for now), PRAYER REQUESTS (maks 3, hati → kartu hilang), banner "N people prayed for you today", FROM YOUR CIRCLES (foto dulu, caption di bawah), kartu program (progres + Continue Day N) |
| Programs | "Grow with structure", YOUR DAILY PRACTICE, MY PROGRAMS (Day X of Y), BROWSE per kategori + badge "Coming soon"; detail program (durasi, deskripsi, Continue · Day N / Start program, WHAT'S INSIDE); layar hari (header `PROGRAM · DAY N OF M`, DEVOTIONAL, PRAYER PROMPT, REFLECTION, Mark day complete) |
| Journal | "Prayers & entries", filter All / Prayer / Journal, kartu entri; FAB tulis entri (type, theme, text, scale 1–10) → `POST /journal` |
| Feed | header komunitas, sub-tab Circles / Global Community, sub-tab Prayer requests / Activity, panel INLINE "View and manage your circles" (JOINED + DISCOVER dengan Join/Leave), prayer cards dengan toggle "Pray for this · N" ↔ "Prayed · N", activity foto dulu + caption di bawah |
| You | "Your journey" + 4 statistik (Streak, Check-ins, Circles, Prayers), "Noticed by your journey", "Personal growth timeline" (tanggal + type_label + judul + isi), akun + Sign out |

Check-in harian: menandai langkah via `POST program-days/{dayId}/complete`
(`prayer` / `reflection` / `day`); setelah langkah pertama muncul sheet berbagi
foto/video (`POST moments`, multipart) yang langsung tampil di tab Feed.

## Struktur kode

```
app/src/main/java/com/anyflow/journey/
├── JourneyApp.kt                 # Application: SessionStore + ApiClient
├── MainActivity.kt               # entry + deep link --es tab
├── data/
│   ├── Api.kt                    # Retrofit interface + ApiClient (OkHttp)
│   ├── Models.kt                 # DTO JSON API v1
│   ├── JourneyRepository.kt      # satu pintu akses API + error message
│   └── SessionStore.kt           # token Sanctum di SharedPreferences
└── ui/
    ├── Theme.kt                  # wine #732E4B, heading serif
    ├── Components.kt             # JourneyCard, PillTabs, StatTile, MediaBlock, …
    ├── JourneyViewModel.kt       # state 5 tab + semua aksi
    ├── App.kt                    # shell, bottom nav 5 tab, route detail
    └── screens/                  # Login, Home, Programs, ProgramDetail,
                                  # ProgramDay, Journal, Feed, You
```

## Dependency

Compose BOM `2024.12.01` (AGP 8.7.3, Kotlin 2.1.0), Retrofit 2.11.0 +
converter-gson, OkHttp 4.12.0 (+ logging), coroutines 1.9.0, dan
**Coil `io.coil-kt:coil-compose:2.7.0`** untuk memuat `media_url` di Feed
(kalau gagal dimuat, kartu menampilkan blok warna + caption dan tetap compile).

## Batasan yang diketahui

- Endpoint `POST /moments` (foto aktivitas) belum diuji dari HP karena API v1
  belum live di production.
- Mockup tidak punya layar login/profil/notifikasi; layar login ditambahkan
  (wajib untuk token) dan sign out ada di tab You.
