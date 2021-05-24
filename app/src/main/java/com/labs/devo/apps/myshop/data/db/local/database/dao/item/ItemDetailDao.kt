package com.labs.devo.apps.myshop.data.db.local.database.dao.item

import androidx.paging.PagingSource
import androidx.room.*
import com.labs.devo.apps.myshop.data.models.item.ItemDetail

@Dao
interface ItemDetailDao {


    @Query("SELECT * FROM itemDetail")
    fun getItemDetails(): PagingSource<Int, ItemDetail>

    @Query("SELECT * FROM itemDetail WHERE itemDetailId = :itemDetailId")
    fun getItemDetailById(itemDetailId: String): ItemDetail?

    @Query("SELECT * FROM itemDetail WHERE itemId = :itemId")
    fun getItemDetail(itemId: String): ItemDetail?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createItemDetail(itemDetail: ItemDetail)

    @Update
    suspend fun updateItemDetail(itemDetail: ItemDetail)

    @Query("DELETE FROM itemDetail where itemId = :itemId")
    suspend fun deleteItemDetail(itemId: String)

    @Query("DELETE FROM itemDetail where itemDetailId = :itemDetailId")
    suspend fun deleteItemDetailById(itemDetailId: String)

    @Query("DELETE FROM itemDetail")
    suspend fun deleteItemDetails()

    @Query("SELECT * FROM itemDetail ORDER BY modifiedAt DESC limit 1")
    suspend fun getLastFetchedItemDetail(): ItemDetail?

}