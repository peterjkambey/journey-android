# Arsitektur — journey-android

Aplikasi klien tipis: seluruh data dari API v1 `journey-app`. Tidak ada database
lokal dan tidak ada data contoh hardcode (beda dari hris-android yang memang
demo/screenshot).

## Struktur kode

```
app/src/main/java/com/anyflow/journey/
├── JourneyApp.kt              # Application: inisialisasi
├── MainActivity.kt            # satu Activity, setContent { JourneyTheme { App() } }
├── data/
│   ├── Api.kt                 # Retrofit interface + OkHttp client (Bearer token + Accept: json)
│   ├── Models.kt              # DTO data class untuk seluruh respons API v1
│   ├── JourneyRepository.kt   # panggilan API + pemetaan error
│   └── SessionStore.kt        # token & identitas di SharedPreferences
└── ui/
    ├── App.kt                 # root: Login ⇄ MainShell, bottom nav 5 tab, sheet share
    ├── JourneyViewModel.kt    # state layar + aksi (load home, pray, complete step, tulis jurnal, join/leave circle)
    ├── Theme.kt               # identitas visual (wine #732E4B, tipografi, shape)
    ├── Components.kt          # komponen bersama: JourneyCard, AvatarBadge, WineButton, media card, dsb.
    └── screens/
        ├── LoginScreen.kt
        ├── HomeScreen.kt          # termasuk sheet "Share this moment?" + panel program
        ├── ProgramsScreen.kt
        ├── ProgramDetailScreen.kt
        ├── ProgramDayScreen.kt
        ├── JournalScreen.kt
        ├── FeedScreen.kt          # sub-tab + panel circle inline
        └── YouScreen.kt
```

## Pola

- **MVVM ringan**: `JourneyViewModel` menyimpan state (data Home, program,
  feed, jurnal) dan mengekspos aksi; layar hanya merender state. Aksi yang butuh
  token memakai `JourneyRepository` yang menyisipkan header dari `SessionStore`.
- **Retrofit + Gson** untuk API, **OkHttp** untuk client dan logging; **Coil**
  untuk foto moment di Feed. Kalau media gagal dimuat, kartu tetap render
  placeholder berwarna + caption.
- **Sesi**: `POST /auth/login` → token disimpan di `SharedPreferences`. Token
  dikirim sebagai `Authorization: Bearer <token>` pada semua request. Logout
  memanggil `POST /auth/logout` lalu menghapus token lokal.
- **Navigasi**: satu Activity, bottom navigation 5 tab (Home, Programs, Journal,
  Feed, You); layar detail program dan layar hari adalah tujuan lanjutan di
  dalam tab Programs. Sheet "Share this moment?" dipasang di shell (`App.kt`)
  supaya bisa muncul dari Home maupun dari layar hari.
- **Konfigurasi API**: `buildConfigField("String", "API_BASE_URL", ...)` dengan
  default produksi dan override lewat `-PapiBaseUrl=...` saat build.

## Aturan yang mengikuti server

- **Today's Practice** = hari pertama program daily practice yang belum selesai;
  kalau hari itu diselesaikan HARI INI, kartu menampilkan keadaan selesai (tidak
  langsung melompat ke hari berikutnya). Logika ini dihitung server, app hanya
  merender `completed`/`steps`.
- **Hati doa** = toggle: menekan sekali memanggil
  `POST /prayer-requests/{id}/pray` dan kartu langsung hilang dari Home; status
  "Prayed · N" muncul di Feed.
- **Activity** hanya memuat aktivitas non-doa (doa punya tab sendiri), moment
  tampil media-dulu dengan caption di bawah.

## Pitfall yang sudah dibayar

1. **Cleartext HTTP diblokir di targetSdk 34** — uji ke `php artisan serve` lokal
   gagal tanpa `res/xml/network_security_config.xml` (base-config
   `cleartextTrafficPermitted="false"` + domain-config true hanya untuk
   10.0.2.2/localhost/127.0.0.1) dan `android:networkSecurityConfig` di manifest.
2. **Bentrok signature setter ViewModel** — beberapa setter dengan pola
   `selectX(...)` perlu nama/parameter berbeda agar tidak bentrok di JVM.
3. **Verifikasi di emulator tanpa jendela** — user terganggu jendela emulator
   yang besar; jalankan `emulator.exe -no-window -no-audio -no-boot-anim` dan
   ambil bukti lewat `adb exec-out screencap`.
4. **Pairing wireless port berubah** — `error: protocol fault` saat `adb pair`
   berarti port pairing sudah berganti; pindai ulang `adb mdns services` dan
   ulangi dengan kode baru.

## Gate

- `assembleDebug` dan `assembleRelease` harus BUILD SUCCESSFUL (release sekaligus
  menjalankan `lintVitalRelease`).
- Verifikasi APK: `aapt dump badging` (package, label, minSdk, targetSdk).
- Tidak ada unit test di repo ini; pengujian perilaku ada di sisi server (Pest)
  plus uji manual di HP/emulator.
