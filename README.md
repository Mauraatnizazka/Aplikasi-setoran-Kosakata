# Setoran Kosakata

Aplikasi Android (Kotlin) untuk setoran kosakata bahasa asing per hari.
Tugas P10 Pemrograman Mobile - SharedPreferences.

## Alur aplikasi
1. **Hari Ini** - lihat target harian, progres (angka + lingkaran persen), hari beruntun,
   rekap 7 hari terakhir, dan daftar setoran hari ini. Ketuk **Setor Kosakata** untuk menyetor.
2. **Setor Kosakata** - isi kata asing, arti, bahasa, contoh kalimat. Tersedia
   "Simpan & Tambah Lagi" supaya bisa menyetor beberapa kata berturut-turut.
3. Tandai kosakata **hafal** dengan ikon centang pada kartu.
4. **Riwayat** - semua setoran dikelompokkan per hari, lengkap dengan status
   "Target tercapai" atau "Kurang N".
5. **Pengaturan** - mode gelap, target harian, bahasa default.

## Pembagian penyimpanan
| Data | Lokasi | Alasan |
|------|--------|--------|
| Mode gelap, target harian, bahasa terakhir | SharedPreferences (`PrefsManager`, `apply()`) | Pengaturan kecil berbentuk key-value |
| Daftar kosakata | Room Database | Data banyak, terstruktur, perlu query per tanggal |

## Teknologi
Kotlin, Room, ViewModel, LiveData, ViewBinding, Fragment, Material Components.
minSdk 26, compileSdk 34.
