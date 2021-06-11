package com.labs.devo.apps.myshop.data.util

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

object ViewModelUtil {

    suspend fun <T : Any> getFromPagingDataFlow(flow: Flow<PagingData<T>>): List<T> {
        val pagingData = flow.first()
        val adapter = MyAdapter<T>()
        adapter.submitData(pagingData)
        delay(100)
        return adapter.snapshot().items
    }

    fun <T> getDiffUtilCallback(): DiffUtil.ItemCallback<T> =
        object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                return newItem == oldItem
            }

        }

    class MyAdapter<T : Any>() :
        PagingDataAdapter<T, RecyclerView.ViewHolder>(getDiffUtilCallback()) {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object: RecyclerView.ViewHolder(parent.rootView) {

            }
        }



    }

}