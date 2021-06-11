package com.labs.devo.apps.myshop.data.db.local.database.dao.item

import androidx.paging.PagingSource
import androidx.room.*
import com.labs.devo.apps.myshop.data.models.item.Item

@Dao
interface ItemDao {

    fun getItems(searchQuery: String, orderBy: String): PagingSource<Int, Item> =
        when (orderBy) {
            Item::itemName.name -> {
                getItemInNameOrder(searchQuery)
            }
            else -> {
                getItemInDateOrder(searchQuery)
            }
        }

    @Query("SELECT * FROM item where itemName LIKE :searchQuery order by itemName ASC")
    fun getItemInDateOrder(searchQuery: String): PagingSource<Int, Item>

    @Query("SELECT * FROM item where itemName LIKE :searchQuery order by modifiedAt ASC")
    fun getItemInNameOrder(searchQuery: String): PagingSource<Int, Item>

    @Query("SELECT * FROM item WHERE itemId = :itemId")
    fun getItem(itemId: String): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createItems(items: List<Item>)

    @Update
    suspend fun updateItem(item: Item)

    @Query("DELETE FROM item where itemId = :itemId")
    suspend fun deleteItem(itemId: String)

    @Query("DELETE FROM item")
    suspend fun deleteItems()

    @Query("SELECT * FROM item ORDER BY modifiedAt DESC limit 1")
    suspend fun getLastFetchedItem(): Item?

}