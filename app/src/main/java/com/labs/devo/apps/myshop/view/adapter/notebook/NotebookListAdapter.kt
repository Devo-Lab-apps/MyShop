package com.labs.devo.apps.myshop.view.adapter.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Notebook
import com.labs.devo.apps.myshop.databinding.NotebookItemViewBinding

class NotebookListAdapter(
    val onNotebookClick: OnNotebookClick,
    val onNotebookSettingsClick: OnNotebookSettingsClick
) :
    ListAdapter<Notebook, NotebookListAdapter.NotebookViewHolder>(DiffCallback()) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    inner class NotebookViewHolder(private val binding: NotebookItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notebook: Notebook) {
            binding.apply {
                notebookName.text = notebook.notebookName
                val pos = adapterPosition
                if (pos != NO_POSITION) {
                    binding.notebookSettings.setOnClickListener {
                        onNotebookSettingsClick.onClick(getItem(pos))
                    }
                    binding.root.setOnClickListener {
                        onNotebookClick.onClick(getItem(pos))
                    }
                }
            }
        }
    }

    interface OnNotebookSettingsClick {
        fun onClick(notebook: Notebook)
    }

    interface OnNotebookClick {
        fun onClick(notebook: Notebook)
    }

    class DiffCallback : DiffUtil.ItemCallback<Notebook>() {

        private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

        override fun areItemsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
            return oldItem.notebookId == newItem.notebookId
        }

        override fun areContentsTheSame(oldItem: Notebook, newItem: Notebook): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookViewHolder {
        return NotebookViewHolder(
            NotebookItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotebookViewHolder, position: Int) {
        val notebook = getItem(position)
        holder.bind(notebook)
    }


}