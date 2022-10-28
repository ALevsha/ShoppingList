package com.proCourse.shoppinglist.domain.usecase

import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.repository.ShopListRepository

class AddShopItemUseCase(private val shopListRepository: ShopListRepository) {
    suspend fun addShopItem(shopItem: ShopItem){
        shopListRepository.addShopItem(shopItem)
    }
}