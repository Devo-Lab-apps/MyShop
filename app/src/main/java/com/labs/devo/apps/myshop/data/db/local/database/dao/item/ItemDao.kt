package com.labs.devo.apps.myshop.data.db.local.database.dao.item

import androidx.paging.PagingSource
import androidx.room.*
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.notebook.Notebook

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getItems(): PagingSource<Int, Item>

    @Query("SELECT * FROM item WHERE itemId = :itemId")
    fun getItem(itemId: String): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<Item>)

    @Update
    suspend fun updateItem(item: Item)

    @Query("DELETE FROM item where itemId = :itemId")
    suspend fun deleteItem(itemId: String)

    @Query("DELETE FROM item")
    suspend fun deleteItems()

    @Query("SELECT * FROM item ORDER BY modifiedAt DESC limit 1")
    suspend fun getLastFetchedItem(): Item?

}