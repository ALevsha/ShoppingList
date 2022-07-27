package com.proCourse.shoppinglist.domain.usecase

import androidx.lifecycle.LiveData
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.repository.ShopListRepository

class GetShopListUseCase(private val shopListRepository: ShopListRepository) {
    fun getShopList(): LiveData<List<ShopItem>>{
        return  shopListRepository.getShopList()

    }
}