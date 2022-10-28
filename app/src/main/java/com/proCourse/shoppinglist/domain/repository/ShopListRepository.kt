package com.proCourse.shoppinglist.domain.repository

import androidx.lifecycle.LiveData
import com.proCourse.shoppinglist.domain.model.ShopItem

/**
 * Модификатор suspend является базовым средством языка Kotlin => м.б без проблем применен
 * в domain-слое ероме тех случаев, которые возвращают объект LiveData, который в свою очередь,
 * уже выполняется в другом потоке
 */

interface ShopListRepository {

    suspend fun addShopItem(shopItem: ShopItem)

    suspend fun deleteShopItem(shopItem: ShopItem)

    suspend fun editShopItem(shopItem: ShopItem)

    suspend fun getShopItem(shopItemId: Int): ShopItem

    fun getShopList(): LiveData<List<ShopItem>>
}