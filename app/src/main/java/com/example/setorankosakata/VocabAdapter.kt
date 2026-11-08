package com.example.setorankosakata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.setorankosakata.databinding.ItemVocabBinding

/** Adapter daftar setoran pada tab "Hari Ini". */
class VocabAdapter(
    private val onToggleMemorized: (Vocab) -> Unit,
    private val onEditClick: (Vocab) -> Unit,
    private val onDeleteClick: (Vocab) -> Unit
) : ListAdapter<Vocab, VocabAdapter.VocabViewHolder>(VocabDiffCallback()) {

    inner class VocabViewHolder(val binding: ItemVocabBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabViewHolder {
        val binding = ItemVocabBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VocabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VocabViewHolder, position: Int) {
        val vocab = getItem(position)
        val b = holder.binding
        val ctx = b.root.context

        b.tvLangCode.text = LangUtil.code(vocab.language)
        b.tvWord.text = vocab.word
        b.tvMeaning.text = vocab.meaning
        b.tvLanguage.text = vocab.language

        if (vocab.example.isEmpty()) {
            b.tvExample.visibility = View.GONE
        } else {
            b.tvExample.visibility = View.VISIBLE
            b.tvExample.text = vocab.example
        }

        if (vocab.memorized) {
            b.btnMemorize.setImageResource(R.drawable.ic_check_filled)
            b.btnMemorize.setColorFilter(ContextCompat.getColor(ctx, R.color.success))
            b.tvStatus.visibility = View.VISIBLE
        } else {
            b.btnMemorize.setImageResource(R.drawable.ic_check_outline)
            b.btnMemorize.setColorFilter(ContextCompat.getColor(ctx, R.color.text_secondary))
            b.tvStatus.visibility = View.GONE
        }

        b.btnMemorize.setOnClickListener { onToggleMemorized(vocab) }
        b.root.setOnClickListener { onEditClick(vocab) }

        b.btnMore.setOnClickListener { anchor ->
            val popup = PopupMenu(anchor.context, anchor)
            popup.menuInflater.inflate(R.menu.vocab_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEditClick(vocab)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(vocab)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}

class VocabDiffCallback : DiffUtil.ItemCallback<Vocab>() {
    override fun areItemsTheSame(oldItem: Vocab, newItem: Vocab): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Vocab, newItem: Vocab): Boolean {
        return oldItem == newItem
    }
}
