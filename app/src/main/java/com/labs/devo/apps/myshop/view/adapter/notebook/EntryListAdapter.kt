package com.labs.devo.apps.myshop.view.adapter.entry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.databinding.EntryItemBinding

class EntryListAdapter(val onEntryClick: OnEntryClick) :
    PagingDataAdapter<Entry, EntryListAdapter.EntryViewHolder>(DiffCallback()) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    inner class EntryViewHolder(private val binding: EntryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Entry) {
            binding.apply {
                entryTitle.text = entry.entryTitle
                entryAmount.text = entry.entryAmount.toString()
                entryDate.text = entry.entryCreatedAt
                val pos = bindingAdapterPosition
                val e = getItem(pos)
                if (pos != RecyclerView.NO_POSITION && e != null) {
                    binding.root.setOnClickListener {
                        onEntryClick.onClick(e)
                    }
                }
            }
        }
    }

    interface OnEntryClick {
        fun onClick(entry: Entry)
    }

    class DiffCallback : DiffUtil.ItemCallback<Entry>() {

        private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

        override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.entryId == newItem.entryId
        }

        override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder(
            EntryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = getItem(position)
        if (entry != null) {
            holder.bind(entry)
        }
    }
}