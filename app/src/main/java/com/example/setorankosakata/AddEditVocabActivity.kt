package com.example.setorankosakata

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.setorankosakata.databinding.ActivityAddEditVocabBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class AddEditVocabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditVocabBinding
    private lateinit var viewModel: VocabViewModel
    private lateinit var prefs: PrefsManager

    private var vocabId: Int = -1
    private var currentVocab: Vocab? = null
    private var allVocabs: List<Vocab> = emptyList()

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditVocabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        prefs = PrefsManager(this)
        viewModel = ViewModelProvider(this)[VocabViewModel::class.java]

        // Daftar pilihan bahasa untuk dropdown
        val languages = resources.getStringArray(R.array.languages)
        binding.actLanguage.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, languages)
        )

        vocabId = intent.getIntExtra(EXTRA_ID, -1)

        if (vocabId != -1) {
            binding.toolbar.title = getString(R.string.edit_vocab)
            binding.infoCard.visibility = View.GONE
            binding.btnSaveNext.visibility = View.GONE
            loadVocab(vocabId)
        } else {
            binding.toolbar.title = getString(R.string.add_vocab)
            // Setoran baru: bahasa diisi dari SharedPreferences, tanggal diisi hari ini
            binding.actLanguage.setText(prefs.getLastLanguage(), false)
            binding.etDate.setText(DateUtil.today())

            // Hitung setoran ke berapa pada tanggal yang dipilih
            viewModel.allVocabs.observe(this) { vocabs ->
                allVocabs = vocabs ?: emptyList()
                updateInfo()
            }
            updateInfo()
        }

        binding.etDate.setOnClickListener { showDatePicker() }
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener { saveVocab(closeAfter = true) }
        binding.btnSaveNext.setOnClickListener { saveVocab(closeAfter = false) }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun updateInfo() {
        if (vocabId != -1) return
        val date = binding.etDate.text.toString().trim().ifEmpty { DateUtil.today() }
        val count = allVocabs.count { it.date == date }
        binding.tvInfo.text = getString(
            R.string.form_info, count + 1, DateUtil.formatLong(date), prefs.getDailyTarget()
        )
    }

    private fun loadVocab(id: Int) {
        lifecycleScope.launch {
            val vocab = viewModel.getVocabById(id)
            if (vocab != null) {
                currentVocab = vocab
                binding.etWord.setText(vocab.word)
                binding.etMeaning.setText(vocab.meaning)
                binding.actLanguage.setText(vocab.language, false)
                binding.etExample.setText(vocab.example)
                binding.etDate.setText(vocab.date)
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = String.format(
                Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay
            )
            binding.etDate.setText(date)
            updateInfo()
        }, year, month, day).show()
    }

    private fun saveVocab(closeAfter: Boolean) {
        val word = binding.etWord.text.toString().trim()
        val meaning = binding.etMeaning.text.toString().trim()
        val language = binding.actLanguage.text.toString().trim()
            .ifEmpty { PrefsManager.DEFAULT_LANGUAGE }
        val example = binding.etExample.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
            .ifEmpty { LocalDate.now().toString() }

        var valid = true
        if (word.isEmpty()) {
            binding.tilWord.error = getString(R.string.word_required)
            valid = false
        } else {
            binding.tilWord.error = null
        }
        if (meaning.isEmpty()) {
            binding.tilMeaning.error = getString(R.string.meaning_required)
            valid = false
        } else {
            binding.tilMeaning.error = null
        }
        if (!valid) return

        // Ingat bahasa terakhir yang dipakai (SharedPreferences, apply())
        prefs.setLastLanguage(language)

        val existing = currentVocab
        if (vocabId != -1 && existing != null) {
            val updated = existing.copy(
                word = word,
                meaning = meaning,
                language = language,
                example = example,
                date = date
            )
            viewModel.update(updated)
            Snackbar.make(binding.root, getString(R.string.vocab_updated), Snackbar.LENGTH_SHORT).show()
        } else {
            val newVocab = Vocab(
                word = word,
                meaning = meaning,
                language = language,
                example = example,
                date = date,
                memorized = false
            )
            viewModel.insert(newVocab)
            if (!closeAfter) {
                // Kosongkan isian, tetap di layar untuk setoran berikutnya
                binding.etWord.setText("")
                binding.etMeaning.setText("")
                binding.etExample.setText("")
                binding.etWord.requestFocus()
                Snackbar.make(binding.root, getString(R.string.vocab_saved_next), Snackbar.LENGTH_SHORT).show()
                return
            }
            Snackbar.make(binding.root, getString(R.string.vocab_added), Snackbar.LENGTH_SHORT).show()
        }

        binding.root.postDelayed({ finish() }, 400)
    }
}
