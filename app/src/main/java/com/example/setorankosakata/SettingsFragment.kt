package com.example.setorankosakata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.setorankosakata.databinding.FragmentSettingsBinding

/** Tab "Pengaturan": semua pilihan di sini disimpan dengan SharedPreferences (PrefsManager). */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: PrefsManager
    private var updating = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())

        // Mode gelap
        binding.switchDark.setOnCheckedChangeListener { _, checked ->
            if (updating) return@setOnCheckedChangeListener
            prefs.setDarkMode(checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Target harian
        binding.btnMinus.setOnClickListener {
            prefs.setDailyTarget(prefs.getDailyTarget() - 1)
            refresh()
        }
        binding.btnPlus.setOnClickListener {
            prefs.setDailyTarget(prefs.getDailyTarget() + 1)
            refresh()
        }

        // Bahasa default
        val languages = resources.getStringArray(R.array.languages)
        binding.actLanguage.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, languages)
        )
        binding.actLanguage.setOnItemClickListener { parent, _, position, _ ->
            prefs.setLastLanguage(parent.getItemAtPosition(position).toString())
            refresh()
        }
    }

    // Nilai pengaturan dibaca ulang setiap layar tampil kembali
    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refresh() {
        val b = _binding ?: return

        updating = true
        b.switchDark.isChecked = prefs.isDarkMode()
        updating = false

        b.tvTarget.text = prefs.getDailyTarget().toString()
        b.actLanguage.setText(prefs.getLastLanguage(), false)
    }
}
