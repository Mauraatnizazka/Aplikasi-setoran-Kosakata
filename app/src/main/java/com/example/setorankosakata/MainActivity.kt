package com.example.setorankosakata

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.setorankosakata.databinding.ActivityMainBinding

/**
 * Activity utama: wadah tiga tab (Hari Ini, Riwayat, Pengaturan)
 * dengan navigasi di bagian bawah.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_today
            showTab(R.id.nav_today)
        } else {
            // Setelah Activity dibuat ulang (mis. ganti mode gelap), pulihkan tab terakhir
            binding.bottomNav.selectedItemId =
                savedInstanceState.getInt(KEY_TAB, R.id.nav_today)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            showTab(item.itemId)
            true
        }
    }

    // Pengaturan dibaca ulang setiap Activity tampil kembali
    override fun onResume() {
        super.onResume()
        val prefs = PrefsManager(this)

        val wanted = if (prefs.isDarkMode())
            AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (AppCompatDelegate.getDefaultNightMode() != wanted) {
            AppCompatDelegate.setDefaultNightMode(wanted)
        }

        // Catat nilai yang terbaca ke Logcat (filter tag: SetoranPrefs) sebagai bukti
        Log.d(
            TAG,
            "onResume -> ${PrefsManager.KEY_DARK_MODE}=${prefs.isDarkMode()}, " +
                "${PrefsManager.KEY_DAILY_TARGET}=${prefs.getDailyTarget()}, " +
                "${PrefsManager.KEY_LAST_LANGUAGE}=${prefs.getLastLanguage()}"
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TAB, binding.bottomNav.selectedItemId)
    }

    private fun showTab(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.nav_history -> HistoryFragment()
            R.id.nav_settings -> SettingsFragment()
            else -> TodayFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    companion object {
        private const val KEY_TAB = "key_tab"
        private const val TAG = "SetoranPrefs"
    }
}
