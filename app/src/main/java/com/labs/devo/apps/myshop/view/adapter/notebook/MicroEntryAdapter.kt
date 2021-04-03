package com.labs.devo.apps.myshop.view.adapter.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Entry
import com.labs.devo.apps.myshop.databinding.MicroEntryItemBinding
import com.labs.devo.apps.myshop.view.adapter.entry.EntryListAdapter
import java.text.SimpleDateFormat
import java.util.*

class MicroEntryAdapter(val onEntryClick: OnMicroEntryClick) :
    PagingDataAdapter<Entry, MicroEntryAdapter.MicroEntryViewHolder>(EntryListAdapter.DiffCallback()) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ENGLISH)

    inner class MicroEntryViewHolder(private val binding: MicroEntryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: Entry) {
            binding.apply {
                microEntryCreatedAt.text = dateFormat.format(Date(entry.createdAt))
                microEntryModifedAt.text = dateFormat.format(Date(entry.modifiedAt))
                microEntryAmount.text = entry.entryAmount.toString()
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

    interface OnMicroEntryClick {
        fun onClick(entry: Entry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MicroEntryViewHolder {
        return MicroEntryViewHolder(
            MicroEntryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MicroEntryViewHolder, position: Int) {
        val entry = getItem(position)
        if (entry != null) {
            holder.bind(entry)
        }
    }
}