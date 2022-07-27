package com.proCourse.shoppinglist.domain.usecase

import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.repository.ShopListRepository

class GetShopItemUseCase(private val shopListRepository: ShopListRepository) {
    fun getShopItem(shopItemId: Int): ShopItem {
        return shopListRepository.getShopItem(shopItemId)
        }
}