# Journey — Android client (Kotlin + Jetpack Compose)

Aplikasi Android untuk **Journey** (journey.anyflow.site) — praktik harian:
devotional, prayer prompt, reflection, journal, prayer requests, circles, dan
feed. Tampilan mengikuti mockup v0 user: bottom nav 5 tab (Home, Programs,
Journal, Feed, You), primary wine `#732E4B`, teks `#3A3A3A`, kartu putih radius
besar, heading serif sistem (mockup memakai Fraunces).

Seluruh isi layar datang dari **API v1** (repo `journey-app`) — tidak ada data
contoh yang di-hardcode di aplikasi.

- Package / applicationId: `com.anyflow.journey`
- Versi: `0.1.0` (versionCode 1), minSdk 26, targetSdk 34, compileSdk 35
- Repo: https://github.com/peterjkambey/journey-android

## Layar

| Tab | Isi |
|-----|-----|
| **Home** | sapaan + streak; Today's Practice (devotional, langkah Prayer & Reflection yang mengembang inline, "Mark ... done", lalu sheet "Share this moment?" dengan unggah foto/video atau skip, dan keadaan selesai kalau dua langkah sudah ditandai); Prayer requests (maks 3, tombol hati = sudah didoakan → kartu hilang); banner "N people prayed for you today"; From your circles (aktivitas, moment tampil foto dulu); My programs (progres + "Continue Day N") |
| **Programs** | Your daily practice; my programs (Day X of Y); browse per kategori + badge "Coming soon" + tombol Start program |
| **Program detail & hari** | durasi, deskripsi, Continue/Start, "What's inside"; layar hari berisi Devotional, Prayer prompt, Reflection, dan Mark day complete |
| **Journal** | "Prayers & entries" dengan filter All/Prayer/Journal; tulis entri baru (type, theme, text, skala 1-10) |
| **Feed** | sub-tab Circles ⇄ Global Community, Prayer requests ⇄ Activity; panel inline "View and manage your circles" (joined + discover + Join/Leave); tombol doa toggle "Pray for this · N" ⇄ "Prayed · N"; Activity foto dulu, caption di bawah |
| **You** | streak, check-ins, circles, prayers; "Noticed by your journey"; timeline pertumbuhan; akun + Sign out |

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

Build debug dan release:

```bash
export JAVA_HOME="C:\Android\jdk17"
/c/Android/gradle-8.9/bin/gradle assembleDebug --console=plain
/c/Android/gradle-8.9/bin/gradle assembleRelease --console=plain
```

APK ada di `app/build/outputs/apk/debug/` dan `app/build/outputs/apk/release/`.
Release ditandatangani dari `keystore.properties` (GITIGNORED, keystore di
`keystore/journey-release.keystore`) — pola sama dengan hris-android supaya
update berikutnya bisa `install -r` tanpa menghapus data aplikasi.

## Base URL API

Default produksi:

```
https://journey.anyflow.site/api/v1/
```

Untuk uji lokal terhadap `php artisan serve` di PC (emulator menembak host):

```bash
/c/Android/gradle-8.9/bin/gradle assembleDebug -PapiBaseUrl=http://10.0.2.2:8123/api/v1/ --console=plain
```

HTTP cleartext hanya diizinkan untuk `10.0.2.2`, `localhost`, dan `127.0.0.1`
(`res/xml/network_security_config.xml`); host lain wajib HTTPS.

## Akun demo

`samuel.wong@student.anyflow.site` — akun member demo. Password demo = nilai
`STUDENT_DEMO_PASSWORD` di `.env` server (`/var/www/journey/.env`).

Layar login menampilkan **kartu DEMO ACCOUNT** berisi alamat lengkap + password
dan tombol *Use demo account* untuk mengisi ulang kedua field. Alamat itu juga
terisi otomatis di field (font diperkecil) — field satu baris akan memotong
alamat panjang, dan itulah yang dulu membuat alamat terlihat seperti salah.
Data akunnya sendiri tidak pernah dipendekkan.

## Pasang ke HP (wireless debugging)

```bash
adb pair <ip>:<port-pairing>        # kode dari dialog "Pair device with pairing code" di HP
adb connect <ip>:<port-connect>
adb -s <ip>:<port> install -r app/build/outputs/apk/release/app-release.apk
adb -s <ip>:<port> shell am start -n com.anyflow.journey/.MainActivity
```

Port pairing berubah setiap Wireless debugging diaktifkan — cari port terkini
dengan `adb mdns services` (`_adb-tls-pairing._tcp` untuk pairing,
`_adb-tls-connect._tcp` untuk connect).

## Dokumentasi terkait

Backend, model data, dan aturan yang dipakai bersama ada di repo `journey-app`:
`docs/architecture.md`, `docs/data-model.md`, `docs/features.md`,
`docs/decisions.md`, `docs/android-app.md` — juga terbit di
https://journey.anyflow.site/docs setelah login.

Detail aplikasi ini: `docs/features.md`, `docs/architecture.md`,
`docs/decisions.md`.
