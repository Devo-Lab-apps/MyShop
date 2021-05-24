package com.labs.devo.apps.myshop.data.db.local.database.dao.item.util

import androidx.paging.PagingSource

object DaoUtil {

    suspend fun <T : Any> getPagingSource(pagingSource: PagingSource<Int, T>): List<T>? {
        val data = pagingSource.load(PagingSource.LoadParams.Refresh(1, 5, false))
        return (data as? PagingSource.LoadResult.Page)?.data
    }

}