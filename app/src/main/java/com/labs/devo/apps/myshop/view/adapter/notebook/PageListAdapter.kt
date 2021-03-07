package com.labs.devo.apps.myshop.view.adapter.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.PageItemViewBinding

class PageListAdapter(
    val onPageClick: OnPageClick,
    val onPageSettingsSettingsClick: OnPageSettingsClick
) :
    ListAdapter<Page, PageListAdapter.PageViewHolder>(DiffCallback()) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    inner class PageViewHolder(private val binding: PageItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: Page) {
            binding.apply {
                pageName.text = page.pageName
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    binding.pageSettings.setOnClickListener {
                        onPageSettingsSettingsClick.onPageSettingsClick(getItem(pos))
                    }
                    binding.root.setOnClickListener {
                        onPageClick.onClick(getItem(pos))
                    }
                }
            }
        }
    }

    interface OnPageSettingsClick {
        fun onPageSettingsClick(page: Page)
    }

    interface OnPageClick {
        fun onClick(page: Page)
    }

    class DiffCallback : DiffUtil.ItemCallback<Page>() {

        private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.pageId == newItem.pageId
        }

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return PageViewHolder(
            PageItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        val page = getItem(position)
        holder.bind(page)
    }


}