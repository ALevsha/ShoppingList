package com.proCourse.shoppinglist.data

import com.proCourse.shoppinglist.domain.ShopItem
import com.proCourse.shoppinglist.domain.ShopListRepository
import java.lang.RuntimeException

/**
 * Класс является object, чтобы сделать класс SingleTone'ом,
 * т.е. обращение было к одному и тому же экземпляру. Чтобы обращаться к нему со всех экранов
 */

object ShopListRepositoryImpl: ShopListRepository {

    private val shopList = mutableListOf<ShopItem>()

    private var autoIncrementId = 0

    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID)
            shopItem.id = autoIncrementId++
        shopList.add(shopItem)
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItem(shopItem.id)
        shopList.remove(oldElement)
        addShopItem(shopItem)
    }

    /**
     * Если для приложения норм, что может прийти null, то можно сделать возвращаемый элемент
     * нуллабельным и обработать этот момент по-другому
     */
    override fun getShopItem(shopItemId: Int): ShopItem {
        return shopList.find { it.id == shopItemId
        } ?: throw RuntimeException("Element with id $shopItemId not found")
    }

    override fun getShopList(): List<ShopItem> {
        return shopList.toMutableList()
    }

}