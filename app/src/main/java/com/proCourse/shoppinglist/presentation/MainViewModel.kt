package com.proCourse.shoppinglist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proCourse.shoppinglist.data.ShopListRepositoryImpl
import com.proCourse.shoppinglist.domain.DeleteShopItemUseCase
import com.proCourse.shoppinglist.domain.EditShopItemUseCase
import com.proCourse.shoppinglist.domain.GetShopListUseCase
import com.proCourse.shoppinglist.domain.ShopItem


/**
 * Наследуемся от ViewModel, т.к. контекст не будет использоваться, иначе наследуемся от AndroidViewModel()
 */
class MainViewModel: ViewModel() {

    private val repository = ShopListRepositoryImpl // неправильно, т.к. реализация зависит о data
                                                    // чего не должно быть, правильно будет
                                                    // с использованием инъекции зависимостей

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = MutableLiveData<List<ShopItem>>() // LiveData - хранилище данных,
                                                     // в который можно поместить какой либо объект
                                                     // и на него можно ПОДПИСАТЬСЯ для плдучения
                                                     // объектов, которые в него помещают

    fun getShopList(){// обновление списка
        // Взаимодействие activity и viewModel д.б через LiveData
        val  list = getShopListUseCase.getShopList()
        shopList.value = list // value(), postValue() - обращение к элементам:
                                                // value - из главного потока,
                                                // postValue - из любого потока
    }

    fun deleteShopItem(shopItem: ShopItem){
        deleteShopItemUseCase.deleteShopItem(shopItem)
        getShopList() // обновление списка
    }

    fun changeEnableState(shopItem: ShopItem){
        // у data class метод copy переопределен
        val newItem = shopItem.copy(enabled = !shopItem.enabled) // т.к. enabled val
        editShopItemUseCase.editShopItem(newItem)
        getShopList()
    }
}