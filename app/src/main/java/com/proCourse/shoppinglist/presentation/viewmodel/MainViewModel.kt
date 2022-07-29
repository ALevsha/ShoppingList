package com.proCourse.shoppinglist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.proCourse.shoppinglist.data.ShopListRepositoryImpl
import com.proCourse.shoppinglist.domain.usecase.DeleteShopItemUseCase
import com.proCourse.shoppinglist.domain.usecase.GetShopListUseCase
import com.proCourse.shoppinglist.domain.model.ShopItem
import com.proCourse.shoppinglist.domain.usecase.EditShopItemUseCase


/**
 * Наследуемся от ViewModel, т.к. контекст не будет использоваться, иначе наследуемся от AndroidViewModel(application)
 */
class MainViewModel: ViewModel() {

    private val repository = ShopListRepositoryImpl // неправильно, т.к. реализация зависит о data
                                                    // чего не должно быть, правильно будет
                                                    // с использованием инъекции зависимостей
                                                    // Автор считает, что presentation и data
                                                    // ничего не должны знать друг о друге

    // добавляем 3 useCase
    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList() // LiveData - хранилище данных,
                                                     // в который можно поместить какой либо объект
                                                     // и на него можно ПОДПИСАТЬСЯ для получения
                                                     // объектов, которые в него помещают


    fun deleteShopItem(shopItem: ShopItem){
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun changeEnableState(shopItem: ShopItem){
        // у data class метод copy переопределен
        val newItem = shopItem.copy(enabled = !shopItem.enabled) // т.к. enabled val
        editShopItemUseCase.editShopItem(newItem)
    }
}