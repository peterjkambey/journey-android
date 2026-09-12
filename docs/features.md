# Fitur — journey-android (v0.1.0)

Semua fitur LIVE dan sudah terpasang di HP demo (OPPO CPH2603). Sumber data:
API v1 journey-app.

| Fitur | Status | Tanggal |
|-------|--------|---------|
| Login member (email + password) → token Sanctum disimpan di SharedPreferences | LIVE | 12-09-2026 |
| Home: sapaan + streak, Today's Practice, Prayer requests (maks 3, tombol hati), banner doa, From your circles, My programs | LIVE | 12-09-2026 |
| Today's Practice: langkah Prayer & Reflection mengembang inline, "Mark ... done", keadaan "Today's practice complete — see you tomorrow" | LIVE | 12-09-2026 |
| Sheet "Share this moment?" setelah langkah doa: unggah foto/video (ActivityResultContracts.PickVisualMedia) atau "Skip for now" | LIVE | 12-09-2026 |
| Prayer requests: tombol hati "Pray for this · N" ⇄ "Prayed · N" (toggle), kartu hilang dari Home setelah didoakan | LIVE | 12-09-2026 |
| Circles: panel inline "View and manage your circles" (joined + discover + Join) | LIVE | 12-09-2026 |
| Feed: Circles ⇄ Global Community, Prayer requests ⇄ Activity, moment foto/video dulu + caption di bawah (Coil) | LIVE | 12-09-2026 |
| Programs: Your daily practice, My programs, browse per kategori + badge "Coming soon", Start program (enroll) | LIVE | 12-09-2026 |
| Program detail + layar hari: Devotional, Prayer prompt, Reflection, Mark day complete, What's inside (pindah hari) | LIVE | 12-09-2026 |
| Journal: "Prayers & entries", filter All/Prayer/Journal, tulis entri (type, theme, text, skala 1-10) | LIVE | 12-09-2026 |
| You: Streak/Check-ins/Circles/Prayers, "Noticed by your journey", timeline pertumbuhan, akun + Sign out | LIVE | 12-09-2026 |
| Tema visual mockup: wine #732E4B, Inter untuk body, serif sistem untuk heading, kartu radius besar, bottom nav 5 tab | LIVE | 12-09-2026 |
| Signing release sendiri (keystore.properties, gitignored) | LIVE | 12-09-2026 |
| Network security config: cleartext hanya untuk host dev (10.0.2.2/localhost/127.0.0.1) | LIVE | 12-09-2026 |

## Lubang mockup yang sengaja diisi

Mockup v0 tidak punya layar login, cara membuat prayer request, atau cara menulis
journal entry (journal-nya read-only). Ketiganya diisi karena aplikasi nyata
membutuhkannya:

- Layar **Login** (mockup: tidak ada) dengan kredensial demo terisi otomatis.
- **Menulis jurnal** dari tab Journal (mockup: hanya daftar).
- **Membuat prayer request** lewat endpoint `POST /prayer-requests` (belum ada
  tombol khusus di UI; endpoint siap, menyusul di UI kalau diminta).

## Belum dikerjakan

- Push notification (reminder harian masih lewat email/WhatsApp dari server).
- Ikon launcher kustom (masih ikon adaptif standar).
- Reaksi moment (tabel `moment_reactions` sudah ada di server, UI belum).
- Unit test / screenshot test di repo ini — gate saat ini: `assembleDebug` +
  `assembleRelease` + `lintVital`.
