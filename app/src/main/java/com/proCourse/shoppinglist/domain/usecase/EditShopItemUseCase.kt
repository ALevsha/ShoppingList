package com.proCourse.shoppinglist.domain.usecase

import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.repository.ShopListRepository

class EditShopItemUseCase(private val shopListRepository: ShopListRepository) {
    suspend fun editShopItem(shopItem: ShopItem){
        shopListRepository.editShopItem(shopItem)
    }
}