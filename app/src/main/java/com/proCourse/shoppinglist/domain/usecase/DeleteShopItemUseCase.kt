package com.proCourse.shoppinglist.domain.usecase

import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.repository.ShopListRepository

class DeleteShopItemUseCase(private val shopListRepository: ShopListRepository) {
    fun deleteShopItem(shopItem: ShopItem){
        shopListRepository.deleteShopItem(shopItem)
    }
}