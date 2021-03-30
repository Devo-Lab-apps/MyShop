package com.labs.devo.apps.myshop.view.adapter.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.databinding.RecurringEntryItemBinding

class RecurringEntryListAdapter(val onRecurringEntryClick: OnRecurringEntryClick) :
    ListAdapter<RecurringEntry, RecurringEntryListAdapter.RecurringEntryViewHolder>(DiffCallback()) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    inner class RecurringEntryViewHolder(private val binding: RecurringEntryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: RecurringEntry) {
            binding.apply {
                recurringEntryName.text = entry.name
            }

            binding.root.setOnClickListener {
                onRecurringEntryClick.onClick(entry.recurringEntryId)
            }
        }
    }

    interface OnRecurringEntryClick {
        fun onClick(recurringEntryId: String)
    }


    class DiffCallback : DiffUtil.ItemCallback<RecurringEntry>() {

        private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

        override fun areItemsTheSame(oldItem: RecurringEntry, newItem: RecurringEntry): Boolean {
            return oldItem.recurringEntryId == newItem.recurringEntryId
        }

        override fun areContentsTheSame(oldItem: RecurringEntry, newItem: RecurringEntry): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecurringEntryViewHolder {
        return RecurringEntryViewHolder(
            RecurringEntryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecurringEntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}