package com.example.setorankosakata

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Format tanggal. Tanggal disimpan sebagai teks yyyy-MM-dd (mis. 2026-11-07). */
object DateUtil {
    private val localeId: Locale = Locale.forLanguageTag("id-ID")
    private val longFormat: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", localeId)

    fun today(): String = LocalDate.now().toString()

    fun formatLong(date: LocalDate): String = date.format(longFormat)

    fun formatLong(dateText: String): String {
        return try {
            LocalDate.parse(dateText).format(longFormat)
        } catch (e: Exception) {
            dateText
        }
    }

    fun dayShort(date: LocalDate): String = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "Sen"
        DayOfWeek.TUESDAY -> "Sel"
        DayOfWeek.WEDNESDAY -> "Rab"
        DayOfWeek.THURSDAY -> "Kam"
        DayOfWeek.FRIDAY -> "Jum"
        DayOfWeek.SATURDAY -> "Sab"
        DayOfWeek.SUNDAY -> "Min"
    }
}

/** Perhitungan statistik setoran. */
object StudyStats {

    /** Jumlah setoran per tanggal. */
    fun countsByDate(list: List<Vocab>): Map<String, Int> =
        list.groupingBy { it.date }.eachCount()

    /**
     * Hari beruntun: berapa hari berturut-turut target harian tercapai.
     * Jika hari ini belum mencapai target, hitung mundur mulai dari kemarin.
     */
    fun streak(counts: Map<String, Int>, target: Int, today: LocalDate): Int {
        var day = today
        if ((counts[day.toString()] ?: 0) < target) {
            day = day.minusDays(1)
        }
        var streak = 0
        while ((counts[day.toString()] ?: 0) >= target) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }
}

/** Kode singkat bahasa untuk lingkaran di kartu kosakata. */
object LangUtil {
    fun code(language: String): String = when (language) {
        "Inggris" -> "EN"
        "Arab" -> "AR"
        "Jepang" -> "JP"
        "Mandarin" -> "ZH"
        "Korea" -> "KO"
        "Jerman" -> "DE"
        "Prancis" -> "FR"
        else -> language.take(2).uppercase()
    }
}
