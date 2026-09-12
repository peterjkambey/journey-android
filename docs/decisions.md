# Keputusan (ADR) — journey-android

## ADR-001 (12-09-2026) — Repo, identitas, dan peran repo
Repo baru `journey-android` (E:\project_2026\ANYFLOW\journey-android), package
`com.anyflow.journey`, label aplikasi "Journey", branch `main`, GitHub publik
`peterjkambey/journey-android`. Repo ini adalah KLIEN dari `journey-app`: app
menentukan fitur, web mengikuti (lihat ADR-014 di repo journey-app).

## ADR-002 (12-09-2026) — Kotlin + Jetpack Compose, build via CLI
Stack mengikuti pola app Android AnyFlow yang sudah terbukti (hris-android):
Kotlin 2.1.0, AGP 8.7.3, Compose BOM 2024.12.01, Gradle 8.9, compileSdk 35,
targetSdk 34, minSdk 26, Retrofit/OkHttp/Gson, Coil untuk foto. Build tanpa
Android Studio (JDK portable + SDK + Gradle langsung).

## ADR-003 (12-09-2026) — Tidak ada data hardcode
Berbeda dari hris-android (demo/screenshot dengan data bawaan), aplikasi ini
mengambil semua data dari API v1. Tampilan mengikuti mockup v0 user
(5 tab, warna wine, kartu radius besar), tetapi isinya nyata: program, hari,
prayer request, moment, insight dari server.

## ADR-004 (12-09-2026) — Lubang mockup diisi sebagai requirement
Mockup tidak punya login, tidak bisa membuat prayer request, dan journal-nya
read-only. Ketiganya dianggap requirement (bukan improvisasi): layar Login
dibuat, endpoint tulis jurnal dan buat prayer request dipakai dari app.

## ADR-005 (12-09-2026) — Signing release sendiri
Keystore `keystore/journey-release.keystore` + `keystore.properties` (keduanya
GITIGNORED, pola hris-android) dipakai untuk `assembleRelease`, supaya update
berikutnya bisa dipasang dengan `install -r` tanpa menghapus data aplikasi.

## ADR-006 (12-09-2026) — Cleartext hanya untuk host dev
Produksi wajib HTTPS. Cleartext HTTP diizinkan HANYA untuk 10.0.2.2, localhost,
dan 127.0.0.1 lewat network security config, supaya pengujian terhadap
`php artisan serve` di PC tetap mungkin tanpa melonggarkan keamanan produksi.

## ADR-007 (12-09-2026) — Kredensial demo diisi otomatis (masa demo)
Layar login mengisikan email dan password akun demo beserta URL API, supaya demo
bisa dibuka dengan satu tap. User memutuskan membiarkannya selama masa demo;
password akun dashboard (super-admin, admin komunitas) tidak diubah.
