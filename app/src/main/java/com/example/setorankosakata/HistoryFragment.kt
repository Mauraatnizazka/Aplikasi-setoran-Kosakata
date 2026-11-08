package com.example.setorankosakata

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.setorankosakata.databinding.FragmentHistoryBinding

/** Tab "Riwayat": seluruh setoran dikelompokkan per hari, lengkap dengan status target. */
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VocabViewModel
    private lateinit var prefs: PrefsManager
    private lateinit var adapter: HistoryAdapter
    private var allVocabs: List<Vocab> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())
        viewModel = ViewModelProvider(requireActivity())[VocabViewModel::class.java]

        adapter = HistoryAdapter { vocab ->
            val intent = Intent(requireContext(), AddEditVocabActivity::class.java)
            intent.putExtra(AddEditVocabActivity.EXTRA_ID, vocab.id)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.allVocabs.observe(viewLifecycleOwner) { vocabs ->
            allVocabs = vocabs ?: emptyList()
            render()
        }
    }

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
        val target = prefs.getDailyTarget()

        val rows = mutableListOf<HistoryRow>()
        val grouped = allVocabs.groupBy { it.date }
        val sortedDates = grouped.keys.sortedDescending()
        for (date in sortedDates) {
            val items = grouped[date] ?: emptyList()
            rows.add(HistoryRow.Header(date, items.size, target))
            for (item in items) {
                rows.add(HistoryRow.Entry(item))
            }
        }
        adapter.submitList(rows)

        b.tvHistorySubtitle.text =
            getString(R.string.history_summary, sortedDates.size, allVocabs.size)

        if (rows.isEmpty()) {
            b.emptyStateLayout.visibility = View.VISIBLE
            b.recyclerView.visibility = View.GONE
        } else {
            b.emptyStateLayout.visibility = View.GONE
            b.recyclerView.visibility = View.VISIBLE
        }
    }
}
