package com.labs.devo.apps.myshop.view.adapter.notebook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.const.AppConstants.DATE_FORMAT
import com.labs.devo.apps.myshop.data.models.notebook.Page
import com.labs.devo.apps.myshop.databinding.PageItemViewBinding
import java.text.SimpleDateFormat
import java.util.*

class PageListAdapter(
    val onPageClick: OnPageClick,
    val onPageSettingsSettingsClick: OnPageSettingsClick
) :
    PagingDataAdapter<Page, PageListAdapter.PageViewHolder>(DiffCallback()) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    inner class PageViewHolder(private val binding: PageItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: Page) {
            binding.apply {
                pageName.text = page.pageName
                val pos = bindingAdapterPosition
                val p = getItem(pos)
                if (pos != RecyclerView.NO_POSITION && p != null) {
                    binding.pageSettings.setOnClickListener {
                        onPageSettingsSettingsClick.onPageSettingsClick(p)
                    }
                    pageModifiedAt.text = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(Date(p.modifiedAt))
                    binding.root.setOnClickListener {
                        onPageClick.onClick(p)
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
        if (page != null)
            holder.bind(page)
    }


}