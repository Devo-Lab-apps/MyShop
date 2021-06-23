package com.labs.devo.apps.myshop.view.adapter.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.const.AppConstants
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.databinding.SingleItemViewBinding

class ItemListAdapter(val onItemClick: OnItemClick) :
    PagingDataAdapter<Item, ItemListAdapter.ItemViewHolder>(DiffCallback()) {

    private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

    inner class ItemViewHolder(private val binding: SingleItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.apply {
                itemName.text = item.itemName
                itemQuantity.text = item.quantity.toString()
                itemDate.text = item.createdAt.toString()
                if (item.imageUrl != null) {
                    Glide.with(binding.root).load(item.imageUrl)
                        .placeholder(R.drawable.user_image).into(binding.circleImageView)
                }
                val pos = bindingAdapterPosition
                val e = getItem(pos)
                if (pos != RecyclerView.NO_POSITION && e != null) {
                    binding.root.setOnClickListener {
                        onItemClick.onClick(e)
                    }
                }
            }
        }
    }

    interface OnItemClick {
        fun onClick(item: Item)
    }

    class DiffCallback : DiffUtil.ItemCallback<Item>() {

        private val TAG = AppConstants.APP_PREFIX + javaClass.simpleName

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            SingleItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }
}