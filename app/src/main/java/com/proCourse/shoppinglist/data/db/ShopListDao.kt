package com.proCourse.shoppinglist.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * В объекте Dao мы будем описывать методы для работы с базой данных.
 * Описываем их в интерфейсе с аннотацией Dao.
 */
@Dao
interface ShopListDao {

    /**
     *  запрос получение всей таблицы
     */
    @Query("SELECT * FROM shop_items")
    fun getShopList(): LiveData<List<ShopItemDBModel>>

    /**
     * добавление элемента, где, если есть коллизия по первичному ключу, заменить
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addShopItem(shopItemDBModel: ShopItemDBModel)

    /**
     * удаление из списка элементов, где первичный ключ (id) = id
     */
    @Query("DELETE FROM shop_items WHERE id=:shopItemId")
    fun deleteShopItem(shopItemId: Int)

    /**
     * получение элемента из таблицы, где первичный ключ (id) = id, но не более 1 элемента
     */
    @Query("SELECT * FROM shop_items WHERE id=:shopItemId LIMIT 1")
    fun getShopItem(shopItemId: Int): ShopItemDBModel
}