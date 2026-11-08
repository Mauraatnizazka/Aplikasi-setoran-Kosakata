package com.example.setorankosakata

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setorankosakata.databinding.FragmentTodayBinding
import com.example.setorankosakata.databinding.ItemDayBinding
import java.time.LocalDate

/** Tab "Hari Ini": target harian, progres, hari beruntun, dan daftar setoran hari ini. */
class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VocabViewModel
    private lateinit var prefs: PrefsManager
    private lateinit var adapter: VocabAdapter
    private var allVocabs: List<Vocab> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())
        viewModel = ViewModelProvider(requireActivity())[VocabViewModel::class.java]

        adapter = VocabAdapter(
            onToggleMemorized = { vocab ->
                viewModel.update(vocab.copy(memorized = !vocab.memorized))
            },
            onEditClick = { vocab ->
                val intent = Intent(requireContext(), AddEditVocabActivity::class.java)
                intent.putExtra(AddEditVocabActivity.EXTRA_ID, vocab.id)
                startActivity(intent)
            },
            onDeleteClick = { vocab -> showDeleteDialog(vocab) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.allVocabs.observe(viewLifecycleOwner) { vocabs ->
            allVocabs = vocabs ?: emptyList()
            render()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditVocabActivity::class.java))
        }
    }

    // Target harian dibaca ulang dari SharedPreferences setiap layar tampil kembali
    override fun onResume() {
        super.onResume()
        render()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render() {
        val b = _binding ?: return
        val ctx = requireContext()

        val target = prefs.getDailyTarget()
        val today = LocalDate.now()
        val todayText = today.toString()
        val todayList = allVocabs.filter { it.date == todayText }
        val count = todayList.size

        adapter.submitList(todayList)

        // Kartu progres
        b.tvDate.text = DateUtil.formatLong(today)
        b.tvProgressCount.text = getString(R.string.count_format, count, target)
        val percent = if (count >= target) 100 else count * 100 / target
        b.progressCircle.setProgressCompat(percent, true)
        b.tvPercent.text = getString(R.string.percent_format, percent)
        b.tvMotivation.text = when {
            count >= target -> getString(R.string.motivation_done)
            count == 0 -> getString(R.string.motivation_start, target)
            else -> getString(R.string.motivation_remaining, target - count)
        }

        // Statistik
        val counts = StudyStats.countsByDate(allVocabs)
        b.tvStreak.text = StudyStats.streak(counts, target, today).toString()
        b.tvTotal.text = allVocabs.size.toString()
        b.tvMemorized.text = allVocabs.count { it.memorized }.toString()

        // Strip 7 hari terakhir
        b.weekStrip.removeAllViews()
        val inflater = LayoutInflater.from(ctx)
        for (i in 6 downTo 0) {
            val day = today.minusDays(i.toLong())
            val n = counts[day.toString()] ?: 0
            val item = ItemDayBinding.inflate(inflater, b.weekStrip, false)
            item.tvDayName.text = DateUtil.dayShort(day)
            item.tvDayCount.text = n.toString()
            when {
                n >= target -> {
                    item.tvDayCount.setBackgroundResource(R.drawable.bg_day_done)
                    item.tvDayCount.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                }
                n > 0 -> {
                    item.tvDayCount.setBackgroundResource(R.drawable.bg_day_partial)
                    item.tvDayCount.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary))
                }
                else -> {
                    item.tvDayCount.setBackgroundResource(R.drawable.bg_day_empty)
                    item.tvDayCount.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
                }
            }
            if (i == 0) {
                item.tvDayName.setTypeface(null, Typeface.BOLD)
                item.tvDayName.setTextColor(ContextCompat.getColor(ctx, R.color.accent))
            }
            b.weekStrip.addView(item.root)
        }

        // Daftar / keadaan kosong
        if (todayList.isEmpty()) {
            b.emptyStateLayout.visibility = View.VISIBLE
            b.recyclerView.visibility = View.GONE
            b.tvEmptySubtitle.text = getString(R.string.empty_today_subtitle, target)
        } else {
            b.emptyStateLayout.visibility = View.GONE
            b.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showDeleteDialog(vocab: Vocab) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_vocab_title))
            .setMessage(getString(R.string.delete_vocab_message))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.delete(vocab)
            }
            .show()
    }
}
