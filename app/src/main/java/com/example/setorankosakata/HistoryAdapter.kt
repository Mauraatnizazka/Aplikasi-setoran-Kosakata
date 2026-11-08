package com.example.setorankosakata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.setorankosakata.databinding.ItemHistoryEntryBinding
import com.example.setorankosakata.databinding.ItemHistoryHeaderBinding

/** Baris pada tab Riwayat: judul per hari, atau satu kosakata. */
sealed class HistoryRow {
    data class Header(val date: String, val count: Int, val target: Int) : HistoryRow()
    data class Entry(val vocab: Vocab) : HistoryRow()
}

private const val TYPE_HEADER = 0
private const val TYPE_ENTRY = 1

class HistoryAdapter(
    private val onEntryClick: (Vocab) -> Unit
) : ListAdapter<HistoryRow, RecyclerView.ViewHolder>(HistoryDiffCallback()) {

    class HeaderHolder(val binding: ItemHistoryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    class EntryHolder(val binding: ItemHistoryEntryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryRow.Header -> TYPE_HEADER
            is HistoryRow.Entry -> TYPE_ENTRY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderHolder(ItemHistoryHeaderBinding.inflate(inflater, parent, false))
        } else {
            EntryHolder(ItemHistoryEntryBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = getItem(position)
        if (row is HistoryRow.Header && holder is HeaderHolder) {
            val b = holder.binding
            val ctx = b.root.context
            b.tvHeaderDate.text = DateUtil.formatLong(row.date)
            b.tvHeaderCount.text = ctx.getString(R.string.count_format, row.count, row.target)
            if (row.count >= row.target) {
                b.tvHeaderStatus.text = ctx.getString(R.string.history_done)
                b.tvHeaderStatus.setBackgroundResource(R.drawable.bg_badge_success)
            } else {
                b.tvHeaderStatus.text = ctx.getString(R.string.history_short, row.target - row.count)
                b.tvHeaderStatus.setBackgroundResource(R.drawable.bg_badge_warn)
            }
        } else if (row is HistoryRow.Entry && holder is EntryHolder) {
            val b = holder.binding
            val vocab = row.vocab
            b.tvLangCode.text = LangUtil.code(vocab.language)
            b.tvWord.text = vocab.word
            b.tvMeaning.text = vocab.meaning
            b.ivMemorized.visibility = if (vocab.memorized) View.VISIBLE else View.GONE
            b.root.setOnClickListener { onEntryClick(vocab) }
        }
    }
}

class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryRow>() {
    override fun areItemsTheSame(oldItem: HistoryRow, newItem: HistoryRow): Boolean {
        return when {
            oldItem is HistoryRow.Header && newItem is HistoryRow.Header ->
                oldItem.date == newItem.date
            oldItem is HistoryRow.Entry && newItem is HistoryRow.Entry ->
                oldItem.vocab.id == newItem.vocab.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: HistoryRow, newItem: HistoryRow): Boolean {
        return oldItem == newItem
    }
}
